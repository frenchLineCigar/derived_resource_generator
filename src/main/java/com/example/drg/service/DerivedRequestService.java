package com.example.drg.service;

import com.example.drg.app.App;
import com.example.drg.dao.DerivedRequestDao;
import com.example.drg.dto.GenFile;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DerivedRequestService {
    private final DerivedRequestDao derivedRequestDao;
    private final GenFileService fileService;

    public DerivedRequest getDerivedRequestByUrl(String url) {
        return derivedRequestDao.findDerivedRequestByUrl(url);
    }

    // 파생 리소스 저장
    public void save(String url, String originUrl, Integer width, Integer height, Integer maxWidth) {

        DerivedRequest newDerivedRequest = DerivedRequest.create(url, originUrl, width, height, maxWidth);

        // 요청 정보 저장
        derivedRequestDao.saveMeta(newDerivedRequest);

        // originUrl 원본이 저장되어 있으면 기존 파일경로를, 없으면 다운로드 후 임시경로 리턴
        String filePath = getFilePathOrDownloadByOriginUrl(originUrl);

        // 파일이 임시경로에 있으면 신규 다운로드로 판단
        boolean isNew = App.existsInTmpDir(filePath); // => filePath.contains(App.getTmpDirPath())
        if (isNew) {
            int newDerivedRequestId = newDerivedRequest.getId();
            String originFileName = Util.getFileNameFromUrl(originUrl); // 파일명 추출

            // 파일 메타정보 저장 및 정식경로로 파일 이동
            fileService.save("derivedRequest", newDerivedRequestId, "common", "origin", 1, originFileName, filePath);
        }
    }

    // originUrl 원본파일을 최초로 저장한 파생요청 조회
    public DerivedRequest getOriginDerivedRequest(String originUrl) {
        return derivedRequestDao.findFirstDerivedRequestByOriginUrl(originUrl);
    }

    // originUrl 원본파일 조회
    public GenFile getOriginGenFile(String originUrl) {
        GenFile originGenFile = null;

        DerivedRequest originDerivedRequest = getOriginDerivedRequest(originUrl); // originUrl 원본을 저장한 최초 파생 요청 조회

        if (originDerivedRequest != null) {
            originGenFile =  getOriginGenFileByOriginRequest(originDerivedRequest); // 해당 요청으로 저장된 원본파일 조회
        }

        return originGenFile;
    }

    // originUrl 원본파일 조회 (오버로딩)
    public GenFile getOriginGenFile(DerivedRequest currentDerivedRequest) {
        return getOriginGenFile(currentDerivedRequest.getOriginUrl());
    }

    // originUrl 원본파일 조회 (쿼리 중복 방지)
    public GenFile getOriginGenFileByOriginRequest(DerivedRequest originDerivedRequest) {
        return fileService.getGenFile("derivedRequest", originDerivedRequest.getId(), "common", "origin", 1);
    }

    // originUrl 원본이 저장되어 있으면 해당 파일경로를, 없으면 다운로드 후 저장경로 리턴
    public String getFilePathOrDownloadByOriginUrl(String originUrl) {

        GenFile originGenFile = getOriginGenFile(originUrl); // originUrl 원본을 저장한 기존파일 조회

        if (originGenFile != null) {
            return originGenFile.getFilePath(); // 있으면 해당 파일 경로 리턴
        }

        return Util.downloadFileByHttp(originUrl, App.getTmpDirPath()); // 없으면 originUrl 원본 다운로드 후 경로 리턴
    }

    // width, height 가 일치하는 이미지 조회, 없으면 생성 후 리턴
    public GenFile getDerivedGenFileByWidthAndHeightOrMake(DerivedRequest derivedRequest, int width, int height) {
        GenFile derivedGenFile = getDerivedGenFileByWidthAndHeight(derivedRequest, width, height);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

	    // TODO 없으면 너비가 width, 높이가 height 인 새로운 genFile 생성 (크롭 & 리사이징)
	    return null;
    }

	// width 가 일치하는 이미지 조회, 없으면 생성 후 리턴
    public GenFile getDerivedGenFileByWidthOrMake(DerivedRequest derivedRequest, int width) {
        GenFile derivedGenFile = getDerivedGenFileByWidth(derivedRequest, width);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

	    // TODO 없으면 너비가 width 인 새로운 genFile 생성 (크롭 & 리사이징)
        return null;
    }

	// width 가 maxWidth 를 초과하지 않는 크기 중 가장 큰 이미지 조회, 없으면 생성 후 리턴
    public GenFile getDerivedGenFileByMaxWidthOrMake(DerivedRequest derivedRequest, int maxWidth) {
        GenFile derivedGenFile = getDerivedGenFileByMaxWidth(derivedRequest, maxWidth);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

        // TODO 없으면 너비가 maxWidth 인 새로운 genFile 생성 (크롭 & 리사이징)
        return null;
    }

    // width, height 가 일치하는 이미지 조회
    private GenFile getDerivedGenFileByWidthAndHeight(DerivedRequest derivedRequest, int width, int height) {

        // 원본 이미지(originUrl)를 저장한 최초 요청을 조회
        DerivedRequest originDerivedRequest = getOriginDerivedRequest(derivedRequest.getOriginUrl());

        // 저장된 원본 이미지가 요구사항의 width, height 와 일치하는지
        return fileService.getGenFileByFileExtTypeCodeAndWidthAndHeight("derivedRequest", originDerivedRequest.getId(), "img", width, height);
    }

	// width 가 일치하는 이미지 조회
    private GenFile getDerivedGenFileByWidth(DerivedRequest derivedRequest, int width) {

        DerivedRequest originDerivedRequest = getOriginDerivedRequest(derivedRequest.getOriginUrl());

        return fileService.getGenFileByFileExtTypeCodeAndWidth("derivedRequest", originDerivedRequest.getId(), "img", width);
    }

    // width 가 maxWidth 를 초과하지 않는 크기 중 가장 큰 이미지 조회
    private GenFile getDerivedGenFileByMaxWidth(DerivedRequest derivedRequest, int maxWidth) {

        DerivedRequest originDerivedRequest = getOriginDerivedRequest(derivedRequest.getOriginUrl());

        return fileService.getGenFileByFileExtTypeCodeAndMaxWidth("derivedRequest", originDerivedRequest.getId(), "img", maxWidth);
    }

}
