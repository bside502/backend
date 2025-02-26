package com.bside.redaeri.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;

@RestController
@RequestMapping("/api/v1/")
public class KakaoController {
	
	@Autowired
	private JWTService jwtService;
	
	private static final String KAKAO_API_URL = "https://kauth.kakao.com/oauth/token";
	private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
	private static final String KAKAO_CLIENT_ID = "";
	private static final String KAKAO_REDIRECT_URI = "";
	
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
	
	public void kakaoCallback(@RequestBody Map<String, Object> param) throws IOException {
		try {
			String code = (String) param.get("code");

			if(code == null) {
				throw new Exception();
			}
			String accessToken = getAccessToken(code);
			
			Map<String, Object> userInfo = getUserProfile(accessToken);
			// todo 이미 등록된 회원인지 확인
			
			String jwtToken = jwtService.generateToken(userInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getAccessToken(String code) {
		String requestURL = KAKAO_API_URL
				+ "?grant_type=authorization_code"
				+ "&cliend_id=" + KAKAO_CLIENT_ID
				+ "&redirect_uri=" + KAKAO_REDIRECT_URI
				+ "&code=" + code;
		
		try {
			URL url = new URL(requestURL);
		
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			
			BufferedReader br;
			int responseCode = con.getResponseCode();
			
			if (responseCode == 200) {
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
			
			// Todo
			String accessToken = response.toString().split("\"access_token\":\"")[1].split("\"")[0];
			
			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private Map<String, Object> getUserProfile(String accessToken) {
		try {
			
		URL url = new URL(KAKAO_USER_INFO_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", "Bearer " + accessToken);
		con.setRequestProperty("Content-Type", CONTENT_TYPE);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		
		while ((inputLine = br.readLine()) != null) {
			response.append(inputLine);
		}
		br.close();
		
		System.out.println(response.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
