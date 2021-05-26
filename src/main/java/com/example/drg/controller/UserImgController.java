package com.example.drg.controller;

import com.example.drg.annotation.RequestUrl;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.dto.GenFile;
import com.example.drg.exception.DownloadFileFailException;
import com.example.drg.service.DerivedRequestService;
import com.example.drg.service.GenFileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserImgController {

    private final DerivedRequestService derivedRequestService;
    private final GenFileService fileService;

    @ExceptionHandler(DownloadFileFailException.class)
    public ResponseEntity<Object> responseEntityToOther(DownloadFileFailException e, HttpServletRequest request, WebRequest webRequest) {
        URI redirectUri = null;
        String failText = request.getParameter("failText");
        if (failText == null) {
            failText = "No Image Available";
        }
        try {
            redirectUri = new URI("https://via.placeholder.com/300x300?text=" + URLEncoder.encode(failText, StandardCharsets.UTF_8.toString()));
        } catch (URISyntaxException | UnsupportedEncodingException ex) {
            log.info(ex.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    /* 요청에 알맞는 이미지 파생 후 결과를 보여준다 */
    @GetMapping("/img")
    @ApiOperation(value = "이미지 변환", notes = "URL에 해당하는 이미지를 입력한 크기로 변환된 이미지로 제공합니다.")
    public ResponseEntity<Resource> showImg(@ApiParam(hidden = true) @RequestUrl String requestUrl,
                                            @ApiParam(value = "이미지 URL") @RequestParam("url") String originUrl,
                                            @ApiParam(value = "원하는 출력 너비") @RequestParam(defaultValue = "0", required = false) int width,
                                            @ApiParam(value = "원하는 출력 높이") @RequestParam(defaultValue = "0", required = false) int height,
                                            @ApiParam(value = "원하는 출력 최대너비") @RequestParam(defaultValue = "0", required = false) int maxWidth) {

        // 현재 요청(requestUrl)과 동일한 기존 요청이 있었는지 조회
        DerivedRequest existing = derivedRequestService.findDerivedRequestByRequestUrl(requestUrl);

        if (existing == null) {

            // 현재 요청 저장
            int newDerivedRequestId = derivedRequestService.save(requestUrl, originUrl, width, height, maxWidth);

            // 재조회
            DerivedRequest newDerivedRequest = derivedRequestService.getDerivedRequestById(newDerivedRequestId);

            // 현재 요청에 알맞는 파일 찾기, 없으면 생성
            GenFile derivedGenFile = derivedRequestService.getDerivedGenFileOrCreateIfNotExists(newDerivedRequest);

            // 현재 요청과 연관된 파일 아이디로 갱신
            derivedRequestService.updateDerivedGenFileId(newDerivedRequest.getId(), derivedGenFile.getId());

            return getClientCachedResponseEntity(derivedGenFile);
        }

        GenFile originGenFile = fileService.getGenFileById(existing.getGenFileId());
        return getClientCachedResponseEntity(originGenFile);
    }

    @GetMapping("/imgById")
    @ApiOperation(value = "이미지 번호로 이미지 출력", notes = "입력 받은 id에 해당하는 이미지를 출력합니다.")
    public ResponseEntity<Resource> showImgById(@ApiParam(value = "이미지 파일의 id") @RequestParam(required = true) int id) {
        // 아이디로 파일 조회
        GenFile genFile = fileService.getGenFileById(id);
        return getClientCachedResponseEntity(genFile);
    }

    // 응답 헤더에 캐시 설정
    private ResponseEntity<Resource> getClientCachedResponseEntity(GenFile genFile) {

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
        Tika tika = new Tika();
        String contentType = null;
        try {
            contentType = tika.detect(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
