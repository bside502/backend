package com.bside.redaeri.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(2)
public class JWTFilter extends OncePerRequestFilter {
	@Value("${jwt.secret.key}")
	private String SECRET_KEY;
	
	@Autowired
	private JWTService jwtService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String requestURI = request.getRequestURI();
		
		if(requestURI.equals("/api/v1/naver/callback")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		filterChain.doFilter(request, response);
		// TODO jwtFilter
		
		String token = request.getHeader("token");
		if(token == null || token == "") {
			// auth error 떨구기
			//response.sendRedirect("/");
			return;
		}
		
		if(jwtService.isExpired(token)) {
			// 토큰 시간 만료 error 떨구기
			
		}
	}
}
