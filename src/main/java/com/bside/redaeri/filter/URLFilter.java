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
import lombok.extern.slf4j.Slf4j;

@Component
@Order(1)
@Slf4j
public class URLFilter extends OncePerRequestFilter {
	@Value("${jwt.secret.key}")
	private String SECRET_KEY;
	
	@Autowired
	private JWTService jwtService;
	
	private final static String[] EXCLUDE_URI = {
						"/api/v1/*"
	};

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		boolean isExclude = false;
		String requestURI = request.getRequestURI();
		
		if(requestURI.equals("/api/v1/naver/callback")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		for(String uri : EXCLUDE_URI) {
			int lastIndex = uri.lastIndexOf("*");
			if(lastIndex > -1) {
				String startUri = uri.substring(0, lastIndex);
				
				if(requestURI.startsWith(startUri)) {
					isExclude = true;
					break;
				}
			} else if(requestURI.equals(uri)) {
				isExclude = true;
				break;
			}
		}
		
		if(isExclude) {
			filterChain.doFilter(request, response);
		}
		log.info("request Url => " + requestURI);
	}
}
