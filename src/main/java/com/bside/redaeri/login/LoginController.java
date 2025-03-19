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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bside.redaeri.filter.JWTService;
import com.bside.redaeri.user.UserDto;
import com.bside.redaeri.user.UserMapper;
import com.bside.redaeri.util.ApiResult;
import com.bside.redaeri.vo.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	private static String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
	
	@Autowired
	private UserMapper userMapper;
	
	@GetMapping("/naver/unlink")
	public ApiResult<Object> naverDelete(@LoginIdx Integer loginIdx) throws JsonMappingException, JsonProcessingException {
		String accessToken = userMapper.getUserAccessToken(loginIdx);
		
		 String url = NAVER_TOKEN_URL + "?grant_type=delete"
	                + "&client_id=" + NAVER_CLIENT_ID
	                + "&client_secret=" + NAVER_CLIENT_SECRET
	                + "&access_token=" + accessToken
	                + "&service_provider=NAVER";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        // 응답 본문을 JSON 문자열로 가져옴
        String responseBody = response.getBody();
        
        // ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);
        
        if(resultMap.get("result").equals("success")) {
        	// db 데이터 삭제 진행
        	userMapper.deleteAnswerGenerateLog(loginIdx);
        	userMapper.deletePersona(loginIdx);
        	userMapper.deleteStore(loginIdx);
        	userMapper.deleteUser(loginIdx);
        } else {
        	return ApiResult.error(ResponseCode.FAIL_NAVER_UNLINK);
        }
        
        return ApiResult.success(ResponseCode.OK, resultMap); // 응답 결과 반환
	}
	
	/* 로그인 테스트
	@GetMapping("/auth/naver")
	public String getNaverLoginUrl() {
        String loginUrl = "https://nid.naver.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + NAVER_CLIENT_ID +
                "&redirect_uri=" + "http://localhost:8080/api/v1/naver/callback" +
                "&state=" + false;

        return "네이버 로그인 URL: <a href='" + loginUrl + "' target='_blank'>" + loginUrl + "</a>";
    }
    */
    	
	@PostMapping("/naver/callback") //@RequestParam("code") String code, @RequestParam("state") String state
	public ApiResult<Object> naverCallback(@RequestBody LoginDto loginDto) throws Exception {
		String accessToken = getAccessToken(loginDto.getCode(), loginDto.getState());
		
		UserDto userDto = new UserDto();
		userDto.setAccessToken(accessToken);
		
		if(accessToken == null) {
			return ApiResult.error(ResponseCode.FAIL_ACCESSTOKEN_ISSUE);
		}
		
		Map<String, Object> userInfo = getUserProfile(accessToken);
		userDto.setUserId((String) userInfo.get("id"));
		// {response : {id : xxxx}}
		
		Integer userIdx = userMapper.existUser((String) userInfo.get("id"));
		
		if(userIdx == null) { //회원 x
			int cnt = userMapper.insertUser(userDto);
			if(cnt == 1) {
				userInfo.put("loginIdx", userDto.getIdx());
			} else {
				return ApiResult.error(ResponseCode.FAIL_ADD_USER);
			}
		} else { // 회원 o
			userMapper.updateUserToken(userDto);
			userInfo.put("loginIdx", userIdx);
		}
		userInfo.remove("id");
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
			// JSON 파싱하여 access_token만 추출
            JSONObject jsonResponse = new JSONObject(response.toString());

            return jsonResponse.getString("access_token");
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
