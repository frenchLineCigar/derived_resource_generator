package com.example.drg.dao;

import com.example.drg.dto.DerivedRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface DerivedRequestDao {
    void saveMeta(Map<String, Object> param);

    DerivedRequest findDerivedRequestByUrl(@Param("url") String url);
}
