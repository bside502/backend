package com.bside.redaeri.vo;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 응답 코드
 */

public enum ResponseCode {
	// Success
	OK("200", HttpStatus.OK, "OK"),
	
	// fail
	FAIL("400", HttpStatus.BAD_REQUEST, "FAIL"),
	
	
	
	// 99
	EXPIRE_TOKEN("99", HttpStatus.UNAUTHORIZED, "EXPIRE_TOKEN"),
	;
	
	private final String code;
	private final HttpStatus httpStatus;
	private final String message;
	
	ResponseCode(String code, HttpStatus httpStatus, String message) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
	
	public String getMessage() {
		return message;
	}
}