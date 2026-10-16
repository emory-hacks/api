package com.example.demo.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpoPushService {
    private static final Logger log = LoggerFactory.getLogger(ExpoPushService.class);
    private static final int BATCH_SIZE = 100;
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final PushTokenRepository pushTokenRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    public ExpoPushService(PushTokenRepository pushTokenRepository, ObjectMapper objectMapper) {
        this.pushTokenRepository = pushTokenRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    public void notifyAnnouncement(String title, String body) {
        try {
            List<String> tokens = pushTokenRepository.findAll().stream()
                    .map(PushToken::getToken)
                    .filter(token -> token != null && !token.isBlank())
                    .toList();
            if (tokens.isEmpty()) {
                log.info("Announcement saved with no push tokens to notify");
                return;
            }

            String notificationTitle = title == null || title.isBlank() ? "Announcement" : title.trim();
            String notificationBody = preview(body);
            log.info("Sending announcement push to {} device(s)", tokens.size());

            for (int start = 0; start < tokens.size(); start += BATCH_SIZE) {
                List<String> batch = tokens.subList(start, Math.min(start + BATCH_SIZE, tokens.size()));
                sendBatch(batch, notificationTitle, notificationBody);
            }
        } catch (Exception exception) {
            log.warn("Failed to send announcement push notification", exception);
        }
    }

    private void sendBatch(List<String> batch, String title, String body) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (String token : batch) {
            Map<String, String> message = new LinkedHashMap<>();
            message.put("to", token);
            message.put("title", title);
            message.put("body", body);
            message.put("sound", "default");
            messages.add(message);
        }

        try {
            String responseBody = restClient.post()
                    .uri(EXPO_PUSH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(messages))
                    .retrieve()
                    .body(String.class);
            removeStaleTokens(batch, responseBody);
        } catch (RestClientResponseException exception) {
            log.warn("Expo push request failed: {}", exception.getResponseBodyAsString());
            removeStaleTokens(batch, exception.getResponseBodyAsString());
        } catch (Exception exception) {
            log.warn("Expo push request failed", exception);
        }
    }

    private void removeStaleTokens(List<String> batch, String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return;
        }

        try {
            JsonNode data = objectMapper.readTree(responseBody).path("data");
            if (!data.isArray()) {
                return;
            }

            for (int index = 0; index < data.size() && index < batch.size(); index++) {
                String error = data.get(index).path("details").path("error").asText("");
                if ("DeviceNotRegistered".equals(error)) {
                    pushTokenRepository.deleteByToken(batch.get(index));
                }
            }
        } catch (Exception exception) {
            log.warn("Could not parse Expo push response", exception);
        }
    }

    private static String preview(String content) {
        if (content == null || content.isBlank()) {
            return "New announcement";
        }

        String trimmed = content.trim();
        if (trimmed.length() <= 180) {
            return trimmed;
        }
        return trimmed.substring(0, 177) + "...";
    }
}
