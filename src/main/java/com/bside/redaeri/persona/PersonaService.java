// PersonaService.java
package com.bside.redaeri.persona;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.util.ResponseUtil;
import com.bside.redaeri.vo.ResponseCode;

@Service
public class PersonaService {
	
	@Autowired
	private ClovaService clovaService;
	
	@Autowired
	private PersonaMapper personaMapper;
	
	/**
	 * 말투 분석 후 정보 저장
	 * @param param
	 * @return
	 */
	public Map<String, Object> personaAnalyze(Map<String, Object> param) {
		clovaService.generateChatResponse();
		
		return param;
	}
	
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param param
	 * @return
	 */
	public Map<String, Object> insertPersonaInfo(Map<String, Object> param) {
		
		return ResponseUtil.error(ResponseCode.FAIL);
	}
	
	/**
	 * 말투 직접 수정해서 저장
	 * @param param
	 * @return
	 */
	public Map<String, Object> updatePersonaInfo(Map<String, Object> param) {
		
		return ResponseUtil.error(ResponseCode.FAIL);
	}
	
	
	/**
	 * 만능 답변 수정
	 * @param param
	 * @return
	 */
	public Map<String, Object> updatePersonaAnswer(Map<String, Object> param) {
		int result = personaMapper.updatePersonaAnswer(param);
		if(result == 1) {
			return ResponseUtil.success();
		} else {
			return ResponseUtil.error(ResponseCode.FAIL);
		}
	}
	
	/**
	 * 현재 나의 페르소나 정보 가져오기
	 * @param loginIdx
	 * @return
	 */
	public Map<String, Object> getPersonaInfo(int loginIdx) {
		Map<String, Object> result = personaMapper.getPersonaInfo(loginIdx);
		
		if(result != null) {
			return ResponseUtil.success(result);
		} else {
			return ResponseUtil.error(ResponseCode.FAIL);
		}
	}
}
