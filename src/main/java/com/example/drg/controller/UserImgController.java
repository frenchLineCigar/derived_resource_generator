package com.example.drg.controller;

import com.example.drg.dto.GenFile;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.service.GenFileService;
import com.example.drg.service.DerivedRequestService;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserImgController {

    private final DerivedRequestService derivedRequestService;
    private final GenFileService fileService;

    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;

    @Value("${custom.tmpDirPath}")
    private String tmpDirPath;

    /* 파생 url 의 이미지를 저장하고 보여준다 */
    @RequestMapping("/img")
    public ResponseEntity<Resource> showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
        String currentUrl = Util.getUrlFromHttpServletRequest(req); // 현재 요청 url
        String queryString = req.getQueryString(); // 쿼리 스트링 추출
        String originUrl = queryString.split("url=")[1]; // 이미지를 다운로드할 파생 url 추출

        // 현재 요청 url과 같은 형태로 저장한 기존 이력이 있는지 조회
        DerivedRequest derivedRequest = derivedRequestService.getDerivedRequestByUrl(currentUrl);

        // 없으면 저장 후 갱신된 결과를 리턴
        if (derivedRequest == null) {
            int width = Util.getAsInt(param.get("width"), 0);
            int height = Util.getAsInt(param.get("height"), 0);
            int maxWidth = Util.getAsInt(param.get("maxWidth"), 0);

            // 다운로드 후 저장 경로를 변수에 담는다
            String downloadedFilePath = Util.downloadFileByHttp(originUrl, tmpDirPath);

            derivedRequestService.save(currentUrl, originUrl, width, height, maxWidth, downloadedFilePath); // 요청 정보 저장

        derivedRequest = derivedRequestService.getDerivedRequestByUrl(currentUrl); // 재조회후 결과 갱신
        }

        // 저장된 파일 정보 조회
        GenFile originGenFile = derivedRequestService.getOriginGenFile(derivedRequest);

        // 파일 경로 찾기
        String filePath = originGenFile.getFilePath(genFileDirPath);

        // 경로를 통해 파일을 읽고 응답 바디에 담을 리소스 생성
        Resource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            log.info("지정된 경로의 파일을 찾을 수 없습니다. : " + e.getMessage());
        }

        // Content-Type 확인
        String contentType = req.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

        // Content-Type 이 없으면 옥텟 스트림으로 Fallback
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // application/octet-stream
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // 아이디로 이미지 조회후 보여주기
    @GetMapping("/imgById")
    public ResponseEntity<Resource> showImgById(int id, HttpServletRequest request) {
        GenFile genFile = fileService.getGenFileById(id);
        String filePath = genFile.getFilePath(genFileDirPath);

        // 응답 바디에 담을 파일을 리소스로 로드해 담는다
        Resource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            log.info("지정된 경로의 파일을 찾을 수 없습니다. : " + e.getMessage());
        }

        // Content-Type 확인
        String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

        // Content-Type 이 없으면 옥텟 스트림으로 Fallback
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // application/octet-stream
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
