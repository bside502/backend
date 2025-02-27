package com.bside.redaeri.clova;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
@Service
public class ClovaService {
	
	@Value("${clova.api.key}")
	private String API_KEY;
	
	@Value("${clova.request.id}")
	private String REQUEST_ID;
	
	private static final String API_URL = "https://your-api-gateway.apigw.ntruss.com/ocr/analyze";
	private static final String API_OCR_KEY = "발급받은 API Key";
	
	//clova studio
	public void generateChatResponse() {
        String urlString = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-DASH-001";
       

        String prompt = ClovaPromptTemplates.TEXT_PATTEN_ANALYZE;
        // todo 프롬프트 선택할 수 있도록
        
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
                try (java.util.Scanner scanner = new java.util.Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    scanner.useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    System.out.println("Response: " + response);
                }
            } else {
                System.out.println("Error: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// clova ocr
	public static String imageTextExtract(String base64Image) {
		RestTemplate restTemplate = new RestTemplate();
		
		//todo images 형태 검사 추출
	        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", API_OCR_KEY);
        
        String requestBody = "{ \"version\": \"V2\", \"images\": [{ \"format\": \"jpg\", \"name\": \"sample\", \"data\": \"" + base64Image + "\" }]}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
	}
}
