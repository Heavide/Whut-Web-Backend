package com.app.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Contact {
    private Long id;
    private String name;
    private String province;
    private String city;
    private String address;
    private String zipcode;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date contactDate;
}