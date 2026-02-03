package com.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMs;

	public JwtService(@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(String username, String role) {
		return Jwts.builder().claims(Map.of("role", role)).subject(username).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationMs)).signWith(key).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public String extractRole(String token) {
		Object role = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role");
		return role != null ? role.toString() : null;
	}
}
