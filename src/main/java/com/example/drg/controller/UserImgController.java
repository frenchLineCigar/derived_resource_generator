package com.example.drg.controller;

import com.example.drg.dto.DerivedRequest;
import com.example.drg.service.DerivedRequestService;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserImgController {

    private final DerivedRequestService derivedRequestService;

    @Value("${custom.tmpDirPath}")
    private String tmpDirPath;

    /* 파생 url 의 이미지를 저장하고 보여준다 */
    @RequestMapping("/img")
    public DerivedRequest showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
        String queryString = req.getQueryString(); // 쿼리 스트링 추출
        String url = queryString.split("url=")[1]; //이미지를 다운로드할 파생 url 추출

        // 파생 url을 저장한 기존 이력이 있는지 조회
        DerivedRequest derivedRequest = derivedRequestService.getDerivedRequestByUrl(url);

        // 없으면 저장 후 갱신된 결과를 리턴
        if (derivedRequest == null) {
            int width = Util.getAsInt(param.get("width"), 0);
            int height = Util.getAsInt(param.get("height"), 0);
            int maxWidth = Util.getAsInt(param.get("maxWidth"), 0);

            // 다운로드 후 저장 경로를 변수에 담는다
            String downloadedFilePath = Util.downloadFileByHttp(url, tmpDirPath);

            derivedRequestService.save(url, width, height, maxWidth, downloadedFilePath); // 요청 정보 저장

            return derivedRequestService.getDerivedRequestByUrl(url); // 재조회후 갱신된 결과 리턴
        }

        return derivedRequest;
    }

}
