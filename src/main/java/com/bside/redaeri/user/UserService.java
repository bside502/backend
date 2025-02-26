package com.bside.redaeri.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.util.ResponseUtil;
import com.bside.redaeri.vo.ResponseCode;

@Service
public class UserService {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 회원 정보
	 * @param param
	 * @return
	 */
	public Map<String, Object> getInfo(Integer loginIdx) {
		Map<String, Object> result = new HashMap<>();	
		
		result = userMapper.getUserInfo(loginIdx);
		
		if(result != null) {
			return ResponseUtil.success(result);
		} else {
			return ResponseUtil.error(ResponseCode.FAIL);
		}
		
	}
	
}
