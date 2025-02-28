package com.bside.redaeri.persona;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "페르소나 분석 및 생성된 답변 DTO")
public class PersonaDto {

    @Schema(description = "생성된 페르소나 idx", example = "1")
    private int idx;
    
    @Schema(description = "가게 idx", example = "1")
    private int storeIdx;
    
    @Schema(description = "로그인 한 회원 idx", example = "1")
    private int loginIdx;

    @Schema(description = "페르소나 이미지 종류", example = "1")
    private int personaImgType;

    @Schema(description = "이모티콘 사용 스타일", example = "다다익선! 많은 이모티콘")
    private String emotionSelect;

    @Schema(description = "답변 길이 스타일", example = "정성이 담긴 장문")
    private String lengthSelect;

    @Schema(description = "페르소나 유형", example = "열정 넘치는 2030 청년 사장님")
    private String personaSelect;

    @Schema(description = "만능 답변", example = "방문해 주셔서 감사합니다! 앞으로 더 좋은 서비스로 보답하겠습니다 😊")
    private String allAnswer;
}