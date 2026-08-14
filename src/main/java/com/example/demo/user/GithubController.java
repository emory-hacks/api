package com.example.demo.user;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class GithubController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${github.client-id:Ov23liDte2Yqe8TEMUIA}")
    private String clientId;

    @Value("${github.client-secret:}")
    private String clientSecret;

    public GithubController(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/github-access-token")
    public ResponseEntity<?> githubAccessToken(@RequestParam String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/auth/github/login")
    public ResponseEntity<?> githubLogin(@RequestBody GithubAuthRequest request, HttpServletResponse response) {
        if (request.getAccessToken() == null || request.getAccessToken().isBlank()) {
            return ResponseEntity.badRequest().body("Missing accessToken in body. Use {\"accessToken\":\"gho_...\"}");
        }

        Map<String, Object> githubUser = fetchGithubUser(request.getAccessToken());
        if (githubUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid GitHub access token");
        }
        if (githubUser.get("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unable to fetch GitHub user email. Ensure the token has user:email scope and the account has a verified email.");
        }

        String email = githubUser.get("email").toString();
        if (email.toLowerCase().endsWith(".edu")) {
            return ResponseEntity.badRequest().body("Error: .edu emails are not allowed for GitHub authentication");
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No account for this GitHub user");
        }

        String jwt = jwtUtils.generateToken(user.getEmail(), user.getRoles());
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("Logged in successfully!", jwt, user.getEmail()));
    }

    @PostMapping("/api/users/register/github")
    public ResponseEntity<?> githubRegister(@RequestBody GithubAuthRequest request) {
        Map<String, Object> githubUser = fetchGithubUser(request.getAccessToken());
        if (githubUser == null || githubUser.get("email") == null) {
            return ResponseEntity.badRequest().body("Unable to fetch GitHub user email");
        }

        String email = githubUser.get("email").toString();
        if (email.toLowerCase().endsWith(".edu")) {
            return ResponseEntity.badRequest().body("Error: .edu emails are not allowed for GitHub authentication");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email already taken!");
        }

        User user = new User();
        user.setEmail(email);
        Object name = githubUser.get("name");
        if (name == null || name.toString().isBlank()) {
            name = githubUser.get("login");
        }
        user.setName(name != null ? name.toString() : email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole("participant");
        user.setTeamName("no team");
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully.");
    }

    private Map<String, Object> fetchGithubUser(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return null;
        }

        HttpHeaders headers = githubHeaders(accessToken);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        Map<String, Object> user = userResponse.getBody();
        if (user == null) {
            return null;
        }

        if (user.get("email") == null || user.get("email").toString().toLowerCase().endsWith(".edu")) {
            String email = fetchGithubEmail(headers);
            if (email != null) {
                user.put("email", email);
            }
        }

        return user;
    }

    private String fetchGithubEmail(HttpHeaders headers) {
        ResponseEntity<List> emailsResponse = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );
        List<?> emails = emailsResponse.getBody();
        if (emails == null) {
            return null;
        }

        String verifiedFallback = null;
        String anyFallback = null;
        for (Object item : emails) {
            if (!(item instanceof Map<?, ?> emailEntry)) {
                continue;
            }
            Object email = emailEntry.get("email");
            if (email == null || email.toString().toLowerCase().endsWith(".edu")) {
                continue;
            }
            boolean primary = Boolean.TRUE.equals(emailEntry.get("primary"));
            boolean verified = Boolean.TRUE.equals(emailEntry.get("verified"));
            if (primary && verified) {
                return email.toString();
            }
            if (verified && verifiedFallback == null) {
                verifiedFallback = email.toString();
            }
            if (anyFallback == null) {
                anyFallback = email.toString();
            }
        }
        return verifiedFallback != null ? verifiedFallback : anyFallback;
    }

    private HttpHeaders githubHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "emory-hacks-api");
        return headers;
    }
}
