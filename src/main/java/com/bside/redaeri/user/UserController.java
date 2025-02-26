package com.bside.redaeri.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.login.LoginIdx;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JWTService jwtService;

	@GetMapping("/user/get")
	public Map<String, Object> userGet(@LoginIdx Integer loginIdx) throws Exception {

		return userService.getInfo(loginIdx);
	}
	
	@GetMapping("/user/test")
	public Map<String, Object> userTest(@LoginIdx Integer loginIdx) throws Exception {
		//String token = JwtUtil.getUserIdx(request.getHeader("token"));
		
		//System.out.println("@LoginIdx ==> " + loginIdx);
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("login_idx", 1);
		
		String token = jwtService.generateToken(map);
		System.out.println("token ==> " + token);
		
		return null;
	}
}
