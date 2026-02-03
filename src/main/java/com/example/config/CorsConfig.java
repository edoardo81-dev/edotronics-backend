package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter(@Value("${app.cors.allowed-origins}") String originsCsv) {

		List<String> origins = Arrays.stream(originsCsv.split(",")).map(String::trim).filter(s -> !s.isBlank())
				.toList();

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(origins);

		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		config.setExposedHeaders(List.of("Authorization"));

		config.setAllowCredentials(true);

		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
