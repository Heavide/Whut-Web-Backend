package com.app.backend.mapper;

import com.app.backend.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{query}, '%') " +
            "OR email LIKE CONCAT('%', #{query}, '%') " +
            "ORDER BY create_time DESC")
    List<User> search(@Param("query") String query);

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    @Insert("INSERT INTO user(username, password, email, avatar, create_time, birthday) " +
            "VALUES(#{username}, #{password}, #{email}, #{avatar},  NOW(), #{birthday})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);

    @Update("UPDATE user SET username=#{username}, email=#{email}, avatar=#{avatar},  birthday=#{birthday} WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE user SET password=#{password} WHERE id=#{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Update("UPDATE user SET avatar=#{avatarPath} WHERE id=#{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatarPath") String avatarPath);

    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteById(@Param("id") Long id);
}