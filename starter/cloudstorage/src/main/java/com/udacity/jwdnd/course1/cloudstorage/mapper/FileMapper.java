package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMapper {

    @Select("SELECT * FROM files WHERE userid = #{userId}")
    List<File> findAllByUserId(Integer userId);

    /**
     * @param userId to get files for
     * @return file list with stripped byte data
     */
    @Select("SELECT fileid, filename, contenttype, fileSize, userId FROM files WHERE userid = #{userId}")
    List<File> findAllMetaByUserId(Integer userId);

    /**
     * @param fileName to get files for
     * @param userId to get files for
     * @return file list with stripped byte data
     */
    @Select("SELECT fileid, filename, contenttype, fileSize, userId FROM files "
            + "WHERE filename = #{fileName} AND userid = #{userId}")
    List<File> findAllMetaByFileNameAndUserId(String fileName, Integer userId);

    @Select("SELECT * FROM files WHERE fileid = #{fileId}")
    File findByFileId(Integer fileId);

    @Insert("INSERT INTO files (filename, contenttype, filesize, userid, filedata) "
            + "VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(File file);

    @Delete("DELETE FROM files WHERE fileid = #{fileId}")
    int delete(Integer fileId);
}
