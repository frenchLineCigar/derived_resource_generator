package com.example.drg.dao;

import com.example.drg.dto.DerivedRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DerivedRequestDao {
    void save(DerivedRequest derivedRequest);

    DerivedRequest findDerivedRequestById(@Param("id") int id);

    DerivedRequest findDerivedRequestByRequestUrl(@Param("requestUrl") String requestUrl);

    // originUrl 로의 최초 파생 요청 (원본을 가지고 있는 요청)
    DerivedRequest findFirstDerivedRequestByOriginUrl(@Param("originUrl") String originUrl);

    // 요청과 연관된 파일 아이디 갱신
    void updateDerivedGenFileId(int id, int genFileId);

}
