package com.bside.redaeri.clova;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
@Service
public class ClovaService {
	
	@Value("${clova.api.key}")
	private String API_KEY;
	
	@Value("${clova.ocr.url}")
	private String API_URL;
	
	@Value("${clova.ocr.key}")
	private String API_OCR_KEY;
	
	
	/**
	 * 프롬프트 파일 읽기
	 * @param filePath
	 * @param content
	 * @return
	 * @throws IOException
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("unchecked")
	public String readPromptFileToJson(String filePath, String content) throws IOException, java.io.IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
        File file = resource.getFile();
        
		ObjectMapper objectMapper = new ObjectMapper();

		try {
            Map<String, Object> jsonMap = objectMapper.readValue(file, new TypeReference<>() {});
            List<Map<String, Object>> messages = (List<Map<String, Object>>) jsonMap.get("messages");

            // "messages" 배열 가져오기
            if (!messages.isEmpty()) {
                messages.get(messages.size() - 1).put("content", content);
            }
            
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);
		} catch (IOException e) {
			e.printStackTrace();
            return null; // 오류 발생 시 null 반환
		}
	}
	//clova studio
	
	/**
	 * @param text
	 * @param type
	 * @return 
	 */
	public String generateChatResponse(String prompt, String engine, String tuning) {
		
        String urlString = "https://clovastudio.stream.ntruss.com";
        
        if(tuning.equals("analyze")) {
        	urlString += "/testapp/v2/tasks/fv4a42an/chat-completions";
        } else if(tuning.equals("pleasantPersona")) {
        	urlString += "/testapp/v2/tasks/1gpvowmx/chat-completions";
        } else if(tuning.equals("happyPersona")) {
        	urlString += "/testapp/v2/tasks/a3rblhs2/chat-completions";
        } else {
        	urlString += "/testapp/v1/chat-completions/" + engine;
        }
        System.out.println("urlString --> " + urlString);
        
        String uuid = UUID.randomUUID().toString();  // 랜덤 UUID 생성
       
    	StringBuilder sb = new StringBuilder();

        try {
            // URL 설정
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("X-NCP-CLOVASTUDIO-REQUEST-ID", uuid);
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
                                
                            	String content = rootNode.path("message").path("content").asText();
                                
                                sb.append(content);
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
        return sb.toString().substring(0, sb.toString().length() / 2);
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
