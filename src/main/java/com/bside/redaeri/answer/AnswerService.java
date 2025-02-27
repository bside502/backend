package com.bside.redaeri.answer;

import java.util.Map;

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
	
	public Map<String, Object> readImageToText(Map<String, Object> param, MultipartFile file) {
		
		/**
		 * 파일을 읽고
		 * ocr 에 보낸다.
		 * 
		 * 답변을 받고
		 * return
		 */
		
		return ResponseUtil.success();
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
