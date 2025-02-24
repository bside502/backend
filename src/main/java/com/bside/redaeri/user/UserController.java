package com.bside.redaeri.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
	
	@Autowired
	private UserService userService;

	@PostMapping("/user/get")
	public Map<String, Object> userGet(@RequestBody Map<String, Object> param) {
		
		return userService.getInfo(param);
	}

}
