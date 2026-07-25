package com.example.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Uses Render's DATABASE_URL when present; otherwise falls back to spring.datasource.* (local).
 */
@Configuration
public class DataSourceConfig {

	@Bean
	@Primary
	public DataSource dataSource(Environment env) {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (StringUtils.hasText(databaseUrl)) {
			if (databaseUrl.startsWith("jdbc:")) {
				return DataSourceBuilder.create()
						.url(ensureSsl(databaseUrl))
						.username(env.getProperty("spring.datasource.username", "postgres"))
						.password(env.getProperty("spring.datasource.password", ""))
						.driverClassName("org.postgresql.Driver")
						.build();
			}

			if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
				URI uri = URI.create(databaseUrl);
				String userInfo = uri.getUserInfo();
				if (StringUtils.hasText(userInfo)) {
					String[] parts = userInfo.split(":", 2);
					return DataSourceBuilder.create()
							.url(toJdbcUrl(uri))
							.username(urlDecode(parts[0]))
							.password(parts.length > 1 ? urlDecode(parts[1]) : "")
							.driverClassName("org.postgresql.Driver")
							.build();
				}
			}
		}

		return DataSourceBuilder.create()
				.url(env.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/postgres"))
				.username(env.getProperty("spring.datasource.username", "postgres"))
				.password(env.getProperty("spring.datasource.password", ""))
				.driverClassName("org.postgresql.Driver")
				.build();
	}

	private static String toJdbcUrl(URI uri) {
		StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
				.append(uri.getHost());
		if (uri.getPort() != -1) {
			jdbcUrl.append(':').append(uri.getPort());
		}
		jdbcUrl.append(uri.getPath());

		String query = uri.getQuery();
		if (StringUtils.hasText(query)) {
			jdbcUrl.append('?').append(query);
			if (!query.contains("sslmode")) {
				jdbcUrl.append("&sslmode=require");
			}
		} else {
			jdbcUrl.append("?sslmode=require");
		}
		return jdbcUrl.toString();
	}

	private static String ensureSsl(String jdbcUrl) {
		if (jdbcUrl.contains("sslmode=")) {
			return jdbcUrl;
		}
		return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
	}

	private static String urlDecode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
