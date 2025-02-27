package com.bside.redaeri.answer;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.clova.ClovaService;
import com.bside.redaeri.util.ResponseUtil;

@Service
public class AnswerService {
	
	@Autowired
	private ClovaService clovaService;
	
	@Autowired
	private AnswerMapper answerMapper;
	
	public Map<String, Object> readImageToText(MultipartFile mFile) throws IOException {
		
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
		result.put("text", answer);
		
		return ResponseUtil.success(result);
	}
	
	public Map<String, Object> generateAnswer(Map<String, Object> param) {
		
		/**
		 * 1. clova 에 요청
		 * 2. 답변 받고 DB 저장 후 return
		 */

		// 2
		int cnt = answerMapper.insertAnswerGenerateLog(param);
		
		return ResponseUtil.success();
	}
}
