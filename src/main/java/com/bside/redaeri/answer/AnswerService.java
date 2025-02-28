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

import com.bside.redaeri.clova.ClovaPromptTemplates;
import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.persona.PersonaDto;
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
	
	public ApiResult<Object> readImageToText(MultipartFile mFile) throws IOException {
		
		String name = mFile.getOriginalFilename();
		String format = name.substring(name.lastIndexOf(".") + 1, name.length());
		
		System.out.println(name + ": " + format);
		
		Map<String, Object> imgInfo = new HashMap<>();
		
		imgInfo.put("requestId", UUID.randomUUID().toString());
		imgInfo.put("timestamp", Instant.now().toEpochMilli());
		imgInfo.put("format", format);
		imgInfo.put("name", name);
		imgInfo.put("data", Base64.getEncoder().encodeToString(mFile.getBytes()));
		
		String answer = clovaService.imageTextExtract(imgInfo);
		
		System.out.println("answer --> " + answer);
		
		Map<String, Object> result = new HashMap<>();
		result.put("reviewText", answer);
		
		return ApiResult.success("200", "성공", result);
	}
	
	public ApiResult<Object> generateAnswer(Integer loginIdx, AnswerDto answerDto) {
		
		// answerDTO 값 넘겨주기
		int storeIdx = personaMapper.getStoreIdx(loginIdx);
		Map<String, Object> personaInfo = personaMapper.getPersonaInfo(storeIdx);
		
		String prompt = ClovaPromptTemplates.ANSWER_GENERATE(answerDto, personaInfo);
		String answer = clovaService.generateChatResponse(prompt);
		System.out.println("answer --> " + answer);
		/**
		 * 1. clova 에 요청
		 * 2. 답변 받고 DB 저장 후 return
		 */
		// type 받아오기 
		answerDto.setReviewType(answer);
		answerDto.setGenerateAnswer(answer);
		
		//int cnt = answerMapper.insertAnswerGenerateLog(answerDto);
		
		
		return ApiResult.success("200", "성공", null);
	}
	
	public ApiResult<Object> getAnswerLog(Integer loginIdx) {
		
		List<Map<String, Object>> result = answerMapper.getAnswerGenerateLogList(loginIdx);
		
		return ApiResult.success("200", "성공", result);
	}

}
