package com.app.backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UserRequest {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private Integer status;
    private Integer role;
    private Date birthday;
}