package com.example.redaeri.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	private static final String SECRET_KEY = "";
	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1일
	
	
	public static String generateToken(String userId) throws Exception {
		 Map<String, Object> claims = new HashMap<>();
		 
		 claims.put("user_idx", userId);
		 
		 return Jwts.builder()
        		 .header()
                 .add("alg", "HS256")
                 .add("typ", "JWT")
                 .and()
                 .claims(claims)
                 .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                 .issuedAt(new Date())
        		 .signWith(createKey(SECRET_KEY))
                 .compact();
    }
	 private static Key createKey(String key) throws Exception {
		 return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
	 }
}
