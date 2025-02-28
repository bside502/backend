package com.bside.redaeri.store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.login.LoginIdx;
import com.bside.redaeri.util.ApiResult;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
public class StoreController {
	
	@Autowired
	public StoreService storeService;
	
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
