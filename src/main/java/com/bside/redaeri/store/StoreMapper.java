package com.bside.redaeri.store;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreMapper {
	
	public int getStoreCount(Map<String, Object> param);
	
	public int insertStoreInfo(Map<String, Object> param);
	
	public int updateStoreInfo(Map<String, Object> param);
}
