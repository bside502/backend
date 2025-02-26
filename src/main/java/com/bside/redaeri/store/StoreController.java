package com.bside.redaeri.store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.login.LoginIdx;

@RestController
@RequestMapping("/api/v1/")
public class StoreController {
	
	@Autowired
	public StoreService storeService;
	
	@PostMapping("/store/insert")
	public Map<String, Object> storeInsert(@LoginIdx Integer loginIdx, @RequestBody Map<String, Object> param) {
		
		param.put("loginIdx", loginIdx);
		
		return storeService.insertStoreInfo(param);
	}
	
	@PatchMapping("/store/update")
	public Map<String, Object> storeUpdate(@LoginIdx Integer loginIdx, @RequestBody Map<String, Object> param) {
		
		param.put("loginIdx", loginIdx);
		
		return storeService.updateStoreInfo(param);
	}
}
