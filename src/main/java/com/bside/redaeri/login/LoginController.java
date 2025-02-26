package com.bside.redaeri.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/")
public class LoginController {
	
	@Autowired
	private JWTService jwtService;
	
	private static String NAVER_CLIENT_ID = "";
	private static String NAVER_CLIENT_SECRET = "";

	private static String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	private static String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
	
	@PostMapping("/naver/callback")
	public void naverCallback(@RequestBody Map<String, Object> param) throws IOException {
		try {
			String code = (String) param.get("code");
			String state = (String) param.get("state");

			if (code == null || state == null) {
				throw new Exception();
			}

			String accessToken = getAccessToken(code, state);

			Map<String, Object> userInfo = getUserProfile(accessToken);
			// todo 이미 등록된 회원인지 확인
			
			String jwtToken = jwtService.generateToken(userInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getAccessToken(String code, String state) throws IOException {
		String requestURL = NAVER_TOKEN_URL 
				+ "?grant_type=authorization_code"
				+ "&client_id=" + NAVER_CLIENT_ID
				+ "&client_secret=" + NAVER_CLIENT_SECRET 
				+ "&code=" + code 
				+ "&state=" + state;

		try {

			URL url = new URL(requestURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader br;
			int responseCode = con.getResponseCode();

			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}

			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			System.out.println("accessToken ==> " + response.toString());
			String accessToken = response.toString().split("\"access_token\":\"")[1].split("\"")[0];
			
			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> getUserProfile(String accessToken) {
		try {
			URL url = new URL(NAVER_USER_INFO_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", "Bearer " + accessToken);
		
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
		
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonMap = objectMapper.readValue(response.toString(), Map.class);
		     
			return (Map<String, Object>) jsonMap.get("response");	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
