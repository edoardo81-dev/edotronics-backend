package com.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String auth = req.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}

		String token = auth.substring(7);
		try {
			String username = jwtService.extractUsername(token);
			String role = jwtService.extractRole(token);

			if (username != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
				var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ignored) {
		}

		chain.doFilter(req, res);
	}
}
