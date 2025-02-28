package com.bside.redaeri.store;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StoreMapper {
	
	@Select("SELECT COUNT(*) FROM store s WHERE s.user_idx = #{loginIdx}")
	public int getStoreCount(StoreDto storeDto);

	@Insert("INSERT INTO store(store_name, store_type, user_idx)"
			+ "VALUES (#{storeName}, #{storeType}, #{loginIdx})")
	public int insertStoreInfo(StoreDto storeDto);
	
	@Update("UPDATE store SET store_name = #{storeName}, store_type = #{storeType} WHERE user_idx = #{loginIdx}")
	public int updateStoreInfo(StoreDto storeDto);

}
