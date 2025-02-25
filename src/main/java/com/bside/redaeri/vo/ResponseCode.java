package com.bside.redaeri.vo;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 응답 코드
 */

@Getter
public enum ResponseCode {
	// Success
	OK("200", HttpStatus.OK, "OK"),
	
	// fail
	FAIL("400", HttpStatus.BAD_REQUEST, "FAIL"),
	
	NOT_EXISTS_MEMBER("1001", HttpStatus.OK, "NOT_EXISTS_MEMBER"),
	
	// 첫 로그인 시 키워드 등록할 때 이미 키워드가 등록되어있는 경우
	ALREADY_CHOICE_KEYWORD("1002", HttpStatus.OK, "ALREADY_CHOICE_KEYWORD"),
	
	EMAIL_DUIPLICATE("1003", HttpStatus.OK, "EMAIL_DUPLICATE"),
	
	NOT_AUTH_EMAIL("1004", HttpStatus.OK, "NOT_AUTH_EMAIL"),
	
	INCORRECT_AUTH_NUMBER("1005", HttpStatus.OK, "INCORRECT_AUTH_NUMBER"),
	
	
	
	NOT_EXISTS_POST("2001", HttpStatus.OK, "NOT_EXISTS_POST"),
	ALREADY_POST_LIKE("2002", HttpStatus.OK, "ALREADY_POST_LIKE"),
	
	NOT_EXISTS_POST_LIKE("2003", HttpStatus.OK, "NOT_EXISTS_POST_LIKE"),
	
	SEND_FAIL_MESSAGE("5001", HttpStatus.OK, "SEND_FAIL_MESSAGE"),
	
	
	
	// token에 member_idx 를 찾을 수 없는 경우
	NOT_FIND_LOGIN_IDX("9999", HttpStatus.OK, "NOT_FIND_LOGIN_IDX")
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
}