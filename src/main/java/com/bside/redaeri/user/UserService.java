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
	
	/**
	 * 회원이 생성한 답변 갯수
	 * @param loginIdx
	 * @return
	 */
	public Map<String, Object> countUserAnswer(Integer loginIdx) {
		
		int cnt = userMapper.countUserAnswer(loginIdx);
		
		Map<String, Object> result = new HashMap<>();
		result.put("answerCount", cnt);
		
		return ResponseUtil.success(result);
	}
	
	public Map<String, Object> deleteUser(Integer loginIdx) {
		
		userMapper.deleteUser(loginIdx);
		// 가게 정보
		// 페르소나
		// 로그 삭제
		
		return ResponseUtil.success();
	}
}
