package com.example.drg.controller;

import com.example.drg.dto.DerivedRequest;
import com.example.drg.dto.GenFile;
import com.example.drg.service.DerivedRequestService;
import com.example.drg.service.GenFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserImgController {

    private final DerivedRequestService derivedRequestService;
    private final GenFileService fileService;

    /* 요청에 알맞는 이미지 파생 후 결과를 보여준다 */
    @RequestMapping("/img")
    public ResponseEntity<Resource> showImg(HttpServletRequest req, DerivedRequest derivedRequest) {

        // 현재 요청(requestUrl)과 동일한 기존 요청이 있었는지 조회
        DerivedRequest existing = derivedRequestService.findDerivedRequestByRequestUrl(derivedRequest.getRequestUrl());

        if (existing == null) {

            // 현재 요청 저장
            int newDerivedRequestId = derivedRequestService.save(derivedRequest);

            // 재조회
            DerivedRequest newDerivedRequest = derivedRequestService.getDerivedRequestById(newDerivedRequestId);

            // 현재 요청에 알맞는 파일 찾기, 없으면 생성
            GenFile derivedGenFile = derivedRequestService.getDerivedGenFileOrCreateIfNotExists(newDerivedRequest);

            // 현재 요청과 연관된 파일 아이디로 갱신
            derivedRequestService.updateDerivedGenFileId(newDerivedRequest.getId(), derivedGenFile.getId());

            return getClientCachedResponseEntity(derivedGenFile, req);
        }

        GenFile originGenFile = fileService.getGenFileById(existing.getGenFileId());
        return getClientCachedResponseEntity(originGenFile, req);
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
