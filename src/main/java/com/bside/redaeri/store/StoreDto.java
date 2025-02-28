package com.bside.redaeri.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "가게 정보 DTO")
public class StoreDto {
	@Schema(description = "가게 idx", example = "1")
	private int idx;
	
	@Schema(description = "로그인한 회원 idx", example = "1")
	private int loginIdx;
	
	@Schema(description = "회원 idx", example = "1")
	private int userIdx;
	
	@Schema(description = "가게 이름", example = "투썸")
	private String storeName;
	
	@Schema(description = "가게 종류", example = "카페")
	private String storeType;
	
	
}
