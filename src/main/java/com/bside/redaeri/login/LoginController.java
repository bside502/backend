package com.bside.redaeri.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.user.UserDto;
import com.bside.redaeri.user.UserMapper;
import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.vo.ResponseCode;

@RestController
@RequestMapping("/api/v1/")
public class LoginController {
	
	@Autowired
	private JWTService jwtService;
	
	@Value("${naver.client.id}")
	private String NAVER_CLIENT_ID;
	
	@Value("${naver.client.key}")
	private String NAVER_CLIENT_SECRET;

	private static String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	
	//private static String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
	//private static String NAVER_CALLBACK_URL = "http://localhost:5671/login-callback";
	
	@Autowired
	private UserMapper userMapper;
	
	@PostMapping("/naver/callback")
	public ApiResult<Object> naverCallback(@RequestBody LoginDto loginDto) throws Exception {
		System.out.println("code--> " + loginDto.getCode() + " : state --> " + loginDto.getState());
		String accessToken = getAccessToken(loginDto.getCode(), loginDto.getState());
		System.out.println("accessToken --> " + accessToken);
		
		
		if(accessToken == null) {
			return ApiResult.error(ResponseCode.FAIL_ACCESSTOKEN_ISSUE);
		}
		
		Map<String, Object> userInfo = new HashMap<>();
		Integer userIdx = userMapper.existUser(accessToken);
		if(userIdx == null) { //회원 x
			UserDto userDto = new UserDto();
			userDto.setUserId(accessToken);
			int idx = userMapper.insertUser(userDto);
			if(idx == 1) {
				userInfo.put("loginIdx", userDto.getIdx());
			} else {
				return ApiResult.error(ResponseCode.FAIL_ADD_USER);
			}
		} else { // 회원 o
			userInfo.put("loginIdx", userIdx);
		}
		String jwtToken = jwtService.generateToken(userInfo);
		userInfo.put("token", jwtToken);
		
		return ApiResult.success(ResponseCode.OK, userInfo);
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
			System.out.println("result ==> " + response.toString());
			// JSON 파싱하여 access_token만 추출
            JSONObject jsonResponse = new JSONObject(response.toString());

            return jsonResponse.getString("access_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
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
	*/
}
