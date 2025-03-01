// PersonaService.java
package com.bside.redaeri.persona;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaPromptTemplates;
import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.store.StoreDto;
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
		sb.append("답변 : \n");
		

		// insert 불가능하도록
		int cnt = personaMapper.existPersona(loginIdx);
		if(cnt >= 1) {
			return ApiResult.error("5000", "이미 생성한 페르소나가 존재합니다.");
		}
		
		PersonaDto personaDto = new PersonaDto();
		if(analyzeDto.getPersonaIdx() == 0) {
			// 본인 페르소나만 수정할 수 있도록todo
			personaDto.setPersonaIdx(analyzeDto.getPersonaIdx());
		}
		
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
				sb.append(answer);
				sb.append("\n\n");
			}
		}
		
		if(analyzeDto.getUploadTextFirst() != null) {
			sb.append(analyzeDto.getUploadTextFirst());
			sb.append("\n\n");
		}
		if(analyzeDto.getUploadTextSecond() != null) {
			sb.append(analyzeDto.getUploadTextSecond());
			sb.append("\n\n");
		}
		if(analyzeDto.getUploadTextThird() != null) {
			sb.append(analyzeDto.getUploadTextThird());
		}
		
		
		String prompt = ClovaPromptTemplates.ANSWER_GENERATE("personaAnalyze/personaSelect.json", sb.toString());
		String answer = clovaService.generateChatResponse(prompt);
		personaDto.setPersonaSelect(answer);
		System.out.println("answer --> " + answer);

		prompt = ClovaPromptTemplates.ANSWER_GENERATE("personaAnalyze/lengthSelect.json", sb.toString());
		answer = clovaService.generateChatResponse(prompt);
		personaDto.setLengthSelect(answer);
		System.out.println("answer --> " + answer);

		prompt = ClovaPromptTemplates.ANSWER_GENERATE("personaAnalyze/emotionSelect.json", sb.toString());
		answer = clovaService.generateChatResponse(prompt);
		personaDto.setEmotionSelect(answer);
		System.out.println("answer --> " + answer);
        
        int type = 5;
        String persona = personaDto.getPersonaSelect();
		String promptPath = "answerGenerate/";
        if(persona.contains("알바생")) {
			type = 1;
			promptPath += "generateAnswer1.json";
		} else if(persona.contains("나이스")) {
			type = 2;                   
			promptPath += "generateAnswer2.json";

		} else if(persona.contains("유쾌한")) {
			type = 3;
			promptPath += "generateAnswer3.json";

		} else if(persona.contains("묵묵히")) {
			type = 4;
			promptPath += "generateAnswer4.json";
		} else {
			type = 5;
			promptPath += "generateAnswer5.json";
		}
        personaDto.setPersonaImgType(type);
        
        StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        String content = "가게 이름 :" + storeDto.getStoreName() + ", 가게 종류 : " + storeDto.getStoreType() + " \n" +
        		personaDto.getEmotionSelect() + "하는 내용으로, 문장 길이는 " + personaDto.getLengthSelect() + "으로 만능답변을 생성하세요";
        
        System.out.println("cotent -- >" + content);
    	prompt = ClovaPromptTemplates.ANSWER_GENERATE(promptPath, content);
		answer = clovaService.generateChatResponse(prompt);
		
		System.out.println(answer);
		personaDto.setAllAnswer(answer);
		
        
        // 만능 답변 생성
        
        // 임시, storeIdx
        int storeIdx = personaMapper.getStoreIdx(loginIdx);
        personaDto.setStoreIdx(storeIdx);
        
        // 기존 persona가 있으면 수정
        System.out.println("analyzeDto --> " + analyzeDto.getPersonaIdx());
        if(analyzeDto.getPersonaIdx() == 0) {
        	personaMapper.insertPersonaInfo(personaDto);
        } else {
        	personaDto.setPersonaIdx(analyzeDto.getPersonaIdx());
        	personaMapper.updatePersonaInfo(personaDto);
        }
        
		return ApiResult.success("200", "성공", personaDto);
	}
	
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param param
	 * @return
	 */
	public ApiResult<Object> insertPersonaInfo(Integer loginIdx, PersonaDto personaDto) {
		
		int storeCnt = personaMapper.getStoreCount(loginIdx);
		if(storeCnt == 0) {
			return ApiResult.error("4001", "가게 정보를 먼저 입력해주세요.");
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt >= 1) {
			return ApiResult.error("4001", "이미 페르소나 정보가 존재합니다.");
		}
		
		// todo clova 만능답변 만들어줘
		StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		String prompt = ClovaPromptTemplates.GENERATE_ALL_ANSWER_PROMPT(personaDto, storeDto);
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
	public ApiResult<Object> updatePersonaInfo(Integer loginIdx, PersonaDto personaDto) {

		int storeCnt = personaMapper.getStoreCount(loginIdx);
		if(storeCnt == 0) {
			return ApiResult.error("4001", "가게 정보를 먼저 입력해주세요.");
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt == 0) {
			return ApiResult.error("4001", "페르소나 정보를 먼저 입력해주세요.");
		}
		
		StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		String prompt = ClovaPromptTemplates.GENERATE_ALL_ANSWER_PROMPT(personaDto, storeDto);
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
		// 존재하는 페르소나 인지 확인
		// todo 본인의 페르소나만 수정 가능하도록
		int cnt = personaMapper.existPersonaInfo(personaDto);
		if(cnt == 0) {
			return ApiResult.success("3001", "존재하지 않는 페르소나 정보입니다.", null);
		}
		int result = personaMapper.updatePersonaAnswer(personaDto);
		if(result == 1) {
			return ApiResult.success("200", "성공", result);
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
			return ApiResult.success("400", "페르소나 정보를 등록해주세요", null);
		}
	}
}
