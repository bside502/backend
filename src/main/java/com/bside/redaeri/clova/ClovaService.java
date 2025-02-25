package com.bside.redaeri.clova;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class ClovaService {
	
	// clova studio
	public static void generateChatResponse(String[] args) {
        String urlString = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-DASH-001";
        String apiKey = "<api-key>";  // 여기에 API 키 입력
        String requestId = "8f5ccf25af4b4c639642c9fcfd330719";

        String jsonInputString = "{"
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"\"},"
                + "{\"role\": \"user\", \"content\": \"\"}"
                + "],"
                + "\"topP\": 0.8,"
                + "\"topK\": 0,"
                + "\"maxTokens\": 256,"
                + "\"temperature\": 0.5,"
                + "\"repeatPenalty\": 5.0,"
                + "\"stopBefore\": [],"
                + "\"includeAiFilters\": true,"
                + "\"seed\": 0"
                + "}";

        try {
            // URL 설정
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setDoOutput(true);

            // 요청 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
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
}
