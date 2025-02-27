// PersonaController.java
package com.bside.redaeri.persona;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.login.LoginIdx;

@RestController
@RequestMapping("/api/v1")
public class PersonaController {
	
	@Autowired
	private PersonaService personaService;
	
	/**
	 * 말투 분석 후 정보 저장
	 * @param param
	 * @return
	 */
	@PostMapping("/persona/analyze")
	public Map<String, Object> personaAnalyze(@RequestBody Map<String, Object> param) {
		
		return personaService.personaAnalyze(param);
	}
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param param
	 * @return
	 */
	@PostMapping("/persona/insert")
	public Map<String, Object> personaInsert(@RequestBody Map<String, Object> param) {
		
		
		return personaService.insertPersonaInfo(param);
	}
	
	/**
	 * 말투 직접 수정해서 저장
	 * @param param
	 * @return
	 */
	@PatchMapping("/persona/update")
	public Map<String, Object> personaUpdate(@RequestBody Map<String, Object> param) {
		
		return personaService.updatePersonaInfo(param);
	}
	
	/**
	 * 만능 답변 수정
	 * @param param
	 * @return
	 */
	@PatchMapping("/persona/answer/update")
	public Map<String, Object> personaAnswerUpdate(@RequestBody Map<String, Object> param) {
		
		return personaService.updatePersonaAnswer(param);
	}
	
	/**
	 * 현재 나의 페르소나 정보 가져오기
	 * @param loginIdx
	 * @return
	 */
	@GetMapping("/persona/get")
	public Map<String, Object> personaGet(@LoginIdx Integer loginIdx) {
		
		return personaService.getPersonaInfo(loginIdx);
	}
	
}
