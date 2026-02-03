package com.example.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;
	private final Environment env;

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		boolean isDemoProfile = Arrays.asList(env.getActiveProfiles()).contains("demo");

		http.csrf(csrf -> {
			csrf.disable();
		}).cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())

				.headers(h -> {
					if (isDemoProfile) {
						h.frameOptions(f -> f.sameOrigin());
					}
				})

				.exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, authEx) -> {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					res.setContentType("application/json");
					res.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Token mancante o non valido\"}");
				}).accessDeniedHandler((req, res, deniedEx) -> {
					res.setStatus(HttpServletResponse.SC_FORBIDDEN);
					res.setContentType("application/json");
					res.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Permessi insufficienti\"}");
				}))

				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						.requestMatchers("/error").permitAll()

						.requestMatchers("/api/auth/**").permitAll().requestMatchers("/api/public/**").permitAll()

						.requestMatchers("/h2-console/**")
						.access((authentication, context) -> isDemoProfile
								? new org.springframework.security.authorization.AuthorizationDecision(true)
								: new org.springframework.security.authorization.AuthorizationDecision(false))

						.requestMatchers("/api/persona/**").hasRole("ADMIN")

						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						.requestMatchers("/api/me/**").hasAnyRole("USER", "ADMIN")

						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
