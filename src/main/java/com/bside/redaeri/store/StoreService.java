package com.bside.redaeri.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.vo.ResponseCode;

@Service
public class StoreService {
	
	@Autowired
	public StoreMapper storeMapper;
	
	public ApiResult<Object> insertStoreInfo(StoreDto storeDto) {
		
		int count = storeMapper.getStoreCount(storeDto);
		if(count != 0) {
			// 이미 등록된 가게가 존재
			return ApiResult.error(ResponseCode.EXIST_STORE); // 삭제 예정
		}
		
		int result = storeMapper.insertStoreInfo(storeDto);
		if(result == 1) {
			return ApiResult.success(ResponseCode.OK, null);
		} else {
			return ApiResult.error(ResponseCode.FAIL);
		}
	}
	
	
	public ApiResult<Object> updateStoreInfo(StoreDto storeDto) {
		int count = storeMapper.getStoreCount(storeDto);
		if(count == 0) {
			// 등록된 가게가 없음
			return ApiResult.error(ResponseCode.NOT_EXIST_STORE); // 삭제 예정
		}
		
		int result = storeMapper.updateStoreInfo(storeDto);
		if(result == 1) {
			return ApiResult.success(ResponseCode.OK, null);
		} else {
			return ApiResult.error(ResponseCode.FAIL); // 삭제 예정
		}
	}
}
