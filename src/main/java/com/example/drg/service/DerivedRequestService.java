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

    public void save(String url, String originUrl, Integer width, Integer height, Integer maxWidth, String filePath) {
        DerivedRequest newDerivedRequest = DerivedRequest.create(url, originUrl, width, height, maxWidth);

        // 요청 정보 저장
        derivedRequestDao.saveMeta(newDerivedRequest);

        // 신규 다운로드 이미지면 파일 저장 => 파일이 임시 경로에 있으면 신규 다운로드로 판단
        boolean isNew = App.existsInTmpDir(filePath); // => filePath.contains(App.getTmpDirPath())
        if (isNew) {
            int newDerivedRequestId = newDerivedRequest.getId();
            String originFileName = Util.getFileNameFromUrl(originUrl); // 파일명 추출
            fileService.save("derivedRequest", newDerivedRequestId, "common", "origin", 1, originFileName, filePath);
        }
    }

    public GenFile getOriginGenFile(DerivedRequest derivedRequest) {
        // 동일한 이미지(originUrl)에 대해 1번만 저장하므로, 최초 저장 요청의 id로 파일을 조회
        DerivedRequest originDerivedRequest = derivedRequestDao.findFirstDerivedRequestByOriginUrl(derivedRequest.getOriginUrl());

        return fileService.getGenFile("derivedRequest", originDerivedRequest.getId(), "common", "origin", 1);
    }

    /* 기존 파일이 있으면 해당 파일경로를, 없으면 다운로드 후 파일경로를 리턴 */
    public String getFilePathOrDownloadByOriginUrl(String originUrl) {

        // 현재 저장할 이미지(originUrl)와 동일한 파일의 저장 요청이 있었는지 조회
        DerivedRequest derivedRequest = derivedRequestDao.findFirstDerivedRequestByOriginUrl(originUrl);

        if (derivedRequest != null) { // 있으면
            GenFile originGenFile = getOriginGenFile(derivedRequest); // 기존에 저장된 이미지(originUrl) 파일 조회

            if (originGenFile != null) {
                return originGenFile.getFilePath(); // 해당 파일의 경로 리턴
            }
        }

        // 없으면 해당 이미지(originUrl) 파일을 다운로드 후 저장 경로를 리턴
        return Util.downloadFileByHttp(originUrl, App.getTmpDirPath());
    }

}
