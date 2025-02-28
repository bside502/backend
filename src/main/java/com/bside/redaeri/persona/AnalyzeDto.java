package com.bside.redaeri.persona;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "파일 및 텍스트 업로드 DTO")
public class AnalyzeDto {

    @Schema(description = "업로드할 파일 리스트")
    private List<MultipartFile> uploadFileList;

    @Schema(description = "첫 번째 텍스트", example = "첫 번째 내용")
    private String uploadTextFirst;

    @Schema(description = "두 번째 텍스트", example = "두 번째 내용")
    private String uploadTextSecond;

    @Schema(description = "세 번째 텍스트", example = "세 번째 내용")
    private String uploadTextThird;
}