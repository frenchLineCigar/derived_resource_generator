package com.example.drg.service;

import com.example.drg.dao.GenFileDao;
import com.example.drg.dto.GenFile;
import com.example.drg.dto.ResultData;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GenFileService {

	private final GenFileDao fileDao;

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	public void saveMeta(GenFile file) {
		fileDao.saveMeta(file);
	}

	public GenFile getGenFileById(int id) {
		return fileDao.findGenFileById(id);
	}

	public GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return fileDao.findGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}

	// Using Java NIO
	public ResultData save(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo, String originFileName, String downloadedFilePath) {

		// 파일 메타정보 생성
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(originFileName);
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(originFileName);
		String fileExt = Util.getFileExtFromFileName(originFileName);
		int fileSize = Util.getFileSize(downloadedFilePath);
		String fileDir = Util.getNowYearMonthDateStr();

		GenFile genFile = GenFile.create(relTypeCode, relId, typeCode, type2Code, fileNo, fileSize, originFileName, fileExtTypeCode, fileExtType2Code, fileExt, fileDir);

		// 파일 메타정보 DB에 저장
		saveMeta(genFile);

		int newGenFileId = genFile.getId(); //생성된 PK
		String fileName = newGenFileId + "." + fileExt; // 파일명
		String destFilePath = genFileDirPath + "/" + relTypeCode + "/" + fileDir + "/" + fileName; // 파일경로

		// 실제 파일 저장
		Util.moveFile(downloadedFilePath, destFilePath); // 다운로드한 파일을 정식경로(destFilePath)로 이동시킨다

		return new ResultData("S-1", "성공하였습니다.", "id", newGenFileId);
	}


	// Using ONLY Java IO
	/*public ResultData saveV1(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo, String originFileName, String tempFilePath) {

		// 1. 파일 메타 정보 DB에 저장
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(originFileName);
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(originFileName);
		String fileExt = Util.getFileExtFromFileName(originFileName);
		int fileSize = Util.getFileSize(tempFilePath);
		String fileDir = Util.getNowYearMonthDateStr();

		GenFile genFile = GenFile.create(relTypeCode, relId, typeCode, type2Code, fileNo, fileSize, originFileName, fileExtTypeCode, fileExtType2Code, fileExt, fileDir);
		saveMeta(genFile); // DB에 저장
		int newGenFileId = genFile.getId(); //생성된 PK

		// 2. 실제 파일 저장
		String fileName = newGenFileId + "." + fileExt; // 파일명
		String destFileDirPath = genFileDirPath + "/" + relTypeCode + "/" + fileDir; // 저장경로
		String destFilePath = destFileDirPath + "/" + fileName; // 파일경로 (저장경로 + 파일명)

		// 경로에 해당하는 디렉터리 없으면 생성
		File destFileDir = new File(destFileDirPath);
		if (! destFileDir.exists()) {
			destFileDir.mkdirs();
		}

		//임시경로(tempFilePath)에 다운로드된 파일을 정식경로(destFilePath)로 이동시킨다
		File tempFile = new File(tempFilePath);
		File destFile = new File(destFilePath);
		tempFile.renameTo(destFile);

		return new ResultData("S-1", "성공하였습니다.", "id", newGenFileId);
	}*/

}
