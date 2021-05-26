package com.example.drg.service;

import com.example.drg.app.App;
import com.example.drg.dao.DerivedRequestDao;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.dto.GenFile;
import com.example.drg.exception.DownloadFileFailException;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DerivedRequestService {

	private final DerivedRequestDao derivedRequestDao;
	private final GenFileService fileService;

	public DerivedRequest getDerivedRequestById(int id) {
		return derivedRequestDao.findDerivedRequestById(id);
	}

	public DerivedRequest findDerivedRequestByRequestUrl(String requestUrl) {
		return derivedRequestDao.findDerivedRequestByRequestUrl(requestUrl);
	}

	public DerivedRequest findFirstDerivedRequestByOriginUrl(String originUrl) {
		return derivedRequestDao.findFirstDerivedRequestByOriginUrl(originUrl);
	}

	public int save(String requestUrl, String originUrl, int width, int height, int maxWidth) {

		// 저장할 객체 생성
		DerivedRequest derivedRequest = DerivedRequest.create(requestUrl, originUrl, false, width, height, maxWidth);

		// 기존에 원본을 가지고 있는 요청이 있는지 조회
		DerivedRequest originDerivedRequest = findFirstDerivedRequestByOriginUrl(originUrl);

		if (originDerivedRequest == null) {
			derivedRequest.setOriginStatus(true); // 원본을 저장하는 요청
		}

		// 1. 요청 정보 저장
		derivedRequestDao.save(derivedRequest);
		int newDerivedRequestId = derivedRequest.getId(); //생성된 요청 아이디

		// 2. 원본 파일 저장 (원본을 저장하는 요청의 경우)
		if (derivedRequest.isOriginStatus()) {

			// URL로 부터 원본 파일을 다운로드 후 경로를 담는다
			String downloadedFilePath = null;
			try {
				downloadedFilePath = Util.downloadFileByHttp(originUrl, App.getTmpDirPath());
			} catch (FileNotFoundException e) {
				throw new DownloadFileFailException();
			}

			// 경로에서 파일명 추출
			String originFileName = Util.getFileNameFromUrl(originUrl);

			// 파일명에 확장자가 없는 경우
			if (originFileName.lastIndexOf(".") == -1) {
				originFileName = Util.getFileNameFromPath(downloadedFilePath);
			}

			// 원본 파일 저장
			fileService.save("derivedRequest", newDerivedRequestId, "common", "origin", 1, originFileName, downloadedFilePath);
		}

		return newDerivedRequestId;
	}

	public GenFile getOriginGenFile(DerivedRequest derivedRequest) {

		DerivedRequest originDerivedRequest = null;

		if (derivedRequest.isOriginStatus()) {
			originDerivedRequest = derivedRequest;
		} else  {
			originDerivedRequest = findFirstDerivedRequestByOriginUrl(derivedRequest.getOriginUrl());
		}

		return fileService.getGenFile("derivedRequest", originDerivedRequest.getId(), "common", "origin", 1);
	}

	// 요청 크기의 이미지 조회, 없으면 생성
	public GenFile getDerivedGenFileOrCreateIfNotExists(DerivedRequest derivedRequest) {

		// 이미지 요구 사항
		int width = derivedRequest.getWidth();
		int height = derivedRequest.getHeight();
		int maxWidth = derivedRequest.getMaxWidth();

		// 원본이 저장된 요청에서 일치하는 이미지가 저장돼있는지 먼저 찾고, 없으면 생성
		if (! derivedRequest.isOriginStatus()) {
			derivedRequest = findFirstDerivedRequestByOriginUrl(derivedRequest.getOriginUrl());
		}

		if (width > 0 && height > 0) {
			return getDerivedGenFileByWidthAndHeightOrCreate(derivedRequest, width, height);
		}

		if (width > 0) {
			return getDerivedGenFileByWidthOrCreate(derivedRequest, width);
		}

		if (maxWidth > 0) {
			return getDerivedGenFileByMaxWidthOrCreate(derivedRequest, maxWidth);
		}

		return getOriginGenFile(derivedRequest);
	}

	public void updateDerivedGenFileId(int newDerivedRequestId, int genFileId) {
		derivedRequestDao.updateDerivedGenFileId(newDerivedRequestId, genFileId);
	}

	public GenFile getDerivedGenFileByWidthAndHeightOrCreate(DerivedRequest derivedRequest, int width, int height) {

		GenFile derivedGenFile = getDerivedGenFileByWidthAndHeight(derivedRequest, width, height);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return createDerivedGenFileByWidthAndHeight(derivedRequest, width, height);
	}

	public GenFile getDerivedGenFileByWidthOrCreate(DerivedRequest derivedRequest, int width) {

		GenFile derivedGenFile = getDerivedGenFileByWidth(derivedRequest, width);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return createDerivedGenFileByWidthAndHeight(derivedRequest, width, 0);
	}

	public GenFile getDerivedGenFileByMaxWidthOrCreate(DerivedRequest derivedRequest, int maxWidth) {

		GenFile derivedGenFile = getDerivedGenFileByMaxWidth(derivedRequest, maxWidth);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return createDerivedGenFileByWidthAndHeight(derivedRequest, maxWidth, 0);
	}

	private GenFile getDerivedGenFileByWidthAndHeight(DerivedRequest derivedRequest, int width, int height) {

		return fileService.getGenFileByFileExtTypeCodeAndWidthAndHeight("derivedRequest", derivedRequest.getId(), "img", width, height);
	}

	private GenFile getDerivedGenFileByWidth(DerivedRequest derivedRequest, int width) {

		// 지정된 높이(height)가 없으므로, 원본 종횡비 기준으로 높이 산정 (원본 비율이 유지된 파일로 조회)
		GenFile originGenFile = getOriginGenFile(derivedRequest);
		int originWidth = originGenFile.getWidth();
		int originHeight = originGenFile.getHeight();
		int height = width * originHeight / originWidth;

		return getDerivedGenFileByWidthAndHeight(derivedRequest, width, height);
	}

	private GenFile getDerivedGenFileByMaxWidth(DerivedRequest derivedRequest, int maxWidth) {

		GenFile originGenFile = getOriginGenFile(derivedRequest);
		int originWidth = originGenFile.getWidth();
		int originHeight = originGenFile.getHeight();
		int height = maxWidth * originHeight / originWidth;

		if (originWidth <= maxWidth) { // 원본 너비가 maxWidth 이하면 원본 리턴
			return originGenFile;
		}

		return getDerivedGenFileByWidthAndHeight(derivedRequest, maxWidth, height);
	}

	private GenFile createDerivedGenFileByWidthAndHeight(DerivedRequest derivedRequest, int width, int height) {

		// 저장된 원본 파일 가져오기
		GenFile originGenFile = getOriginGenFile(derivedRequest);

		// 리사이징한 파일이 저장될 임시경로
		String resizedFilePath = App.getNewTmpFilePath(originGenFile.getFileExt());

		// 지정된 높이(height)가 없는 경우, 원본 종횡비 기준으로 높이 산정
		if (height == 0) {
			// originWidth / originHeight == width / height
			int originWidth = originGenFile.getWidth(); // 원본 너비
			int originHeight = originGenFile.getHeight(); // 원본 높이
			height = originHeight * width / originWidth; // 산정된 높이
		}

		// 요구사항에 맞게 리사이징
		Util.resizeImg(originGenFile.getFilePath(), resizedFilePath, width, height);

		// 리사이징한 파일 정보 등록 및 저장
		int newGenFileId = fileService.save("derivedRequest", derivedRequest.getId(), "common", "derived", 0, originGenFile.getOriginFileName(), resizedFilePath);

		// 저장된 파일 정보 리턴
		return fileService.getGenFileById(newGenFileId);
	}

}
