package com.bside.redaeri.answer;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.persona.PersonaMapper;
import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.util.PromptUtil;
import com.bside.redaeri.vo.ResponseCode;

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
		if(mFile == null) {
			return ApiResult.error(ResponseCode.FAIL);
		}
		
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
		
		return ApiResult.success(ResponseCode.OK, result);
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
			return ApiResult.error(ResponseCode.NOT_EXIST_STORE);
		}
		
		int personaCnt = personaMapper.existPersona(loginIdx);
		if(personaCnt == 0) {
			return ApiResult.error(ResponseCode.NOT_EXIST_PERSONA);
		}
		
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		answerDto.setStoreIdx(storeIdx);
		Map<String, Object> personaInfo = personaMapper.getPersonaInfo(storeIdx);
		
		// 리뷰 분류
		String prompt = clovaService.readPromptFileToJson("answerGenerate/reviewAnalyze.json", answerDto.getReviewText());
		String answer = clovaService.generateChatResponse(prompt, "HCX-003");
		answerDto.setReviewType(answer);
		System.out.println("type --> " + answer);
		
		answerDto.setPersonaIdx((int) personaInfo.get("personaIdx"));
		
		// 1. 감정 + 길이에 맞게 리뷰 답변 생성
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath((String) personaInfo.get("emotionSelect"), (String) personaInfo.get("lengthSelect"));
		
		prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), answerDto.getReviewText());
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"));
		System.out.println("baseAnswer --> " + baseAnswer);
		
		// 2. 페르소나에 맞게 리뷰 답변 생성
		String persona = (String) personaInfo.get("personaSelect");
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		//TODO 필수 문구 부분.. 생각
		String content = "";
		if(answerDto.getIncludeText() != null || answerDto.getIncludeText().strip() != "") {
			content += "필수로 들어가야 하는 문구 : " + answerDto.getIncludeText() + "\n\n";
		}
		content += baseAnswer;
		
		prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), content);
		answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"));
		answerDto.setGenerateAnswer(answer);
		
		int result = answerMapper.insertAnswerGenerateLog(answerDto);
		return ApiResult.success(ResponseCode.OK, answerDto);
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

		// TODO 
		// 1. 감정 + 길이에 맞게 리뷰 답변 생성
		Map<String, Object> personaAdditionalPromptInfo = PromptUtil.emotionLengthPromptPath((String) personaInfo.get("emotionSelect"), (String) personaInfo.get("lengthSelect"));
		
		String prompt = clovaService.readPromptFileToJson((String) personaAdditionalPromptInfo.get("path"), answerDto.getReviewText());
		String baseAnswer = clovaService.generateChatResponse(prompt, (String) personaAdditionalPromptInfo.get("engine"));
		System.out.println("baseAnswer --> " + baseAnswer);
		
		// 2. 페르소나에 맞게 리뷰 답변 생성
		String persona = (String) personaInfo.get("personaSelect");
		Map<String, Object> personaPromptInfo = PromptUtil.personaPromtPath(persona);
		
		//TODO 필수 문구 부분.. 생각
				String content = "";
				if(answerDto.getIncludeText() != null || answerDto.getIncludeText().strip() != "") {
					content += "필수로 들어가야 하는 문구 : " + answerDto.getIncludeText() + "\n\n";
				}
				content += baseAnswer;		
		prompt = clovaService.readPromptFileToJson((String) personaPromptInfo.get("path"), content);
		String answer = clovaService.generateChatResponse(prompt, (String) personaPromptInfo.get("engine"));
		answerDto.setGenerateAnswer(answer);
		
		int cnt = answerMapper.updateAnswerGenerateLog(answerDto);

		return ApiResult.success(ResponseCode.OK, answerDto);
	}
	
	/**
	 * 생성한 답변 히스토리 보기
	 * @param loginIdx
	 * @return
	 */
	public ApiResult<Object> getAnswerLog(Integer loginIdx) {
		
		List<Map<String, Object>> result = answerMapper.getAnswerGenerateLogList(loginIdx);
		
		return ApiResult.success(ResponseCode.OK, result);
	}

}
