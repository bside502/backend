package com.bside.redaeri.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JWTService jwtService;

	@GetMapping("/user/get")
	public Map<String, Object> userGet(HttpServletRequest request) throws Exception {
		//String token = JwtUtil.getUserIdx(request.getHeader("token"));
		
		Map<String, Object> map = new HashMap<>();
		map.put("user_idx", 2);
		
		String token = jwtService.generateToken(map);
		System.out.println("token ==> " + token);
		
		return userService.getInfo();
	}
}
