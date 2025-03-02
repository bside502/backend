// PersonaController.java
package com.bside.redaeri.persona;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.login.LoginIdx;
import com.bside.redaeri.util.ApiResult;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class PersonaController {
	
	@Autowired
	private PersonaService personaService;
	
	/**
	 * 말투 분석 후 정보 저장
	 * @param AnalyzeDto
	 * @return
	 * @throws IOException 
	 */
	@SecurityRequirement(name = "token")
	@PostMapping(value = "/persona/analyze")
	public ApiResult<Object> personaAnalyze(
			@RequestParam(value = "uploadFileList", required = false) List<MultipartFile> uploadFileList,
		    HttpServletRequest request,
		    @LoginIdx Integer loginIdx) throws IOException {
		
		AnalyzeDto analyzeDto = new AnalyzeDto();
		analyzeDto.setUploadFileList(uploadFileList);
		analyzeDto.setUploadTextFirst(request.getParameter("uploadTextFirst"));
		analyzeDto.setUploadTextSecond(request.getParameter("uploadTextSecond"));
		analyzeDto.setUploadTextThird(request.getParameter("uploadTextThird"));
		if(request.getParameter("personaIdx") != null) {
			analyzeDto.setPersonaIdx(Integer.parseInt(request.getParameter("personaIdx")));
		}
		System.out.println("analyzeDto --> " + analyzeDto.getUploadTextFirst());
		System.out.println("analyzeDto --> " + analyzeDto.getUploadTextSecond());
		System.out.println("analyzeDto --> " + analyzeDto.getUploadTextThird());

		return personaService.personaAnalyze(analyzeDto, loginIdx);
	}
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param PersonaDto
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	@SecurityRequirement(name = "token")
	@PostMapping("/persona/insert")
	public ApiResult<Object> personaInsert(@LoginIdx Integer loginIdx, @RequestBody PersonaDto personaDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		return personaService.insertPersonaInfo(loginIdx, personaDto);
	}
	
	/**
	 * 말투 직접 수정해서 저장
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	@SecurityRequirement(name = "token")
	@PatchMapping("/persona/update")
	public ApiResult<Object> personaUpdate(@LoginIdx Integer loginIdx, @RequestBody PersonaDto personaDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		return personaService.updatePersonaInfo(loginIdx, personaDto);
	}
	
	/**
	 * 만능 답변 수정
	 * @param param
	 * @return
	 */
	@SecurityRequirement(name = "token")
	@PatchMapping("/persona/answer/update")
	public ApiResult<Object> personaAnswerUpdate(@RequestBody PersonaDto personaDto) {
		
		return personaService.updatePersonaAnswer(personaDto);
	}
	
	/**
	 * 현재 나의 페르소나 정보 가져오기
	 * @param loginIdx
	 * @return
	 */
	@SecurityRequirement(name = "token")
	@GetMapping("/persona/get")
	public ApiResult<Object> personaGet(@LoginIdx Integer loginIdx) {
		
		return personaService.getPersonaInfo(loginIdx);
	}
	
}
