package com.mx.cbtis.gestor.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration driven by the {@code CORS_ALLOWED_ORIGINS} property.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${cors.allowed-origins:*}")
	private String allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String[] origins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toArray(String[]::new);

		registry.addMapping("/**")
				.allowedOriginPatterns(origins.length > 0 ? origins : new String[] { "*" })
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false)
				.maxAge(3600);
	}
}
