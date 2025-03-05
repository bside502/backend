// ClovaPromptTemplates
package com.bside.redaeri.clova;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.bside.redaeri.answer.AnswerDto;
import com.bside.redaeri.persona.PersonaDto;
import com.bside.redaeri.store.StoreDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

public class ClovaPromptTemplates {
	public static String TEXT_PATTHEN_ANALYZE_PROMPT(String content) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("messages",  new Object[] {
			Map.of("role", "system", "content", "입력된 답변 샘플들을 바탕으로 \\r\\n**인물 유형\\r\\n**감정\\r\\n** 답변 길이\\r\\n파악하고 해당 스타일을 반영한 만능 리뷰 답변을 생성하세요.\\r\\n\\r\\n**인물 유형 (답변의 어투와 태도 분석)\\r\\n - 해피바이러스! 발랄한 20대 알바생 : ~용과 같은 애교스러운 어미를 한 번이라도 사용한 사람\\r\\n - 예의 바르고 나이스한 30대 초보 사장님 : 완벽한 표준어를 사용하며, 이모티콘을 많이 사용하지 않고, 차분한 성격임\\r\\n - 단골 챙기는 정 많고 유쾌한 40대 사장님 : ~거덩요, ^^, ^0^ 등의 어투 주로 사용\\r\\n - 묵묵히 음식에 최선을 다하는 60대 사장님 : ...와 같은 말줄임표를 많이 사용하며 문장의 끝에 꼭 온점을 사용\\r\\n - 충청도 출신 외식업 강자 사장님 : 충청도 사투리가 심하다 어미는 ~했어유, 했구먼 스타일로 끝맺는다.\\r\\n\\r\\n**감정 선택 (답변 스타일)\\r\\n - 감사\\n - 행복\\n - 격려\\r\\n\\r\\n**답변 길이 (내용의 상세 정도 분석)\\r\\n - 정성이 최고! 긴 ~ 답변\\r\\n - 길지도 짧지도 않게 알잘딱깔센\\r\\n - 짧지만 핵심만 간단하게! 단문 답변\\r\\n\\r\\n# 예시\\r\\n**input : \\r\\n방문해 주셔서 감사합니다.\\r\\n\\r\\n맛있게 드셔주셔서 감사합니다.\\r\\n\\r\\n기대에 미치지 못해 죄송합니다. 앞으로 더 노력하겠습니다.\\r\\n\\r\\n**output\\r\\n인물 유형 : 묵묵히 음식에 최선을 다하는 60대 사장님\\r\\n감정 : 감사\\r\\n답변 길이 : 짧지만 핵심만 간단하게! 단문 답변\\r\\n만능 답변 : 방문해 주셔서 감사합니다. 열심히 하겠습니다.\\r\\n\\r\\n"),
			Map.of("role", "user", "content", content)
		});
		requestBody.put("max_tokens", 256);
        requestBody.put("temperature", 0.5);
        requestBody.put("top_p", 0.8);
        requestBody.put("repeat_penalty", 5.0);
		
		String TEXT_PATTEN_ANALYZE =
				"{"
                + "\"messages\": ["
                + "{"
                + "  \"role\": \"system\","
                + "  \"content\": \"입력된 답변 샘플들을 바탕으로\\n"
                + "  **인물 유형(persona)** \\n"
                + "  **감정(emotion)** \\n"
                + "  **답변 길이(length)** \\n"
                + "  파악하고 해당 스타일을 반영한 만능 리뷰 답변(answer)을 생성하세요. "
                + "  하나의 결과만 출력하고 JSON 형식으로 생성하세요.\\n"
                + "  \\n"
                + " **답변 샘플 (스타일 파악) **\\n"
                + content
                + "  **인물 유형 (답변의 어투와 태도 분석)** \\n"
                + "  - 해피바이러스! 발랄한 20대 알바생 : \"~용\"과 같은 애교스러운 어미를 한번이라도 사용한 사람 \\n"
                + "  - 예의 바르고 나이스한 30대 초보 사장님 : 완벽한 표준어를 사용하며, 이모티콘을 많이 사용하지 않고, 차분한 성격임 \\n"
                + "  - 단골 챙기는 정 많고 유쾌한 40대 사장님 : \"~거덩요\". \"^^\", \"^O^\" 등의 어투 주로 사용 \\n"
                + "  - 묵묵히 음식에 최선을 다하는 60대 사장님 : \"...\"와 같은 말줄임표를 많이 사용하며, 문장의 끝에 꼭 \".\"과 같은 온점을 붙임 \\n"
                + "  - 조보아씨 이리와봐유 충청도 출신 외식업 강자 사장님 : 충청도 사투리가 심하다. 어미는 \"~했어유\", \"했구먼\" 스타일로 끝맺는다. \\n"
                + "  \\n"
                + "  **감정** \\n"
                + "  - 고객님께 감사해요 \\n"
                + "  - 고객님 덕분에 행복해요 ! \\n"
                + "  - 고객님의 리뷰가 힘이 돼요 \\n"
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
	public static String GENERATE_ALL_ANSWER_PROMPT(PersonaDto personaDto, StoreDto storeDto) {
		String GENERATE_ALL_ANSWER_PROMPT =
				"{\n" +
			    "  \"messages\" : [ {\n" +
			    "    \"role\" : \"system\",\n" +
			    "    \"content\" : \"다음 정보를 가지고 알맞는 언제 어디서나 쓰일 수 있는 만능 답변을 생성해주세요.\n\n\n## 예시 \n **input**\n** 인물 : 파이팅 넘치는 2030 청년 사장님\n** 감정 : 고객님께 감사해요\n** 답변 길이 : 정성이 담긴 장문\n\n## 예시\n**output**\n소중한 후기 남겨주셔서 감사합니다! 😊 고객님의 의견이 저희에게 큰 힘이 됩니다. 앞으로도 더 좋은 서비스와 만족을 드릴 수 있도록 최선을 다하겠습니다. 또 이용해 주실 날을 기다리겠습니다! 감사합니다. 🌸\n\"\n" +
			    "  }, {\n" +
			    "    \"role\" : \"user\",\n" +
			    "    \"content\" : \"가게 이름 : " + storeDto.getStoreName() + "\n가게 종류 :" + storeDto.getStoreType() + "\n인물 : " + personaDto.getPersonaSelect() + "\n감정 : " + personaDto.getEmotionSelect() + "\n답변 길이 : " + personaDto.getLengthSelect() + "\"\n" +
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
	
	// 답변 생성하기 리뷰 분류
	public static String ANSWER_GENERATE(String fileName, String content) throws IOException, java.io.IOException {
		ClassPathResource resource = new ClassPathResource(fileName);
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
}
