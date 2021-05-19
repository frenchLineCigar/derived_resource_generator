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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        // 파생 요청 객체 생성
        DerivedRequest newDerivedRequest = DerivedRequest.create(url, originUrl, width, height, maxWidth);

        // 파생 요청 정보 저장
        derivedRequestDao.saveMeta(newDerivedRequest);

        // 요청받은 파일 저장, 중복 저장을 막는다
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

    // originUrl 원본파일이 생성된 최초 파생 요청
    public DerivedRequest getOriginDerivedRequest(String originUrl) {
        return derivedRequestDao.findFirstDerivedRequestByOriginUrl(originUrl);
    }

    // originUrl 원본파일 조회
    public GenFile getOriginGenFile(String originUrl) {
        GenFile originGenFile = null;

        DerivedRequest originDerivedRequest = getOriginDerivedRequest(originUrl); // originUrl 원본을 생성한 최초 파생 요청 조회

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
        // width, height 가 일치하는 이미지 조회
        GenFile derivedGenFile = getDerivedGenFileByWidthAndHeight(derivedRequest, width, height);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

        // 없으면 너비가 width, 높이가 height 인 새로운 이미지 파일 생성
	    return makeDerivedGenFileByWidthAndHeight(derivedRequest, width, height);
    }

	// width 가 일치하는 이미지 조회, 없으면 생성 후 리턴
    public GenFile getDerivedGenFileByWidthOrMake(DerivedRequest derivedRequest, int width) {
        // width 가 일치하는 이미지 조회
        GenFile derivedGenFile = getDerivedGenFileByWidth(derivedRequest, width);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

        // 없으면 너비가 width 인 새로운 이미지 파일 생성
        return makeDerivedGenFileByWidth(derivedRequest, width);
    }

	// width 가 maxWidth 를 초과하지 않는 크기 중 가장 큰 이미지 조회, 없으면 생성 후 리턴
    public GenFile getDerivedGenFileByMaxWidthOrMake(DerivedRequest derivedRequest, int maxWidth) {

        // 너비가 maxWidth 를 초과하지 않는 크기 중 가장 큰 이미지 조회
        GenFile derivedGenFile = getDerivedGenFileByMaxWidth(derivedRequest, maxWidth);

        if (derivedGenFile != null) {
            return derivedGenFile;
        }

        // 없으면 너비가 maxWidth 인 새로운 이미지 파일 생성
        return makeDerivedGenFileByWidth(derivedRequest, maxWidth);
    }

    // width, height 가 일치하는 이미지 조회
    private GenFile getDerivedGenFileByWidthAndHeight(DerivedRequest derivedRequest, int width, int height) {

        // 지정한 width 와 height 크기로 이미지가 생성된 최초 파생 요청
        DerivedRequest derivedRequestByWidthAndHeight = derivedRequestDao.findFirstDerivedRequestByWidthAndHeight(derivedRequest.getOriginUrl(), width, height);

        // 해당 이미지 조회
        return fileService.getGenFileByFileExtTypeCodeAndWidthAndHeight("derivedRequest", derivedRequestByWidthAndHeight.getId(), "img", width, height);
    }

	// width 가 일치하는 이미지 조회
    private GenFile getDerivedGenFileByWidth(DerivedRequest derivedRequest, int width) {

        // 지정한 width 로 이미지가 생성된 최초 파생 요청
        DerivedRequest derivedRequestByWidth = derivedRequestDao.findFirstDerivedRequestByWidthAndHeight(derivedRequest.getOriginUrl(), width, 0); // 특정 height 가 지정되지 않을 경우(원본 비율이 유지된 경우)에 height 는 0

        // height 가 없으므로 원본 비율을 기준으로 산정해 파일을 조회한다 (원본 비율이 유지된 결과를 조회하기 위함)
        GenFile originGenFile = getOriginGenFile(derivedRequest.getOriginUrl());
        int originWidth = originGenFile.getWidth();
        int originHeight = originGenFile.getHeight();
        int height = width * originHeight / originWidth;

        // 해당 이미지 조회
        return fileService.getGenFileByFileExtTypeCodeAndWidthAndHeight("derivedRequest", derivedRequestByWidth.getId(), "img", width, height);
    }

    // width 가 maxWidth 이하의 크기 중 가장 큰 이미지 조회
    private GenFile getDerivedGenFileByMaxWidth(DerivedRequest derivedRequest, int maxWidth) {

        // width 가 maxWidth 이하의 크기 중, 가장 큰 width 의 이미지가 생성된 최초 파생 요청
        DerivedRequest derivedRequestByMaxWidth = derivedRequestDao.findFirstDerivedRequestByMaxWidth(derivedRequest.getOriginUrl(), maxWidth);

        return fileService.getGenFileByFileExtTypeCodeAndMaxWidth("derivedRequest", derivedRequestByMaxWidth.getId(), "img", maxWidth);
    }

//    // width 가 maxWidth 이하의 크기 중 가장 큰 이미지 조회
//    private GenFile getDerivedGenFileByMaxWidth(DerivedRequest derivedRequest, int maxWidth) {
//
//        // 결과를 담을 변수
//        GenFile derivedGenFile = null;
//
//        // width 가 maxWidth 이하인 모든 이미지 파생 요청
//        List<Integer> derivedRequestIds =
//                derivedRequestDao.findDerivedRequestListByMaxWidth(derivedRequest.getOriginUrl(), maxWidth).stream()
//                        .filter(request -> request.getHeight() == 0) // 원본비율이 유지된 경우만(특정 height 가 지정되지 않은 경우)
//                        .map(DerivedRequest::getId) // 아이디만 추출
//                        .collect(Collectors.toList());
//
//        if (! derivedRequestIds.isEmpty()) {
//
//            // 일치하는 이미지 조회 결과
//            List<GenFile> derivedGenFileList = fileService.getGenFileListByFileExtTypeCodeAndMaxWidth("derivedRequest", derivedRequestIds, "img", maxWidth);
//
//            // 결과 중 가장 큰 width 이미지
//            derivedGenFile = derivedGenFileList.stream()
//                    .max(Comparator.comparingInt(GenFile::getWidth)) //가장 큰 width 찾기
//                    .orElse(null);
//        }
//
//        return derivedGenFile;
//    }

    // 지정한 width, height 로 변환한 이미지 파일 생성 및 저장 (크롭 & 리사이징)
    private GenFile makeDerivedGenFileByWidthAndHeight(DerivedRequest derivedRequest, int width, int height) {

        // 저장된 원본 파일 조회
        GenFile originGenFile = getOriginGenFile(derivedRequest);

        // 파생 요청으로 생성된 파일이 저장될 임시경로
        String destFilePath = App.getNewTmpFilePath(originGenFile.getFileExt());

        // 요구사항에 맞게 리사이징
        Util.resizeImg(originGenFile.getFilePath(), destFilePath, width, height);

        // 파일 저장
        int newGenFileId = fileService.save("derivedRequest", derivedRequest.getId(), "common", "derived", 0, originGenFile.getOriginFileName(), destFilePath);

        // 파일 정보 리턴
        return fileService.getGenFileById(newGenFileId);
    }

    // 지정한 width 로 변환한 이미지 파일 생성 및 저장 (크롭 & 리사이징)
    private GenFile makeDerivedGenFileByWidth(DerivedRequest derivedRequest, int width) {

        GenFile originGenFile = getOriginGenFile(derivedRequest);

        String destFilePath = App.getNewTmpFilePath(originGenFile.getFileExt());

        // 지정된 높이(height)가 없으므로, 원본 종횡비 기준으로 산정
        // originWidth / originHeight == width / height
        int originWidth = originGenFile.getWidth(); // 원본 실제 너비
        int originHeight = originGenFile.getHeight(); // 원본 실제 높이
        int height = originHeight * width / originWidth; // 비율에 맞게 산정된 높이

        Util.resizeImg(originGenFile.getFilePath(), destFilePath, width, height);

        int newGenFileId = fileService.save("derivedRequest", derivedRequest.getId(), "common", "derived", 0, originGenFile.getOriginFileName(), destFilePath);

        return fileService.getGenFileById(newGenFileId);
    }

}
