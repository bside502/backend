package com.bside.redaeri.answer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "답변 생성 DTO")
public class AnswerDto {
	@Schema(description = "답변 idx", example = "1")
    private int logIdx;

    @Schema(description = "별점 1 ~ 5", example = "5")
    private int score;

    @Schema(description = "생성된 답변", example = "이 제품 정말 마음에 들어요!")
    private String generateAnswer;

    @Schema(description = "(긍정/부정/문의/기타)", example = "긍정")
    private String reviewType;

    @Schema(description = "업로드한 텍스트", example = "이 제품을 사용해봤는데 품질이 정말 좋아요.")
    private String reviewText;

    @Schema(description = "생성일", example = "")
    private String insertDate;
}
