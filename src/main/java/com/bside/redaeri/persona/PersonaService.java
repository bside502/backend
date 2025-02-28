// PersonaService.java
package com.bside.redaeri.persona;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaPromptTemplates;
import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.store.StoreMapper;
import com.bside.redaeri.util.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PersonaService {
	
	@Autowired
	private ClovaService clovaService;
	
	@Autowired
	private PersonaMapper personaMapper;
	
	@Autowired
	private StoreMapper storeMapper;
	
	/**
	 * 말투 분석 후 정보 저장
	 * @param 
	 * @return
	 * @throws IOException 
	 */
	public ApiResult<Object> personaAnalyze(AnalyzeDto analyzeDto, Integer loginIdx) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		System.out.println(analyzeDto.getUploadFileList() + " : " + analyzeDto.getUploadFileList() == null);
		if(analyzeDto.getUploadFileList() != null) {
			for(MultipartFile mFile : analyzeDto.getUploadFileList()) {
				String name = mFile.getOriginalFilename();
				String format = name.substring(name.lastIndexOf(".") + 1, name.length());
				Map<String, Object> imgInfo = new HashMap<>();
				
				// todo 한번에 보낼 수 있도록 수정
				imgInfo.put("requestId", UUID.randomUUID().toString());
				imgInfo.put("timestamp", Instant.now().toEpochMilli());
				imgInfo.put("format", format);
				imgInfo.put("name", name);
				imgInfo.put("data", Base64.getEncoder().encodeToString(mFile.getBytes()));
			
				String answer = clovaService.imageTextExtract(imgInfo);
				sb.append("[리뷰 내용]\n").append(answer);
			}
			sb.append("\n\n");
		}
		
		if(analyzeDto.getUploadTextFirst() != null) {
			sb.append("[리뷰 내용]\n").append(analyzeDto.getUploadTextFirst());
			sb.append("\n\n");
		}
		if(analyzeDto.getUploadTextSecond() != null) {
			sb.append("[리뷰 내용]\n").append(analyzeDto.getUploadTextSecond());
			sb.append("\n\n");
		}
		if(analyzeDto.getUploadTextThird() != null) {
			sb.append("[리뷰 내용]\n").append(analyzeDto.getUploadTextThird());
		}
		
		String prompt = ClovaPromptTemplates.TEXT_PATTHEN_ANALYZE_PROMPT(sb.toString());
		String analyze = clovaService.generateChatResponse(prompt);
		
		//1. 클로바에 말투 분석 요청
		//2. 답변을 받고 
		//3. 형식에 맞게 전달.
		System.out.println("analyze --> " + analyze);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(analyze);
        
        String persona = rootNode.path("persona").asText();
        String emotion = rootNode.path("emotion").asText();
        String length = rootNode.path("length").asText();
        String answer = rootNode.path("answer").asText();
            
        
        PersonaDto personaDto = new PersonaDto();
        personaDto.setPersonaSelect(persona);
        personaDto.setEmotionSelect(emotion);
        personaDto.setLengthSelect(length);
        personaDto.setAllAnswer(answer);
        
        int type = 0;
        if(persona.contains("알바생")) {
        	type = 1;
        } else if(persona.contains("청년")) {
        	type = 2;
        } else if(persona.contains("초보")) {
        	type = 3;
        } else if(persona.contains("유쾌한")) {
        	type = 4;
        } else if(persona.contains("최선")) {
        	type = 5;
        }
        personaDto.setPersonaImgType(type);
        
        
        // 임시, storeIdx
        int storeIdx = personaMapper.getStoreIdx(loginIdx);
        
        personaDto.setStoreIdx(storeIdx);
        
        
        personaMapper.insertPersonaInfo(personaDto);
        
		Map<String, Object> result = new HashMap<>();
		result.put("result", analyze);
		return ApiResult.success("200", "성공", result);
	}
	
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param param
	 * @return
	 */
	public ApiResult<Object> insertPersonaInfo(Integer loginIdx, PersonaDto personaDto) {
		
		// todo clova 만능답변 만들어줘
		String prompt = ClovaPromptTemplates.GENERATE_ALL_ANSWER_PROMPT(personaDto);
		String answer = clovaService.generateChatResponse(prompt);
		
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		
		personaDto.setStoreIdx(storeIdx);
		personaDto.setAllAnswer(answer);
		int result = personaMapper.insertPersonaInfo(personaDto);
		if(result != 0) {
			return ApiResult.success("200", "성공", personaDto);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
	
	/**
	 * 말투 직접 수정해서 저장
	 * @param param
	 * @return
	 */
	public ApiResult<Object> updatePersonaInfo(PersonaDto personaDto) {
		
		String prompt = ClovaPromptTemplates.GENERATE_ALL_ANSWER_PROMPT(personaDto);
		String answer = clovaService.generateChatResponse(prompt);
		
		personaDto.setAllAnswer(answer);
		
		int result = personaMapper.updatePersonaInfo(personaDto);
		if(result != 0) {
			return ApiResult.success("200", "성공", personaDto);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
	
	
	/**
	 * 만능 답변 수정
	 * @param param
	 * @return
	 */
	public ApiResult<Object> updatePersonaAnswer(PersonaDto personaDto) {
		int result = personaMapper.updatePersonaAnswer(personaDto);
		if(result == 1) {
			return ApiResult.success("200", "성공", null);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
	
	/**
	 * 현재 나의 페르소나 정보 가져오기
	 * @param loginIdx
	 * @return
	 */
	public ApiResult<Object> getPersonaInfo(int loginIdx) {
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		
		Map<String, Object> result = personaMapper.getPersonaInfo(storeIdx);
		
		if(result != null) {
			return ApiResult.success("200", "성공", result);
		} else {
			return ApiResult.success("400", "실패", null);
		}
	}
}
