package com.example.drg.dto;

import com.example.drg.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenFile {
	private int id;
	private String regDate;
	private String updateDate;
	private boolean delStatus;
	private String delDate;
	private String relTypeCode; //연관 데이터 타입 (Ex. article)
	private int relId; //연관 데이터 번호
	private String typeCode; //파일 종류 코드 (Ex. common)
	private String type2Code; //파일 종류2 코드 (Ex. attachment)
	private int fileNo; //파일 번호 (1 or 2) - 몇번째 첨부파일인지
	private int fileSize; //파일 크기(byte)
	private String originFileName; //원본 파일명
	private String fileExtTypeCode; //파일 규격 코드 (Ex. img, video)
	private String fileExtType2Code; //파일 규격2 코드 (Ex. jpg, mp4)
	private String fileExt; //파일 확장자
	private String fileDir; //파일 저장 폴더 : yyyy_MM (Ex. 2021_03)

	/* 단위 환산된 파일 크기 리턴 (byte, KB, MB) */
	public String getFileSizeWithUnit() {
		return Util.byteSizeTo(fileSize, 2); // 소수점 2자리까지
	}

	@JsonIgnore
	public String getFilePath(String fileDirPath) {
		return fileDirPath + getBaseFileUri();
	}

	@JsonIgnore
	private String getBaseFileUri() {
		return "/" + relTypeCode + "/" + fileDir + "/" + getFileName();
	}

	public String getFileName() {
		return id + "." + fileExt;
	}

	public String getForPrintUrl() {
		return "/file" + getBaseFileUri() + "?updateDate=" + updateDate;
	}

	public String getDownloadUrl() {
		return "/common/file/doDownload?id=" + id;
	}

	public static GenFile create(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
	                             int fileSize, String originFileName, String fileExtTypeCode, String fileExtType2Code, String fileExt, String fileDir) {

		return GenFile.builder()
				.relTypeCode(relTypeCode).relId(relId).typeCode(typeCode).type2Code(type2Code).fileNo(fileNo)
				.fileSize(fileSize).originFileName(originFileName).fileExtTypeCode(fileExtTypeCode).fileExtType2Code(fileExtType2Code).fileExt(fileExt).fileDir(fileDir)
				.build();
	}

}
