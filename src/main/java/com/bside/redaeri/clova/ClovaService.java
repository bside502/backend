package com.bside.redaeri.clova;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class ClovaService {
	
	@Value("${clova.api.key}")
	private String API_KEY;
	
	@Value("${clova.request.id}")
	private String REQUEST_ID;
	
	@Value("${clova.ocr.url}")
	private String API_URL;
	
	@Value("${clova.ocr.key}")
	private String API_OCR_KEY;
	
	//clova studio
	
	/**
	 * TPA : TextPattenAnalyze (리뷰 분석 프롬프트)
	 * AG : AnswerGenerate (답변 생성 프롬프트)
	 * GA : 말투 직접 선택 시 (만능 답변만 생성 프롬프트)
	 * @param text
	 * @param type
	 * @return 
	 */
	public String generateChatResponse(String prompt) {
        String urlString = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-DASH-001";
       
        System.out.println(prompt);
    	StringBuilder sb = new StringBuilder();

        try {
            // URL 설정
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("X-NCP-CLOVASTUDIO-REQUEST-ID", REQUEST_ID);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setDoOutput(true);

            // 요청 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = prompt.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            

            // 응답 본문 출력
            if (responseCode == HttpURLConnection.HTTP_OK) {
            	try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                
            		String line;
            		while ((line = reader.readLine()) != null) {
            			if (line.startsWith("data:")) {
                            try {
                                // "data:" 접두사 제거
                                String jsonStr = line.substring(5);
                                
                                // 빈 라인 건너뛰기
                                if (jsonStr.trim().isEmpty()) {
                                    continue;
                                }
                                
                                // JSON 파싱
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode rootNode = objectMapper.readTree(jsonStr);
                                
                                // content 값 추출 및 추가
                                String content = rootNode.path("message").path("content").asText();
                                sb.append(content);
                                
                                // 처리 로그 (선택적)
                            } catch (Exception e) {
                                System.err.println("JSON 파싱 오류: " + e.getMessage());
                            }
                        }
            		}
                }
            } else {
                System.out.println("Error: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
	
	// clova ocr
	public String imageTextExtract(Map<String, Object> imgInfo) {
		RestTemplate restTemplate = new RestTemplate();
		
		//todo images 형태 검사 추출
	        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", API_OCR_KEY);
        
        String requestBody = "{ \"version\": \"V2\", "
                + "\"requestId\": \"" + imgInfo.get("requestId") + "\", "
                + "\"timestamp\": " + imgInfo.get("timestamp") + ", "
                + "\"images\": ["
                + "{ \"format\": \"" + imgInfo.get("format") + "\", "
                + "\"name\": \"" + imgInfo.get("name") + "\", "
                + "\"data\": \"" + imgInfo.get("data") + "\" }]}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        StringBuilder result = new StringBuilder();
        try {
            JSONObject jsonObj = new JSONObject(response.getBody());
            
            // Check if the JSON contains the expected structure
            if (jsonObj.has("images")) {
                JSONArray images = jsonObj.getJSONArray("images");
                
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    
                    // Check if inferResult is SUCCESS
                    if ("SUCCESS".equals(image.getString("inferResult"))) {
                        // Get the fields array
                        if (image.has("fields")) {
                            JSONArray fields = image.getJSONArray("fields");
                            
                            for (int j = 0; j < fields.length(); j++) {
                                JSONObject field = fields.getJSONObject(j);
                                
                                // Extract inferText
                                if (field.has("inferText")) {
                                    String inferText = field.getString("inferText");
                                    result.append(inferText);
                                    
                                    // Add space or newline based on lineBreak flag
                                    if (field.has("lineBreak") && field.getBoolean("lineBreak")) {
                                    	result.append("\n");
                                    } else {
                                    	result.append(" ");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "Error parsing JSON: " + e.getMessage();
        }
        
        return result.toString().trim();
	}
}
