package com.bside.redaeri.filter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class JWTService {
	
	@Value("${jwt.secret.key}")
	private String key;
	
	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 10; // 10일
	
    private SecretKey secretKey() {
		return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
	
	public String generateToken(Map<String, Object> param) throws Exception {
		 Map<String, Object> claims = new HashMap<>();
		 
		 claims.putAll(param);
		 
		 return Jwts.builder()
        		 .header()
                 .add("alg", "HS256")
                 .add("typ", "JWT")
                 .and()
                 .claims(claims)
                 .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                 .issuedAt(new Date())
        		 .signWith(secretKey())
                 .compact();
    }
	 
	 public String loginToken(Map<String, Object> param) throws Exception {
		 return generateToken(param);
	 }
	 
	 public Integer getUserIdx(String jwt) {
		 String accessToken = jwt;
		 if(accessToken == null || accessToken.length() == 0) {
			 return null;
		 }
		 
		 
		 Integer idx = Jwts.parser()
					.verifyWith(secretKey())
					.build()
					.parseSignedClaims(jwt)
					.getPayload()
					.get("loginIdx", Integer.class);
		 
		 if(idx == null) {
			 idx = 0;
		 }
		 return idx;
	 }
	 
	 // 로그인 만료 시간
	 public Boolean isExpired(String token) {
	        return Jwts.parser()
	        		.verifyWith(secretKey())
	        		.build()
	        		.parseSignedClaims(token)
	        		.getPayload()
	        		.getExpiration()
	        		.before(new Date());
	    }
}
