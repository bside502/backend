package com.bside.redaeri.answer;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaPromptTemplates;
import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.persona.PersonaMapper;
import com.bside.redaeri.util.ApiResult;

@Service
public class AnswerService {
	
	@Autowired
	private ClovaService clovaService;
	
	@Autowired
	private AnswerMapper answerMapper;
	
	@Autowired
	private PersonaMapper personaMapper;
	
	/**
	 * clova ocr 이미지 텍스트 읽기
	 * @param mFile
	 * @return
	 * @throws IOException
	 */
	public ApiResult<Object> readImageToText(MultipartFile mFile) throws IOException {
		
		String name = mFile.getOriginalFilename();
		String format = name.substring(name.lastIndexOf(".") + 1, name.length());
		
		Map<String, Object> imgInfo = new HashMap<>();
		
		imgInfo.put("requestId", UUID.randomUUID().toString());
		imgInfo.put("timestamp", Instant.now().toEpochMilli());
		imgInfo.put("format", format);
		imgInfo.put("name", name);
		imgInfo.put("data", Base64.getEncoder().encodeToString(mFile.getBytes()));
		
		String answer = clovaService.imageTextExtract(imgInfo);
		
		Map<String, Object> result = new HashMap<>();
		result.put("reviewText", answer);
		
		return ApiResult.success("200", "성공", result);
	}
	
	/**
	 * 답변 생성하기
	 * @param loginIdx
	 * @param answerDto
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	public ApiResult<Object> generateAnswer(Integer loginIdx, AnswerDto answerDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		answerDto.setLoginIdx(loginIdx);
		// answerDTO 값 넘겨주기
		int cnt = personaMapper.getStoreCount(loginIdx);
		if(cnt == 0) {
			return ApiResult.error("2002", "등록된 가게가 없습니다.");
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt == 0) {
			return ApiResult.error("2002", "등록된 페르소나가 없습니다.");
		}
		
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		answerDto.setStoreIdx(storeIdx);
		Map<String, Object> personaInfo = personaMapper.getPersonaInfo(storeIdx);
		
		// 리뷰 분류
		String engine = "HCX-003";
		String prompt = ClovaPromptTemplates.ANSWER_GENERATE("answerGenerate/reviewAnalyze.json", answerDto.getReviewText());
		String answer = clovaService.generateChatResponse(prompt, engine);
		answerDto.setReviewType(answer);
		System.out.println("type --> " + answer);
		
		// 페르소나에 맞게 리뷰 답변 생성
		answerDto.setPersonaIdx((int) personaInfo.get("personaIdx"));
		String persona = (String) personaInfo.get("personaSelect");
		
		String promptPath = "answerGenerate/";
		if(persona.contains("알바생")) {
			engine = "HCX-DASH-001";
			promptPath += "generateAnswer1.json";
		} else if(persona.contains("나이스")) {
			promptPath += "generateAnswer2.json";
		} else if(persona.contains("유쾌한")) {
			promptPath += "generateAnswer3.json";
		} else if(persona.contains("묵묵히")) {
			promptPath += "generateAnswer4.json";
		} else {
			promptPath += "generateAnswer5.json";
		}
		
		String content = answerDto.getReviewText() + "\n" + 
		"필수로 들어가야 하는 문구 : " + answerDto.getIncludeText();
		prompt = ClovaPromptTemplates.ANSWER_GENERATE(promptPath, content);
		answer = clovaService.generateChatResponse(prompt, engine);
		answerDto.setGenerateAnswer(answer);
		
		int result = answerMapper.insertAnswerGenerateLog(answerDto);
		return ApiResult.success("200", "성공", answerDto);
	}
	
	/**
	 * 답변 재생성하기
	 * @param answerDto
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	public ApiResult<Object> retryAnswer(AnswerDto answerDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		answerDto = answerMapper.getLogInfo(answerDto);
		Map<String, Object> personaInfo = personaMapper.getPersonaInfo(answerDto.getStoreIdx());

		String persona = (String) personaInfo.get("personaSelect");
		String engine = "HCX-003";
		String promptPath = "answerGenerate/";
		if(persona.contains("알바생")) {
			engine = "HCX-DASH-001";
			promptPath += "generateAnswer1.json";
		} else if(persona.contains("나이스")) {
			promptPath += "generateAnswer2.json";
		} else if(persona.contains("유쾌한")) {
			promptPath += "generateAnswer3.json";
		} else if(persona.contains("묵묵히")) {
			promptPath += "generateAnswer4.json";
		} else {
			promptPath += "generateAnswer5.json";
		}
		
		String content = answerDto.getReviewText() + "\n" + 
		"필수로 들어가야 하는 문구 : " + answerDto.getIncludeText();
		String prompt = ClovaPromptTemplates.ANSWER_GENERATE(promptPath, content);
		String answer = clovaService.generateChatResponse(prompt, engine);
		
		answerDto.setGenerateAnswer(answer);
		int cnt = answerMapper.updateAnswerGenerateLog(answerDto);
		
		return ApiResult.success("200", "성공", answerDto);
	}
	
	/**
	 * 생성한 답변 히스토리 보기
	 * @param loginIdx
	 * @return
	 */
	public ApiResult<Object> getAnswerLog(Integer loginIdx) {
		
		List<Map<String, Object>> result = answerMapper.getAnswerGenerateLogList(loginIdx);
		
		return ApiResult.success("200", "성공", result);
	}

}
