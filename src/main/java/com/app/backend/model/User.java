package com.app.backend.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private Date createTime;
    private Date birthday;
    private int articleCount;

    public void hideSensitiveInfo() {
        this.password = null;
    }
}