package com.bside.redaeri.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.util.ApiResult;

@Service
public class StoreService {
	
	@Autowired
	public StoreMapper storeMapper;
	
	public ApiResult<Object> insertStoreInfo(StoreDto storeDto) {
		
		int count = storeMapper.getStoreCount(storeDto);
		if(count != 0) {
			// 이미 등록된 가게가 존재
			return ApiResult.success("2001", "이미 하나의 가게를 등록했습니다.", null);
		}
		
		int result = storeMapper.insertStoreInfo(storeDto);
		if(result == 1) {
			return ApiResult.success("200", "성공", null);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
	
	
	public ApiResult<Object> updateStoreInfo(StoreDto storeDto) {
		int count = storeMapper.getStoreCount(storeDto);
		if(count == 0) {
			// 등록된 가게가 없음
			return ApiResult.success("2002", "수정할 가게 정보가 없습니다.", null);
		}
		
		int result = storeMapper.updateStoreInfo(storeDto);
		if(result == 1) {
			return ApiResult.success("200", "성공", null);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
}
