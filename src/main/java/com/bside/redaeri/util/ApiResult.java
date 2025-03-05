package com.bside.redaeri.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "공통 응답 DTO")
public class ApiResult<T> {
	
	@Schema(description = "응답 상태", example = "success")
    private String code;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "실제 데이터")
    private T data;
    
    public static <T> ApiResult<T> success(String code, String message, T data) {
        return new ApiResult<>(code, message, data);
    }
    
    public static <T> ApiResult<T> error(String code, String message) {
        return new ApiResult<>(code, message, null);
    }
	
}