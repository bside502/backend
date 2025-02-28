// ClovaPromptTemplates
package com.bside.redaeri.clova;

import java.util.Map;

import com.bside.redaeri.answer.AnswerDto;
import com.bside.redaeri.persona.PersonaDto;

public class ClovaPromptTemplates {
	
	public static String TEXT_PATTHEN_ANALYZE_PROMPT(String content) {
		String TEXT_PATTEN_ANALYZE =
				"{"
                + "\"messages\": ["
                + "{"
                + "  \"role\": \"system\","
                + "  \"content\": \"입력된 답변 샘플들을 바탕으로\\n"
                + "  **인물 유형(persona)** \\n"
                + "  **이모티콘 사용 빈도(emotion)** \\n"
                + "  **답변 길이(length)** \\n"
                + "  파악하고 해당 스타일을 반영한 만능 리뷰 답변(answer)을 생성하세요. "
                + "  하나의 결과만 출력하고 JSON 형식으로 생성하세요.\\n"
                + "  \\n"
                + " **답변 샘플 (스타일 파악) **\\n"
                + content
                + "  **인물 유형 (답변의 어투와 태도 분석)** \\n"
                + "  - 해피바이러스! 발랄한 20대 알바생 \\n"
                + "  - 파이팅 넘치는 2030 청년사장님 \\n"
                + "  - 예의 바르고 나이스한 30대 초보 사장님 \\n"
                + "  - 단골 챙기는 정 많고 유쾌한 40대 사장님 \\n"
                + "  - 묵묵히 음식에 최선을 다하는 60대 사장님 \\n"
                + "  \\n"
                + "  **이모티콘 사용 빈도 (이모티콘 활용 스타일 분석)** \\n"
                + "  - 다다익선! 많으면 많을 수록 좋아요 \\n"
                + "  - 적당~히. 한 문장에 하나 ! \\n"
                + "  - 맥락에 맞게 딱 한 개만. \\n"
                + "  - 굳이? \\n"
                + "  \\n"
                + "  **답변 길이 (내용의 상세 정도 분석)** \\n"
                + "  - 정성이 최고! 긴 ~ 답변 \\n"
                + "  - 길지도 짧지도 않게 알잘딱깔센 \\n"
                + "  - 짧지만 핵심만 간단하게! 단문 답변 \\n"
                + "  \\n"
                + "  **# 예시** \\n"
                + "  **예시 input :** 샘플 답변 1: \\n"
                + "  방문해 주셔서 감사합니다. \\n"
                + "  \\n"
                + "  샘플 답변 2: \\n"
                + "  맛있게 드셔주셔서 감사합니다. \\n"
                + "  \\n"
                + "  샘플 답변 3: \\n"
                + "  기대에 미치지 못해 죄송합니다. 앞으로 더 노력하겠습니다. \\n"
                + "  \\n"
                + "  **예시 output** \\n"
                + "  persona : 묵묵히 음식에 최선을 다하는 60대 사장님 \\n"
                + "  emotion : 굳이? \\n"
                + "  length : 짧지만 핵심만 간단하게! 단문 답변 \\n"
                + "  answer : 방문해 주셔서 감사합니다. 열심히 하겠습니다.\""
                + "},"
                + "{"
                + "  \"role\": \"user\","
                + "  \"content\": \"\""
                + "}"
                + "],"
                + "\"topP\": 0.8,"
                + "\"topK\": 0,"
                + "\"maxTokens\": 256,"
                + "\"temperature\": 0.5,"
                + "\"repeatPenalty\": 5.0,"
                + "\"stopBefore\": [],"
                + "\"includeAiFilters\": true,"
                + "\"seed\": 0"
                + "}";;
		
		return TEXT_PATTEN_ANALYZE;
	}
	
	// 리뷰 분석 직접 선택 시 만능답변만 생성
	public static String GENERATE_ALL_ANSWER_PROMPT(PersonaDto personaDto) {
		String GENERATE_ALL_ANSWER_PROMPT =
				"{\n" +
			    "  \"messages\" : [ {\n" +
			    "    \"role\" : \"system\",\n" +
			    "    \"content\" : \"다음 정보를 가지고 알맞는 언제 어디서나 쓰일 수 있는 만능 답변을 생성해주세요.\n\n\n## 예시 \n **input**\n** 인물 : 파이팅 넘치는 2030 청년 사장님\n** 감정 : 고객님께 감사해요\n** 답변 길이 : 정성이 담긴 장문\n\n## 예시\n**output**\n감사합니다! 💕 정말 맛있게 드셨나봐요!  장어가지덮밥과 모밀 모두 정갈하고 깔끔한 맛이라 많은 분들이 좋아해주시는 메뉴에요! 특히 가지와 장어의 조합이 생소하지만 입 안 가득 풍미가 느껴지는 특별한 맛이죠! 거기다 저희 가게만의 특제 양념 소스까지 어우러져 더욱 맛있는 한 끼 식사가 되셨을 거라 생각하니 저도 기분이 좋네요! 😊 \n모밀 또한 시원하면서도 깊은 육수맛이 일품이라 여름엔 더더욱 인기가 많은데 이렇게 찾아주시고 칭찬해주시니 그저 감사할 따름입니다! 🙏 앞으로도 변함 없는 맛과 서비스로 보답할게요! ☺️ \n다음에도 꼭 재방문 부탁드리며 오늘도 행복한 하루 보내시길 바랍니다! 🌸\n\"\n" +
			    "  }, {\n" +
			    "    \"role\" : \"user\",\n" +
			    "    \"content\" : \"인물 : " + personaDto.getPersonaSelect() + "\n감정 : " + personaDto.getEmotionSelect() + "\n답변 길이 : " + personaDto.getLengthSelect() + "\"\n" +
			    "  } ],\n" +
			    "  \"topP\" : 0.8,\n" +
			    "  \"topK\" : 0,\n" +
			    "  \"maxTokens\" : 256,\n" +
			    "  \"temperature\" : 0.5,\n" +
			    "  \"repeatPenalty\" : 5.0,\n" +
			    "  \"stopBefore\" : [ ],\n" +
			    "  \"includeAiFilters\" : true,\n" +
			    "  \"seed\" : 0\n" +
			    "}";
		
		return GENERATE_ALL_ANSWER_PROMPT;
	}
	// 답변 생성하기
	public static String ANSWER_GENERATE(AnswerDto answerDto, Map<String, Object> personaInfo) {
		String ANSWER_GENERATE_PROMPT = "{\n" +
				  " \"messages\" : [ {\n" +
				  " \"role\" : \"system\",\n" +
				  " \"content\" : \"다음 리뷰를 파악하여 리뷰의 상태를 출력하고 사용자의 성격을 참고하여 답변을 생성하세요. " +
				  "리뷰의 상태는 (긍정/부정/중립/문의) 중 하나이다. 결과는 json형태로 출력하세요.\\n\\n\\n" +
				  "##예시 입력\\n" +
				  "리뷰 : 부드럽고 촉촉한 케이크에 달콤한 크림이 완벽하게 어우러져요! 한 입 먹자마자 행복해지는 맛입니다. 😊🎂\\n\\n" +
				  "리뷰 별점(5점 만점) : 5\\n\\n" +
				  "답변 생성시 필수 문구 : \\\"저희 매장 빵은 모두 당일 생산입니다!\\\"\\n\\n" +
				  "**사용자의 성격**\\n" +
				  "성격 : 파이팅 넘치는 2030 청년 사장님\\n" +
				  "감정 : 고객님의 리뷰가 힘이 돼요💪\\n" +
				  "원하는 답변길이 : 짧지만 핵심만 간단하게! 단문 답변\\n\\n" +
				  "## 예시 결과\\n" +
				  "{ \\\"type\\\": \\\"긍정\\\", \\\"answer\\\": \\\"" +
				  "감사합니다! 저희 매장 빵은 모두 당일 생산입니다! 💪 고객님의 리뷰가 큰 힘이 되네요! " +
				  "주신 말씀처럼 항상 신선한 재료와 정성으로 준비해서 맛있고 건강한 음식을 대접하겠습니다! " +
				  "다음 방문 때도 기대해주세요~😊\\\"}\"\n" +
				  " }, {\n" +
				  " \"role\" : \"user\",\n" +
				  " \"content\" : \"**리뷰**\\n" +
				  "\\\"\" + answerDto.getReviewText() + \"\\\"\\n\\n" +
				  "**리뷰 별점**\\n\" + answerDto.getScore() + \"\\n\\n" +
				  "**답변 생성시 필수 문구**\\n\" + answerDto.getIncludeText() + \"\\n\\n" +
				  "**사용자의 성격**\\n" +
				  "성격 : \" + personaInfo.get(\"personaSelect\") + \"\\n" +
				  "감정 : \" + personaInfo.get(\"emotionSelect\") + \"\\n" +
				  "원하는 답변길이 : \" + personaInfo.get(\"lengthSelect\") + \"\\n\"\n" +
				  " } ],\n" +
				  " \"topP\" : 0.8,\n" +
				  " \"topK\" : 0,\n" +
				  " \"maxTokens\" : 256,\n" +
				  " \"temperature\" : 0.5,\n" +
				  " \"repeatPenalty\" : 5.0,\n" +
				  " \"stopBefore\" : [ ],\n" +
				  " \"includeAiFilters\" : true,\n" +
				  " \"seed\" : 0\n" +
				  "}";
		
		return ANSWER_GENERATE_PROMPT;
	}
}
