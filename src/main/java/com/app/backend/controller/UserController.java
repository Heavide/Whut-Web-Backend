package com.app.backend.controller;

import com.app.backend.common.Result;
import com.app.backend.dto.PasswordResetRequest;
import com.app.backend.dto.UserRequest;
import com.app.backend.model.User;
import com.app.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    @Autowired
    private UserService userService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";

    @GetMapping
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping
    public Result<User> createUser(@RequestBody UserRequest userRequest) {
        try {
            User user = userService.createUser(userRequest);
            return Result.success(user);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        try {
            userRequest.setId(id);
            User user = userService.updateUser(userRequest);
            return Result.success(user);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.deleteUser(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    public Result<Boolean> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordMap) {
        try {
            String newPassword = passwordMap.get("password");
            if (newPassword == null || newPassword.isEmpty()) {
                return Result.fail("密码不能为空");
            }

            boolean result = userService.updatePassword(id, newPassword);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新密码失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/verify-identity")
    public Result<Boolean> verifyIdentity(@RequestBody Map<String, String> identityMap) {
        try {
            String username = identityMap.get("username");
            String email = identityMap.get("email");

            if (username == null || username.isEmpty()) {
                return Result.fail("用户名不能为空");
            }

            if (email == null || email.isEmpty()) {
                return Result.fail("邮箱不能为空");
            }

            boolean verified = userService.verifyUserIdentity(username, email);
            if (verified) {
                return Result.success(true);
            } else {
                return Result.fail("身份验证失败，用户名或邮箱不匹配");
            }
        } catch (Exception e) {
            log.error("验证用户身份失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public Result<Boolean> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Result.fail("用户名不能为空");
            }

            if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
                return Result.fail("新密码不能为空");
            }

            boolean verified = userService.verifyUserIdentity(request.getUsername(),
                    request.getEmail());
            if (!verified) {
                return Result.fail("用户身份验证失败");
            }

            boolean result = userService.resetPassword(request.getUsername(), request.getNewPassword());
            if (result) {
                return Result.success(true);
            } else {
                return Result.fail("密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置密码失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/{id}/avatar")
    public Result<Map<String, String>> uploadAvatar(@PathVariable Long id, @RequestParam("avatar") MultipartFile avatar) {
        try {
            if (avatar == null || avatar.isEmpty()) {
                return Result.fail("头像文件不能为空");
            }

            String fileName = saveAvatar(avatar);
            boolean result = userService.updateAvatar(id, fileName);

            if (!result) {
                return Result.fail("更新头像失败");
            }

            Map<String, String> data = new HashMap<>();
            data.put("avatar", fileName);
            return Result.success(data);
        } catch (Exception e) {
            log.error("上传头像失败", e);
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