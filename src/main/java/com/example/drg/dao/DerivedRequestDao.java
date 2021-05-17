package com.example.drg.dao;

import com.example.drg.dto.DerivedRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DerivedRequestDao {
    void saveMeta(DerivedRequest derivedRequest);

    DerivedRequest findDerivedRequestByUrl(@Param("url") String url);

    // 원본 크기의 이미지가 생성된 최초 파생 요청(derivedRequest)
    DerivedRequest findFirstDerivedRequestByOriginUrl(@Param("originUrl") String originUrl);

    // width 와 height 크기로 이미지가 생성된 최초 파생 요청(derivedRequest)
    DerivedRequest findFirstDerivedRequestByWidthAndHeight(String originUrl, int width, int height);

    // maxWidth 이하인 width 중, 가장 큰 width 로 이미지가 생성된 최초 파생 요청(derivedRequest)
    DerivedRequest findFirstDerivedRequestByMaxWidth(String originUrl, int maxWidth);

}
