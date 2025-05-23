package com.app.backend.mapper;

import com.app.backend.model.Contact;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContactMapper {

    @Select("SELECT * FROM contact WHERE id = #{id}")
    Contact findById(@Param("id") Long id);

    @Select("SELECT * FROM contact WHERE user_id = #{userId} ORDER BY contact_date DESC")
    List<Contact> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM contact WHERE user_id = #{userId} AND " +
            "(name LIKE CONCAT('%', #{query}, '%') OR province LIKE CONCAT('%', #{query}, '%') OR " +
            "city LIKE CONCAT('%', #{query}, '%') OR address LIKE CONCAT('%', #{query}, '%') OR " +
            "zipcode LIKE CONCAT('%', #{query}, '%')) " +
            "ORDER BY contact_date DESC")
    List<Contact> searchByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Insert("INSERT INTO contact(name, province, city, address, zipcode, user_id, contact_date) " +
            "VALUES(#{name}, #{province}, #{city}, #{address}, #{zipcode}, #{userId}, #{contactDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Contact contact);

    @Update("UPDATE contact SET name=#{name}, province=#{province}, city=#{city}, address=#{address}, " +
            "zipcode=#{zipcode}, contact_date=#{contactDate} WHERE id=#{id}")
    int update(Contact contact);

    @Delete("DELETE FROM contact WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM contact WHERE user_id=#{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}