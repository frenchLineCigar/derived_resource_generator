package com.example.drg.service;

import com.example.drg.dao.DerivedRequestDao;
import com.example.drg.dto.DerivedRequest;
import com.example.drg.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class DerivedRequestService {
    private final DerivedRequestDao derivedRequestDao;
    private final GenFileService fileService;

    public DerivedRequest getDerivedRequestByUrl(String url) {
        return derivedRequestDao.findDerivedRequestByUrl(url);
    }

    public void save(String url, Integer width, Integer height, Integer maxWidth, String downloadedFilePath) {
        Map<String, Object> param = Util.mapOf("url", url, "width", width, "height", height, "maxWidth", maxWidth);

        // 요청 url 에 대한 정보 저장
        derivedRequestDao.saveMeta(param);

        int newDerivedRequestId = Util.getAsInt(param.get("id"), 0); // 생성된 PK
        String originFileName = Util.getFileNameFromUrl(url); // 파일명 추출

        // 파일 저장
        fileService.save("derivedRequest", newDerivedRequestId, "common", "origin", 1, originFileName, downloadedFilePath);
    }
}
