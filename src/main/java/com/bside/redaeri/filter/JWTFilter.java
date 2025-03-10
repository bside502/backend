package com.bside.redaeri.filter;

import java.io.IOException;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(2)
@Slf4j
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
		
		// TODO jwtFilter
		
		String token = request.getHeader("token");
		System.out.println("token!! --> " + token);
		/*
		if(token == null || token == "") {
		    sendUnauthorizedResponse(response, "missing JWT token");
		    return;
		}
		if(jwtService.isExpired(token)) {
		    sendUnauthorizedResponse(response, "JWT token has expired");
		    return;
		}
		*/
		filterChain.doFilter(request, response);
	}
	
	private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    response.setContentType("application/json");
	    response.getWriter().write(" {\"code\": 401,\n");
	    response.getWriter().write("\"error\": \"" + message + "\"}");
	    response.getWriter().flush();
	}
}
