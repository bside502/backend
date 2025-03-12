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
		int count = 0;
		int textLength = 0;
		
		StringBuilder sb = new StringBuilder();
		
		PersonaDto personaDto = new PersonaDto();
		if(analyzeDto.getPersonaIdx() == 0) {
			// 본인 페르소나만 수정할 수 있도록todo
			personaDto.setPersonaIdx(analyzeDto.getPersonaIdx());
		}
		
		if(analyzeDto.getUploadFileList() != null) {
			for(MultipartFile mFile : analyzeDto.getUploadFileList()) {
				count++;
				sb.append(count).append("번째 답변 : ");
				
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
				sb.append("\n");
				textLength += answer.length();
			}
		}
		
		if(analyzeDto.getUploadTextFirst() != null) {
			count++;
			sb.append(count).append("번째 답변 : ");
			sb.append(analyzeDto.getUploadTextFirst());
			sb.append("\n");
			textLength += analyzeDto.getUploadTextFirst().length();
		}
		if(analyzeDto.getUploadTextSecond() != null) {
			count++;
			sb.append(count).append("번째 답변 : ");
			sb.append(analyzeDto.getUploadTextSecond());
			sb.append("\n");
			textLength += analyzeDto.getUploadTextSecond().length();
		}
		if(analyzeDto.getUploadTextThird() != null) {
			count++;
			sb.append(count).append("번째 답변 : ");
			sb.append(analyzeDto.getUploadTextThird());
			textLength += analyzeDto.getUploadTextThird().length();
		}
		
		String engine = "HCX-003";
		
		String content = "- 최대 3개의 답변이 주어질 수 있으며, 가장 유사한 단 하나의 인물만 출력하세요.\n" + sb.toString(); 
		String prompt = clovaService.readPromptFileToJson("personaAnalyze/personaSelect.json", content);
		String persona = clovaService.generateChatResponse(prompt, engine, "");
		System.out.println("persona --> " + persona);
		
		String personaText = "nicePersona";
		if(persona.contains("20대")) {
			personaText = "happyPersona";
		} else if(persona.contains("40대")) {
			personaText = "pleasantPersona";
		} else if(persona.contains("60대")) {
			personaText = "silentPersona";
		} else if(persona.contains("충청도")) {
			personaText = "chungcheongdoPersona";
		}
		personaDto.setPersonaSelect(personaText);

		int length = textLength / count;

		String lengthText = "long";
		if(length <= 199) {
			lengthText = "short";
		} else if(length >= 200 && length <= 299) {
			lengthText = "medium";
		}
		personaDto.setLengthSelect(lengthText);
		
		prompt = clovaService.readPromptFileToJson("personaAnalyze/emotionSelect.json", sb.toString());
		String emotion = clovaService.generateChatResponse(prompt, engine, "");
		String emotionText = "cheer";
		if(emotion.contains("감사")) {
			emotionText = "thank";
		} else if(emotion.contains("행복")) {
			emotionText = "happy";
		}
		
		personaDto.setEmotionSelect(emotionText);
		
		/**
		 * 말투 분석 후 baseAnswer 가져오기
		 */
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath(emotionText, lengthText);
        content = "###모든 리뷰에 답변할 수 있는 만능 답변을 생성하세요.\n- {가게 이름}은 제외하세요.\n- {포함 내용}은 제외하세요.";
        
		prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"), "");
		
		System.out.println("baseAnswer --> " + baseAnswer);
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(personaText);
		
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"), personaText);
		personaDto.setAllAnswer(answer);

        System.out.println("answer --> " + answer);
        
        
        //StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
        
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
		
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath(personaDto.getEmotionSelect(), personaDto.getLengthSelect());
        String content = "###모든 리뷰에 답변할 수 있는 만능 답변을 생성하세요.\n- {가게 이름}은 제외하세요.\n- {포함 내용}은 제외하세요.";
		
		String prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"), "");
		System.out.println("baseAnswer -- >" + baseAnswer + "\npath  -->" +  personaAdditionalPromptInfo.get("path"));

		String persona = personaDto.getPersonaSelect();
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"), persona);
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
        String content = "###모든 리뷰에 답변할 수 있는 만능 답변을 생성하세요.\n- {가게 이름}은 제외하세요.\n- {포함 내용}은 제외하세요.";
		
        // 1. 감정 + 길이에 맞게 만능 답변 생성
		String prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), content);
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"), "");
		System.out.println("baseAnswer -- >" + baseAnswer + "\npath  -->" +  personaAdditionalPromptInfo.get("path"));

		String persona = personaDto.getPersonaSelect();
		// 페르소나 인물 프롬프트 정보 가져오기
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		personaDto.setPersonaImgType((int) personaPromptInfo.get("type"));
		
		//StoreDto storeDto = storeMapper.getStoreInfo(loginIdx);
		
		// baseAnswer --> 1차로 생성한 답변에서 추가로 프롬프트 더 작성해야하는지 테스트
        prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), baseAnswer);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"), persona);
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
			int type = 2;
			if(persona.equals("happyPersona")) {
				type = 1;
			} else if(persona.equals("pleasantPersona")) {
				type = 3;
			} else if(persona.equals("silentPersona")) {
				type = 4;
			} else if(persona.equals("chungcheongdoPersona")){
				type = 5;
			}
			result.put("personaImgType", type);
			return ApiResult.success(ResponseCode.OK, result);
		} else {
			return ApiResult.error(ResponseCode.NOT_EXIST_PERSONA);
		}
	}
	
	public ApiResult<Object> preferPersona() {
		Map<String, Object> persona = personaMapper.preferPersona();
		
		return ApiResult.success(ResponseCode.OK, persona);
	}
}
