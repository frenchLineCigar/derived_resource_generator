package com.example.drg.controller;

import com.example.drg.dto.GenFile;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.service.GenFileService;
import com.example.drg.service.DerivedRequestService;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserImgController {

    private final DerivedRequestService derivedRequestService;
    private final GenFileService fileService;

    /* 이미지 파생 */
    @RequestMapping("/img")
    public ResponseEntity<Resource> showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
        String currentUrl = Util.getUrlFromHttpServletRequest(req); // 현재 요청 url
        String queryString = req.getQueryString(); // 쿼리 스트링 추출
        String originUrl = queryString.split("url=")[1]; // 이미지 url

        // 현재 요청(currentUrl)과 완전히 일치하는 기존 요청이 있었는지 조회
        DerivedRequest derivedRequest = derivedRequestService.getDerivedRequestByUrl(currentUrl);

        if (derivedRequest == null) {
            int width = Util.getAsInt(param.get("width"), 0);
            int height = Util.getAsInt(param.get("height"), 0);
            int maxWidth = Util.getAsInt(param.get("maxWidth"), 0);

            derivedRequestService.save(currentUrl, originUrl, width, height, maxWidth); // 파생 리소스 저장
            derivedRequest = derivedRequestService.getDerivedRequestByUrl(currentUrl); // 재조회후 결과 갱신
        }

        // 클라이언트 요구 사항
        int width = derivedRequest.getWidth(); // 요청 너비
        int height = derivedRequest.getHeight(); // 요청 높이
        int maxWidth = derivedRequest.getMaxWidth(); // 요청 최대너비

        // 요구사항에 맞는 이미지로 가공
        if ( width > 0 && height > 0 ) { // 너비, 높이 둘 다 있는 경우 (너비 지정 시 최대너비 무시)
            GenFile derivedGenFile = derivedRequestService.getDerivedGenFileByWidthAndHeightOrMake(derivedRequest, width, height);
            return getClientCachedResponseEntity(derivedGenFile, req);
        }
        else if ( width > 0 ) { // 너비만 있는 경우
            GenFile derivedGenFile = derivedRequestService.getDerivedGenFileByWidthOrMake(derivedRequest, width);
            return getClientCachedResponseEntity(derivedGenFile, req);
        }
        else if ( maxWidth > 0 ) { // 최대너비만 있는 경우
            GenFile derivedGenFile = derivedRequestService.getDerivedGenFileByMaxWidthOrMake(derivedRequest, maxWidth);
            return getClientCachedResponseEntity(derivedGenFile, req);
        }
        else { // 그 밖의 경우는 원본 리턴
            GenFile originGenFile = derivedRequestService.getOriginGenFile(derivedRequest.getOriginUrl());
            return getClientCachedResponseEntity(originGenFile, req);
        }
    }

    // 아이디로 이미지 조회후 보여주기
    @GetMapping("/imgById")
    public ResponseEntity<Resource> showImgById(int id, HttpServletRequest req) {
        // 아이디로 파일 조회
        GenFile genFile = fileService.getGenFileById(id);
        return getClientCachedResponseEntity(genFile, req);
    }

    // 응답 헤더에 캐시 설정
    private ResponseEntity<Resource> getClientCachedResponseEntity(GenFile genFile, HttpServletRequest req) {

        // 파일 경로 찾기
        String filePath = genFile.getFilePath();

        // 경로를 통해 파일을 읽고 응답바디에 담을 리소스 생성
        Resource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            log.info("지정된 경로의 파일을 찾을 수 없습니다. : " + e.getMessage());
        }

        // Content-Type 확인
        String contentType = req.getServletContext().getMimeType(new File(filePath).getAbsolutePath());
        // String contentType= URLConnection.guessContentTypeFromName(filePath);

        // Content-Type 이 없으면 옥텟 스트림으로 Fallback
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // application/octet-stream
        }

        log.info("filePath = " + filePath);
        log.info("contentType = " + contentType);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60 * 60 * 24 * 30, TimeUnit.SECONDS))
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
