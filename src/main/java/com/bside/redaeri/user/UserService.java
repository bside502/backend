package com.bside.redaeri.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.util.ApiResult;

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
	public ApiResult<Object> getInfo(Integer loginIdx) {
		Map<String, Object> result = new HashMap<>();	
		
		result = userMapper.getUserInfo(loginIdx);
		
		if(result != null) {
			return ApiResult.success("200", "성공", result);
		} else {
			return ApiResult.success("400", "실패", null);
		}
		
	}
	
	/**
	 * 회원이 생성한 답변 갯수
	 * @param loginIdx
	 * @return
	 */
	public ApiResult<Object> countUserAnswer(Integer loginIdx) {
		
		int cnt = userMapper.countUserAnswer(loginIdx);
		
		Map<String, Object> result = new HashMap<>();
		result.put("answerCount", cnt);
		
		return ApiResult.success("200", "성공", result);
	}
	
	public ApiResult<Object> deleteUser(Integer loginIdx) {
		
		userMapper.deleteUser(loginIdx);
		// 가게 정보
		// 페르소나
		// 로그 삭제
		
		return ApiResult.success("200", "성공", null);
	}
}
