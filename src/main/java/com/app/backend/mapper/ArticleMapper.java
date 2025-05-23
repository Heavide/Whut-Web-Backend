package com.app.backend.mapper;

import com.app.backend.model.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("SELECT * FROM article WHERE id = #{id}")
    Article findById(@Param("id") Long id);

    @Select("SELECT * FROM article WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Article> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM article WHERE user_id = #{userId} AND " +
            "(title LIKE CONCAT('%', #{query}, '%') OR content LIKE CONCAT('%', #{query}, '%')) " +
            "ORDER BY create_time DESC")
    List<Article> searchByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Insert("INSERT INTO article(title, content, user_id, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{userId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Article article);

    @Update("UPDATE article SET title=#{title}, content=#{content}, update_time=#{updateTime} WHERE id=#{id}")
    int update(Article article);

    @Delete("DELETE FROM article WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM article WHERE user_id=#{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM article WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}