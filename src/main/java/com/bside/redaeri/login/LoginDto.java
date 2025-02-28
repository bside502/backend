package com.bside.redaeri.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 DTO")
public class LoginDto {
    @Schema(description = "naver code", example = "")
    private String code;
    
    @Schema(description = "naver status", example = "")
    private String status;
}
