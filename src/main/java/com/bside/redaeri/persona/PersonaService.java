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
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.store.StoreMapper;
import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.vo.ResponseCode;

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
		
		String engine = "HCX-003";
		String prompt = clovaService.readPromptFileToJson("personaAnalyze/personaSelect.json", sb.toString());
		System.out.println("prompt -- >" + prompt);
		String answer = clovaService.generateChatResponse(prompt, engine);
		personaDto.setPersonaSelect(answer);
		System.out.println("answer --> " + answer);

		prompt = clovaService.readPromptFileToJson("personaAnalyze/lengthSelect.json", sb.toString());
		String length = clovaService.generateChatResponse(prompt, engine);

		String lengthText = "핵심만 간단하게 단문";
		if(length.contains("장문")) {
			lengthText = "적당한 중간 길이";
		} else if(length.contains("중문")) {
			lengthText = "정성이 담긴 장문";
		}
		personaDto.setLengthSelect(lengthText);

		prompt = clovaService.readPromptFileToJson("personaAnalyze/emotionSelect.json", sb.toString());
		String emotion = clovaService.generateChatResponse(prompt, engine);
		String emotionText = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(emotion.contains("감사")) {
			emotionText = "따뜻한 한마디에 감사하고,";
		} else if(emotion.contains("행복")) {
			emotionText = "고객과 소통하며 기뻐하고,";
		}
		
		personaDto.setEmotionSelect(emotionText);
		System.out.println("answer --> " + emotion);
        
        int type = 5;
        String persona = personaDto.getPersonaSelect();
		String promptPath = "answerGenerate/";
        if(persona.contains("알바생")) {
			engine = "HCX-DASH-001";
			type = 1;
			promptPath += "generateAnswer1.json";
			persona = "해피바이러스! 발랄한 20대 알바생";
		} else if(persona.contains("나이스")) {
			type = 2;                   
			promptPath += "generateAnswer2.json";
			persona = "예의 바르고 나이스한 30대 초보 사장님";
		} else if(persona.contains("유쾌한")) {
			type = 3;
			promptPath += "generateAnswer3.json";
			persona = "단골 챙기는 정 많고 유쾌한 40대 사장님";
		} else if(persona.contains("묵묵히")) {
			type = 4;
			promptPath += "generateAnswer4.json";
			persona = "묵묵히 음식에 최선을 다하는 60대 사장님";
		} else {
			type = 5;
			promptPath += "generateAnswer5.json";
			persona = "충청도 출신 외식업 강자 사장님";
		}
        personaDto.setPersonaImgType(type);
        personaDto.setPersonaSelect(persona);

        System.out.println("path --> " + promptPath);
        
        //StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        String content = emotion + "하는 내용으로, 문장 길이는 " + personaDto.getLengthSelect() + "으로 만능답변을 생성하세요";
        
        System.out.println("cotent -- >" + content);
    	prompt = clovaService.readPromptFileToJson(promptPath, content);
		answer = clovaService.generateChatResponse(prompt, engine);
		
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
        
		return ApiResult.success(ResponseCode.OK, personaDto);
	}
	
	
	/**
	 * 말투 직접 선택해서 저장
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	public ApiResult<Object> insertPersonaInfo(Integer loginIdx, PersonaDto personaDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		int storeCnt = personaMapper.getStoreCount(loginIdx);
		if(storeCnt == 0) {
			return ApiResult.error(ResponseCode.NOT_EXIST_STORE);
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt >= 1) {
			return ApiResult.error(ResponseCode.EXIST_PERSONA);
		}
		
		String emotion = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(personaDto.getEmotionSelect().contains("감사")) {
			emotion = "따뜻한 한마디에 감사하고,";
		} else if(personaDto.getEmotionSelect().contains("행복")) {
			emotion = "고객과 소통하며 기뻐하고,";
		}
		
		String length = "핵심만 간단하게 단문";
		if(personaDto.getLengthSelect().contains("정성")) {
			length = "정성이 담긴 장문";
		} else if(personaDto.getLengthSelect().contains("알잘딱")) {
			length = "적당한 중간 길이";
		}
		
		// todo clova 만능답변 만들어줘
		String persona = personaDto.getPersonaSelect();
		String promptPath = "answerGenerate/";
		String engine = "HCX-003";
		int type = 5;
		if(persona.contains("알바생")) {
			promptPath += "generateAnswer1.json";
			engine = "HCX-DASH-001";
			type = 1;
		} else if(persona.contains("나이스")) {
			promptPath += "generateAnswer2.json";
			type = 2;
		} else if(persona.contains("유쾌한")) {
			promptPath += "generateAnswer3.json";
			type = 3;
		} else if(persona.contains("묵묵히")) {
			promptPath += "generateAnswer4.json";
			type = 4;
		} else {
			promptPath += "generateAnswer5.json";
			type = 5;
		}
		personaDto.setPersonaImgType(type);
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        String content = personaDto.getEmotionSelect() + "하는 내용으로, 문장 길이는 " + personaDto.getLengthSelect() + "으로 만능답변을 생성하세요";
	        
		personaDto.setEmotionSelect(emotion);
		personaDto.setLengthSelect(length);
        
        String prompt = clovaService.readPromptFileToJson(promptPath, content);
		String answer = clovaService.generateChatResponse(prompt, engine);
		personaDto.setAllAnswer(answer);
		
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		
		personaDto.setStoreIdx(storeIdx);
		int result = personaMapper.insertPersonaInfo(personaDto);
		if(result != 0) {
			return ApiResult.success(ResponseCode.OK, personaDto);
		} else {
			return ApiResult.error(ResponseCode.FAIL);
		}
	}
	
	/**
	 * 말투 직접 수정해서 저장
	 * @param param
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	public ApiResult<Object> updatePersonaInfo(Integer loginIdx, PersonaDto personaDto) throws io.jsonwebtoken.io.IOException, IOException {

		int storeCnt = personaMapper.getStoreCount(loginIdx);
		if(storeCnt == 0) {
			return ApiResult.error(ResponseCode.NOT_EXIST_STORE);
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt == 0) {
			return ApiResult.error(ResponseCode.NOT_EXIST_PERSONA);
		}
		
		String emotion = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(personaDto.getEmotionSelect().contains("감사")) {
			emotion = "따뜻한 한마디에 감사하고,";
		} else if(personaDto.getEmotionSelect().contains("행복")) {
			emotion = "고객과 소통하며 기뻐하고,";
		}
		
		String length = "핵심만 간단하게 단문";
		if(personaDto.getLengthSelect().contains("정성")) {
			length = "정성이 담긴 장문";
		} else if(personaDto.getLengthSelect().contains("알잘딱")) {
			length = "적당한 중간 길이";
		}
		
		String persona = personaDto.getPersonaSelect();
		String promptPath = "answerGenerate/";
		String engine = "HCX-003";
		int type = 5;
		if(persona.contains("알바생")) {
			engine = "HCX-DASH-001";
			promptPath += "generateAnswer1.json";
			type = 1;
		} else if(persona.contains("나이스")) {
			promptPath += "generateAnswer2.json";
			type = 2;
		} else if(persona.contains("유쾌한")) {
			promptPath += "generateAnswer3.json";
			type = 3;
		} else if(persona.contains("묵묵히")) {
			promptPath += "generateAnswer4.json";
			type = 4;
		} else {
			promptPath += "generateAnswer5.json";
			type = 5;
		}
		personaDto.setPersonaImgType(type);
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        String content = personaDto.getEmotionSelect() + "하는 내용으로, 문장 길이는 " + personaDto.getLengthSelect() + "으로 만능답변을 생성하세요";

		personaDto.setEmotionSelect(emotion);
		personaDto.setLengthSelect(length);

        String prompt = clovaService.readPromptFileToJson(promptPath, content);
		String answer = clovaService.generateChatResponse(prompt, engine);
		personaDto.setAllAnswer(answer);
		
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		
		personaDto.setStoreIdx(storeIdx);
		int result = personaMapper.updatePersonaInfo(personaDto);
		if(result != 0) {
			return ApiResult.success(ResponseCode.OK, personaDto);
		} else {
			return ApiResult.error(ResponseCode.FAIL);
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
			return ApiResult.error(ResponseCode.NOT_EXIST_PERSONA);
		}
		int result = personaMapper.updatePersonaAnswer(personaDto);
		if(result == 1) {
			return ApiResult.success(ResponseCode.OK, result);
		} else {
			return ApiResult.error(ResponseCode.FAIL);
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
			String persona = (String) result.get("personaSelect");
			int type = 5;
			if(persona.contains("알바생")) {
				type = 1;
			} else if(persona.contains("나이스")) {
				type = 2;
			} else if(persona.contains("유쾌한")) {
				type = 3;
			} else if(persona.contains("묵묵히")) {
				type = 4;
			} else {
				type = 5;
			}
			result.put("personaImgType", type);
			return ApiResult.success(ResponseCode.OK, result);
		} else {
			return ApiResult.error(ResponseCode.NOT_EXIST_PERSONA);
		}
	}
}
