package com.example.demo.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts Render/Heroku-style DATABASE_URL (postgres://...) into Spring datasource properties.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RenderDatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String databaseUrl = environment.getProperty("DATABASE_URL");
		if (databaseUrl == null || databaseUrl.isBlank()) {
			return;
		}
		if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) {
			return;
		}

		URI uri = URI.create(databaseUrl);
		String userInfo = uri.getUserInfo();
		if (userInfo == null || userInfo.isBlank()) {
			return;
		}

		String[] parts = userInfo.split(":", 2);
		String username = urlDecode(parts[0]);
		String password = parts.length > 1 ? urlDecode(parts[1]) : "";

		StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
				.append(uri.getHost());
		if (uri.getPort() != -1) {
			jdbcUrl.append(':').append(uri.getPort());
		}
		jdbcUrl.append(uri.getPath());

		String query = uri.getQuery();
		if (query != null && !query.isBlank()) {
			jdbcUrl.append('?').append(query);
			if (!query.contains("sslmode")) {
				jdbcUrl.append("&sslmode=require");
			}
		} else {
			jdbcUrl.append("?sslmode=require");
		}

		Map<String, Object> props = new HashMap<>();
		props.put("spring.datasource.url", jdbcUrl.toString());
		props.put("spring.datasource.username", username);
		props.put("spring.datasource.password", password);

		environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", props));
	}

	private static String urlDecode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
