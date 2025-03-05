package com.bside.redaeri.store;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.login.LoginIdx;
import com.bside.redaeri.user.UserMapper;
import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.vo.ResponseCode;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
public class StoreController {
	
	@Autowired
	public StoreService storeService;
	
	@Autowired
	public JWTService jwtService;
	
	@Autowired
	public UserMapper userMapper;
	
	@GetMapping("/token/get")
	public ApiResult<Object> tokenGet(@RequestParam("userIdx") int userIdx) throws Exception {

		int cnt = userMapper.existsUser(userIdx);
		if(cnt == 0) {
			return ApiResult.error(ResponseCode.NOT_EXIST_USER);
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("loginIdx", userIdx);
		
		String jwtToken = jwtService.generateToken(result);
		result.put("token", jwtToken);
		
		return ApiResult.success(ResponseCode.ISSUED, result);
	}
	
	
	@SecurityRequirement(name = "token")
	@PostMapping("/store/insert")
	public ApiResult<Object> storeInsert(@LoginIdx Integer loginIdx, @RequestBody StoreDto storeDto) {
		
		storeDto.setLoginIdx(loginIdx);
		
		return storeService.insertStoreInfo(storeDto);
	}
	
	@SecurityRequirement(name = "token")
	@PatchMapping("/store/update")
	public ApiResult<Object> storeUpdate(@LoginIdx Integer loginIdx, @RequestBody StoreDto storeDto) {
		
		storeDto.setLoginIdx(loginIdx);
		
		return storeService.updateStoreInfo(storeDto);
	}
}
