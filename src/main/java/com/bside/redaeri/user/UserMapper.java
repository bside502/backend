package com.bside.redaeri.user;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	
	public Map<String, Object> getUserInfo(Integer loginIdx);
	
	public int countUserAnswer(Integer loginIdx);
	
	public int deleteUser(Integer loginIdx);
}
