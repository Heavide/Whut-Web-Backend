package com.app.backend.controller;

import com.app.backend.common.Result;
import com.app.backend.model.User;
import com.app.backend.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        try {
            String username = loginForm.get("username");
            String password = loginForm.get("password");
            if (username == null || password == null) {
                return Result.fail("用户名和密码不能为空");
            }
            log.info(username);
            Map<String, Object> data = authService.login(username, password);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam(value = "birthday", required = false) String birthdayStr,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                try {
                    Date birthday = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(birthdayStr);
                    user.setBirthday(birthday);
                } catch (ParseException e) {
                    return Result.fail("生日格式不正确");
                }
            }

            if (avatar != null && !avatar.isEmpty()) {
                String fileName = saveAvatar(avatar);
                user.setAvatar(fileName);
            }

            Map<String, Object> data = authService.register(user);
            return Result.success(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    private String saveAvatar(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + fileExtension;

        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(new File(filePath.toFile().getAbsolutePath()));

        return fileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}