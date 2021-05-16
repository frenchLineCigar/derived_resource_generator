package com.example.drg.dao;

import com.example.drg.dto.DerivedRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DerivedRequestDao {
    void saveMeta(DerivedRequest derivedRequest);

    DerivedRequest findDerivedRequestByUrl(@Param("url") String url);

    DerivedRequest findFirstDerivedRequestByOriginUrl(@Param("originUrl") String originUrl);

}
