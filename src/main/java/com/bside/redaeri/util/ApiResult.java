package com.bside.redaeri.util;

import com.bside.redaeri.vo.ResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "공통 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {
	
	private String code;
	private String message;
	private ResponseCode responseCode;
    private T data;
    
    public ApiResult(ResponseCode responseCode, T data) {
    	this.code = responseCode.getCode();
    	this.message = responseCode.getMessage();
    	this.data = data;
    }
    
    public static <T> ApiResult<T> success(ResponseCode responseCode, T data) {
        return new ApiResult<>(responseCode, data);
    }
    
    public static <T> ApiResult<T> error(ResponseCode responseCode) {
        return new ApiResult<>(responseCode, null);
    }
	
}