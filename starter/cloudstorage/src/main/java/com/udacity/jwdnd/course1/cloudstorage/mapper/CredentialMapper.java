package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CredentialMapper {

    @Select("SELECT * FROM credentials")
    List<Credential> findAll();

    @Select("SELECT * FROM credentials WHERE userid = #{userId}")
    List<Credential> findByUserId(Integer userId);

    @Select("SELECT * FROM credentials WHERE credentialid = #{credentialId}")
    Credential findById(Integer credentialId);

    @Insert("INSERT INTO credentials(url, username, key, password, userid) "
            + "VALUES (#{url}, #{username}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insert(Credential credential);

    @Update("UPDATE credentials " +
            "SET url = #{url}, username = #{username}, key = #{key}, password = #{password} " +
            "WHERE credentialid = #{credentialId}")
    int update(Integer credentialId, String url, String username, String key, String password);

    @Delete("DELETE FROM credentials WHERE credentialid = #{credentialId}")
    int delete(Integer credentialId);

}
