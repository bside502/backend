package com.bside.redaeri.store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.util.ResponseUtil;
import com.bside.redaeri.vo.ResponseCode;

@Service
public class StoreService {
	
	@Autowired
	public StoreMapper storeMapper;
	
	public Map<String, Object> insertStoreInfo(Map<String, Object> param) {
		
		int count = storeMapper.getStoreCount(param);
		if(count != 0) {
			// 이미 등록된 가게가 존재
			return ResponseUtil.error(ResponseCode.FAIL);
		}
		
		int result = storeMapper.insertStoreInfo(param);
		if(result == 1) {
			return ResponseUtil.success();
		} else {
			return ResponseUtil.error(ResponseCode.FAIL);
		}
	}
	
	
	public Map<String, Object> updateStoreInfo(Map<String, Object> param) {
		int count = storeMapper.getStoreCount(param);
		if(count == 0) {
			// 등록된 가게가 없음
			return ResponseUtil.error(ResponseCode.FAIL);
		}
		
		int result = storeMapper.updateStoreInfo(param);
		if(result == 1) {
			return ResponseUtil.success();
		} else {
			return ResponseUtil.error(ResponseCode.FAIL);
		}
	}
}
