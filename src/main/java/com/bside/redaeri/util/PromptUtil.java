package com.bside.redaeri.util;

import java.util.HashMap;
import java.util.Map;

public class PromptUtil {
	
	/**
	 * 페르소나 인물 프롬프트 정보 가져오기
	 * @param persona ex) happyPErsoan, nicePersona, ...
	 * @return path: 프롬프트 경로, type: 이미지 타입, engine: 엔진명, personaName: 해피바이러스! ~~~
	 */
	public static Map<String, Object> personaPromtPath(String persona) {
		Map<String, Object> result = new HashMap<>();
		
		String promptPath = "persona/";
		String engine = "HCX-003";
		String personaName = "예의 바르고 나이스한 30대 초보 사장님";
		int type = 2;
		
		if(persona.equals("happyPersona")) {
			promptPath += "generateAnswer1.json";
			type = 1;
			personaName = "해피바이러스! 발랄한 20대 알바생";
		} else if(persona.equals("pleasantPersona")) {
			promptPath += "generateAnswer3.json";
			type = 3;
			personaName = "단골 챙기는 정 많고 유쾌한 40대 사장님";
		} else if(persona.equals("silentPersona")) {
			promptPath += "generateAnswer4.json";
			type = 4;
			personaName = "묵묵히 음식에 최선을 다하는 60대 사장님";
		} else if(persona.equals("chungcheongdoPersona")) {
			promptPath += "generateAnswer5.json";
			type = 5;
			personaName = "충청도 출신 외식업 강자 사장님";
		} else {
			promptPath += "generateAnswer2.json";
		}
		
		result.put("name", personaName);
		result.put("path", promptPath);
		result.put("engine", engine);
		result.put("type", type);
		
		return result;
	}
	
	/**
	 * 길이 + 감정 프롬프트 (페르소나 말투와 함께 쓰임)
	 * @param emotion ex) "감사", "기뻐", ... 바뀔 수 있음
	 * @param length ex) "장문", "중간" , ... 바뀔 수 있음
	 * @return
	 */
	public static Map<String, Object> emotionLengthPromptPath(String emotion, String length) {
		Map<String, Object> result = new HashMap<>();
		
		String path = "persona/additional/";
		String engine = "HCX-003";
		
		if(emotion.equals("thank")) {
			if(length.equals("long")) {
				path += "thankLong.json";
			} else if(length.equals("medium")) {
				path += "thankMedium.json";
			} else { // 단문
				path += "thankShort.json";
			}
		} else if(emotion.equals("happy")) {
			if(length.equals("long")) {
				path += "happyLong.json";
			} else if(length.equals("medium")) {
				path += "happyMedium.json";
			} else { // 단문
				path += "happyShort.json";
			}
		} else { // 격려
			if(length.equals("long")) {
				path += "cheerLong.json";
			} else if(length.equals("medium")) {
				path += "cheerMedium.json";
			} else { // 단문
				path += "cheerShort.json";
			}
		}
		
		result.put("path", path);
		result.put("engine", engine);
		return result;
	}
}
