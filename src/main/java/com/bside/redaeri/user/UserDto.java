package com.bside.redaeri.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원 정보를 담고 있는 DTO")
public class UserDto {
	
	@Schema(description = "회원의 고유 idx", example = "1")
	private int idx;
	
	@Schema(description = "회원 id", example = "")
	private String userId;
	
	@Schema(description = "가게의 고유 idx", example = "100")
	private int storeIdx;
	
	@Schema(description = "가게 상호명", example = "맛있는 식당")
	private String storeName;
	
	@Schema(description = "가게 음식 종류", example = "한식")
	private String storeType;
	
	@Schema(description = "현재 페르소나 인물 이름", example = "")
	private String personaName;
	
	@Schema(description = "페르소나의 고유 idx", example = "10")
	private int personaIdx;
}

