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
import com.bside.redaeri.util.PromptUtil;
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
		
		//TODO 프롬프트 수정 1~3가지의 답변이 주어진다.
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
		String persona = clovaService.generateChatResponse(prompt, engine);
		
		String personaText = "nicePersona";
		if(persona.contains("해피")) {
			personaText = "happyPersona";
		} else if(persona.contains("유쾌한")) {
			personaText = "pleasantPersona";
		} else if(persona.contains("묵묵히")) {
			personaText = "silentPersona";
		} else if(persona.contains("충청도")) {
			personaText = "chungcheongdoPersona";
		}
		personaDto.setPersonaSelect(personaText);
		System.out.println("persona --> " + personaText);

		prompt = clovaService.readPromptFileToJson("personaAnalyze/lengthSelect.json", sb.toString());
		String length = clovaService.generateChatResponse(prompt, engine);

		String lengthText = "핵심만 간단하게 단문";
		if(length.contains("장문")) {
			lengthText = "정성이 담긴 장문";
		} else if(length.contains("중문")) {
			lengthText = "적당한 중간 길이";
		}
		personaDto.setLengthSelect(lengthText);

		prompt = clovaService.readPromptFileToJson("personaAnalyze/emotionSelect.json", sb.toString());
		String emotion = clovaService.generateChatResponse(prompt, engine);
		String emotionText = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(emotion.contains("감사")) {
			emotionText = "따뜻한 한마디에 감사하고,";
		} else if(emotion.contains("기뻐")) {
			emotionText = "고객과 소통하며 기뻐하고,";
		}
		
		personaDto.setEmotionSelect(emotionText);
		System.out.println("answer --> " + emotion);
		
		/**
		 * 말투 분석 후 baseAnswer 가져오기
		 */
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath(personaDto.getEmotionSelect(), personaDto.getLengthSelect());
        String content = "모든 음식 후기에 답변할 수 있는 만능 답변을 생성하세요.";
		
		prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"));
		
        persona = personaDto.getPersonaSelect();
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"));
		personaDto.setAllAnswer(answer);

        System.out.println("answer --> " + answer);
        
        
        //StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        
        // 임시, storeIdx
        int storeIdx = personaMapper.getStoreIdx(loginIdx);
        personaDto.setStoreIdx(storeIdx);
        
        // 기존 persona가 있으면 수정
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
		
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath(personaDto.getEmotionSelect(), personaDto.getLengthSelect());
        String content = "모든 음식 후기에 답변할 수 있는 만능 답변을 생성하세요.";
		
		String prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"));
		System.out.println("baseAnswer -- >" + baseAnswer + "\npath  -->" +  personaAdditionalPromptInfo.get("path"));

		
		String emotion = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(personaDto.getEmotionSelect().contains("감사")) {
			emotion = "따뜻한 한마디에 감사하고,";
		} else if(personaDto.getEmotionSelect().contains("기뻐")) {
			emotion = "고객과 소통하며 기뻐하고,";
		}
		
		String length = "핵심만 간단하게 단문";
		if(personaDto.getLengthSelect().contains("장문")) {
			length = "정성이 담긴 장문";
		} else if(personaDto.getLengthSelect().contains("알잘딱")) {
			length = "적당한 중간 길이";
		}
		personaDto.setEmotionSelect(emotion);
		personaDto.setLengthSelect(length);
		
		String persona = personaDto.getPersonaSelect();
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"));
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
		
		// TODO
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath(personaDto.getEmotionSelect(), personaDto.getLengthSelect());
        String content = "모든 음식 후기에 답변할 수 있는 만능 답변을 생성하세요.";
		
        // 1. 감정 + 길이에 맞게 만능 답변 생성
		String prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"));
		System.out.println("baseAnswer -- >" + baseAnswer + "\npath  -->" +  personaAdditionalPromptInfo.get("path"));

		
		String emotion = "힘이 되는 리뷰로부터 자신감을 충전하고,";
		if(personaDto.getEmotionSelect().contains("감사")) {
			emotion = "따뜻한 한마디에 감사하고,";
		} else if(personaDto.getEmotionSelect().contains("기뻐")) {
			emotion = "고객과 소통하며 기뻐하고,";
		}
		
		String length = "핵심만 간단하게 단문";
		if(personaDto.getLengthSelect().contains("장문")) {
			length = "정성이 담긴 장문";
		} else if(personaDto.getLengthSelect().contains("알잘딱")) {
			length = "적당한 중간 길이";
		}

		personaDto.setEmotionSelect(emotion);
		personaDto.setLengthSelect(length);
		
		String persona = personaDto.getPersonaSelect();
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"));
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
		
		
		// TODO 수정
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
