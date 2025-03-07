package com.bside.redaeri.vo;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 응답 코드
 */

public enum ResponseCode {
	// Success
	OK("200", HttpStatus.OK, "OK"),
	ISSUED("201", HttpStatus.OK, "새로 발급된 토큰을 사용해주세요"),
	
	
	
	// fail
	FAIL("400", HttpStatus.BAD_REQUEST, "FAIL"),
	
	// 1001 ~
	FAIL_ACCESSTOKEN_ISSUE("1001", HttpStatus.OK, "accessToken 발급 실패"),
	FAIL_ADD_USER("1002", HttpStatus.OK, "회원 가입 실패"),
	NOT_EXIST_USER("1003", HttpStatus.OK, "존재하지 않는 회원입니다."),
	FAIL_NAVER_UNLINK("1004", HttpStatus.OK, "네이버 철회 오류"),
	
	
	// 2001 ~
	NOT_EXIST_STORE("2001", HttpStatus.OK, "등록된 가게가 없습니다."),
	
	EXIST_STORE("2002", HttpStatus.OK, "이미 등록된 가게가 존재합니다"), // 삭제 예정
	
	// 3001 ~
	NOT_EXIST_PERSONA("3001", HttpStatus.OK, "등록된 페르소나가 없습니다."),
	EXIST_PERSONA("3002", HttpStatus.OK, "이미 페르소나가 존재합니다."),
	
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