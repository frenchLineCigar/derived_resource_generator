package com.example.drg.service;

import com.example.drg.dao.GenFileDao;
import com.example.drg.dto.GenFile;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GenFileService {

	private final GenFileDao fileDao;

	public void saveMeta(GenFile file) {
		fileDao.saveMeta(file);
	}

	public GenFile getGenFileById(int id) {
		return fileDao.findGenFileById(id);
	}

	public GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return fileDao.findGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}

	public GenFile getGenFileByFileExtTypeCodeAndWidthAndHeight(String relTypeCode, int relId, String fileExtTypeCode, int width, int height) {
		return fileDao.findGenFileByFileExtTypeCodeAndWidthAndHeight(relTypeCode, relId, fileExtTypeCode, width, height);
	}

	public GenFile getGenFileByFileExtTypeCodeAndMaxWidth(String relTypeCode, int relId, String fileExtTypeCode, int maxWidth) {
		return fileDao.findGenFileByFileExtTypeCodeAndMaxWidth(relTypeCode, relId, fileExtTypeCode, maxWidth);
	}

	// 파일 저장
	public int save(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
	                String originFileName, String filePath) {

		// 파일 메타정보 생성
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(originFileName);
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(originFileName);
		String fileExt = Util.getFileExtFromFileName(originFileName);
		int fileSize = Util.getFileSize(filePath);
		String fileDir = Util.getNowYearMonthDateStr();

		int originWidth = 0, originHeight = 0; // 이미지 파일의 실제 너비, 높이 정보

		if (fileExtTypeCode.equals("img")) {
			try {
				BufferedImage bufferedImage = ImageIO.read(new File(filePath));
				originWidth = bufferedImage.getWidth();
				originHeight = bufferedImage.getHeight();
			} catch (IOException e) {
				log.error("Can't read input file!");
			}
		}

		GenFile genFile = GenFile.create(relTypeCode, relId, typeCode, type2Code, fileNo, fileSize, originFileName, fileExtTypeCode, fileExtType2Code, fileExt, fileDir, originWidth, originHeight);

		// 파일 메타정보 DB에 저장
		saveMeta(genFile);

		int newGenFileId = genFile.getId();
		String destFilePath = genFile.getFilePath();

		// 파일 저장
		saveOnDisk(filePath, destFilePath);

		return newGenFileId;
	}

	public void saveOnDisk(String filePath, String destFilePath) {
		Util.moveFile(filePath, destFilePath); // 다운로드한 파일을 정식경로(destFilePath)로 이동
	}


	// 파일 저장 v1
	/*public ResultData saveV1(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo, String originFileName, String tempFilePath) {

		// 1. 파일 메타 정보 DB에 저장
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(originFileName);
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(originFileName);
		String fileExt = Util.getFileExtFromFileName(originFileName);
		int fileSize = Util.getFileSize(tempFilePath);
		String fileDir = Util.getNowYearMonthDateStr();

		int mediaWidth = 0, mediaHeight = 0; // 이미지 파일의 실제 너비, 높이 정보

		if (fileExtTypeCode.equals("img")) {
			try {
				BufferedImage bufferedImage = ImageIO.read(new File(tempFilePath));
				mediaWidth = bufferedImage.getWidth();
				mediaHeight = bufferedImage.getHeight();
			} catch (IOException e) {
				log.error("Can't read input file!");
			}
		}

		GenFile genFile = GenFile.create(relTypeCode, relId, typeCode, type2Code, fileNo, fileSize, originFileName, fileExtTypeCode, fileExtType2Code, fileExt, fileDir, mediaWidth, mediaHeight);

		saveMeta(genFile); // DB에 저장
		int newGenFileId = genFile.getId(); //생성된 PK

		// 2. 실제 파일 저장
		String fileName = newGenFileId + "." + fileExt; // 파일명
		String destFileDirPath = App.getGenFileDirPath() + "/" + relTypeCode + "/" + fileDir; // 저장경로
		String destFilePath = destFileDirPath + "/" + fileName; // 파일경로 (저장경로 + 파일명)

		// 경로에 해당하는 디렉터리 없으면 생성
		File destFileDir = new File(destFileDirPath);
		if (! destFileDir.exists()) {
			destFileDir.mkdirs();
		}

		//임시경로(tempFilePath)에 다운로드된 파일을 정식경로(destFilePath)로 이동시킨다
		File tempFile = new File(tempFilePath);
		File destFile = new File(destFilePath);
		tempFile.renameTo(destFile); // Using Java IO

		return new ResultData("S-1", "성공하였습니다.", "id", newGenFileId);
	}*/

}
