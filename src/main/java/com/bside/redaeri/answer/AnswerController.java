package com.bside.redaeri.answer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class AnswerController {
	
	@Autowired
	private AnswerService answerService;
	
	@PostMapping("/image/text/read")
	public Map<String, Object> imageTextReader(@RequestParam Map<String, Object> param, MultipartFile file) {
		
		return answerService.readImageToText(param, file);
	}
	
	@PostMapping("/answer/generate")
	public Map<String, Object> answerGenerate(@RequestBody Map<String, Object> param) {
		
		return answerService.generateAnswer(param);
	}
}
