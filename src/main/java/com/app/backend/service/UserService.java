package com.app.backend.service;

import com.app.backend.dto.UserRequest;
import com.app.backend.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> searchUsers(String query);
    User createUser(UserRequest userRequest);
    User updateUser(UserRequest userRequest);
    boolean deleteUser(Long userId);
    boolean updatePassword(Long userId, String newPassword);
    boolean updateAvatar(Long userId, String avatarPath);
    boolean verifyUserIdentity(String username, String email);
    boolean resetPassword(String username, String newPassword);
}