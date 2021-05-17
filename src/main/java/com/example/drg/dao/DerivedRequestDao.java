package com.example.drg.dao;

import com.example.drg.dto.DerivedRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DerivedRequestDao {
    void saveMeta(DerivedRequest derivedRequest);

    DerivedRequest findDerivedRequestByUrl(@Param("url") String url);

    // originUrl 원본 이미지를 저장했던 가장 최초의 파생 요청을 조회한다
    // 동일한 출처의 이미지는 최초 단 1번만 원본을 저장하고, 이후 파생 요청들에서 이 원본파일을 재활용하므로
    // 동일 이미지의 중복 저장을 막기 위해 해당 이미지(originUrl)에 대한 최초 파생 요청과 이를 매개로 저장된 파일 정보를 관리해 재활용한다
    DerivedRequest findFirstDerivedRequestByOriginUrl(@Param("originUrl") String originUrl);

}
