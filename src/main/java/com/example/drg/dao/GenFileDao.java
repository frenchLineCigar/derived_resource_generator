package com.example.drg.dao;

import com.example.drg.dto.GenFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenFileDao {

	void saveMeta(GenFile file);

	GenFile findGenFileById(@Param("id") int id);

	GenFile findGenFile(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId,
	                    @Param("typeCode") String typeCode, @Param("type2Code") String type2Code,
	                    @Param("fileNo") int fileNo);

	GenFile findGenFileByFileExtTypeCodeAndWidthAndHeight(String relTypeCode, int relId, String fileExtTypeCode, int width, int height);
	GenFile findGenFileByFileExtTypeCodeAndMaxWidth(String relTypeCode, int relId, String fileExtTypeCode, int maxWidth);
//	List<GenFile> findGenFileListByFileExtTypeCodeAndMaxWidth(String relTypeCode, List<Integer> relIds, String fileExtTypeCode, int maxWidth);
}
