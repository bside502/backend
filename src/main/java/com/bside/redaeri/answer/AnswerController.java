package com.bside.redaeri.answer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bside.redaeri.login.LoginIdx;
import com.bside.redaeri.util.ApiResult;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1")
public class AnswerController {
	
	@Autowired
	private AnswerService answerService;
	
	/**
	 * clova ocr 이미지 텍스트 읽기
	 * @param mFile
	 * @return
	 * @throws IOException
	 */
	@SecurityRequirement(name = "token")
	@PostMapping("/image/text/read")
	public ApiResult<Object> imageTextReader(@RequestParam("reviewImgFile") MultipartFile mFile) throws IOException {
		
		return answerService.readImageToText(mFile);
	}
	
	/**
	 * 답변 생성하기
	 * @param loginIdx
	 * @param answerDto
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	@SecurityRequirement(name = "token")
	@PostMapping("/answer/generate")
	public ApiResult<Object> answerGenerate(@LoginIdx Integer loginIdx, @RequestBody AnswerDto answerDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		return answerService.generateAnswer(loginIdx, answerDto);
	}
	
	/**
	 * 답변 재생성하기
	 * @param answerDto
	 * @return
	 * @throws IOException 
	 * @throws io.jsonwebtoken.io.IOException 
	 */
	@SecurityRequirement(name = "token")
	@PatchMapping("/answer/generate/retry")
	public ApiResult<Object> answerRetry(@RequestBody AnswerDto answerDto) throws io.jsonwebtoken.io.IOException, IOException {
		
		return answerService.retryAnswer(answerDto);
	}
	
	/**
	 * 생성한 답변 히스토리 보기
	 * @param loginIdx
	 * @return
	 */
	@SecurityRequirement(name = "token")
	@GetMapping("/answer/log/get")
	public ApiResult<Object> answerLogGet(@LoginIdx Integer loginIdx) {
		
		return answerService.getAnswerLog(loginIdx);
	}
	
	
}
