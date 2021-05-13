package com.example.drg.dao;

import com.example.drg.dto.GenFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GenFileDao {

	void saveMeta(GenFile file);

	GenFile findGenFileById(@Param("id") int id);

}
