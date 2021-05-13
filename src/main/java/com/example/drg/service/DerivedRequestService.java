package com.example.drg.service;

import com.example.drg.dao.DerivedRequestDao;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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

    public void save(String url, String originUrl, Integer width, Integer height, Integer maxWidth, String downloadedFilePath) {
        Map<String, Object> param = Util.mapOf("url", url, "originUrl", originUrl, "width", width, "height", height, "maxWidth", maxWidth);

        // 요청 정보 저장
        derivedRequestDao.saveMeta(param);

        int newDerivedRequestId = Util.getAsInt(param.get("id"), 0); // 생성된 PK
        String originFileName = Util.getFileNameFromUrl(originUrl); // 파일명 추출

        // 파일 저장
        fileService.save("derivedRequest", newDerivedRequestId, "common", "origin", 1, originFileName, downloadedFilePath);
    }
}
