package com.bside.redaeri.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.login.LoginIdx;
import com.bside.redaeri.util.ApiResult;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private JWTService jwtService;

	@SecurityRequirement(name = "token")
	@GetMapping("/user/get")
	public ApiResult<Object> userGet(@LoginIdx Integer loginIdx) throws Exception {

		return userService.getInfo(loginIdx);
	}
	
	
	@GetMapping("/user/test/token")
	public Map<String, Object> userAdd() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		UserDto user = new UserDto();
		user.setUserId("test");
		
		userMapper.insertUser(user);
		
		map.put("loginIdx", user.getIdx());
		String token = jwtService.generateToken(map);
		
		map.put("token", token);
		return map;
	}
	
	@SecurityRequirement(name = "token")
	@GetMapping("/user/answer/count")
	public ApiResult<Object> userAnswerCount(@LoginIdx Integer loginIdx) {
		
		return userService.countUserAnswer(loginIdx);
	}
	
	@SecurityRequirement(name = "token")
	@DeleteMapping("/user/delete")
	public ApiResult<Object> userDelete(@LoginIdx Integer loginIdx) {
		
		return userService.deleteUser(loginIdx);
	}
	
}
