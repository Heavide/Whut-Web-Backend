package com.app.backend.service.impl;

import com.app.backend.dto.UserRequest;
import com.app.backend.mapper.ArticleMapper;
import com.app.backend.mapper.UserMapper;
import com.app.backend.model.Article;
import com.app.backend.model.User;
import com.app.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAll();
        for (User user : users) {
            int articleCount = articleMapper.countByUserId(user.getId());
            user.setArticleCount(articleCount);
            user.hideSensitiveInfo();
        }
        return users;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.hideSensitiveInfo();
        }
        return user;
    }

    @Override
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllUsers();
        }
        List<User> users = userMapper.search(query);
        users.forEach(User::hideSensitiveInfo);
        return users;
    }

    @Override
    @Transactional
    public User createUser(UserRequest userRequest) {
        if (userMapper.countByUsername(userRequest.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()
                && userMapper.countByEmail(userRequest.getEmail()) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setAvatar(userRequest.getAvatar());
        user.setBirthday(userRequest.getBirthday());

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("创建用户失败");
        }

        User createdUser = userMapper.findByUsername(user.getUsername());
        createdUser.hideSensitiveInfo();
        return createdUser;
    }

    @Override
    @Transactional
    public User updateUser(UserRequest userRequest) {
        User existingUser = userMapper.findById(userRequest.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!existingUser.getUsername().equals(userRequest.getUsername())
                && userMapper.countByUsername(userRequest.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()
                && !userRequest.getEmail().equals(existingUser.getEmail())
                && userMapper.countByEmail(userRequest.getEmail()) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setBirthday(userRequest.getBirthday());

        if (userRequest.getAvatar() != null && !userRequest.getAvatar().isEmpty()) {
            existingUser.setAvatar(userRequest.getAvatar());
        }

        int result = userMapper.update(existingUser);
        if (result <= 0) {
            throw new RuntimeException("更新用户信息失败");
        }

        User updatedUser = userMapper.findById(existingUser.getId());
        updatedUser.hideSensitiveInfo();
        return updatedUser;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        if (userMapper.findById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
        return userMapper.deleteById(userId) > 0;
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        return userMapper.updatePassword(userId, encodedPassword) > 0;
    }

    @Override
    @Transactional
    public boolean updateAvatar(Long userId, String avatarPath) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return userMapper.updateAvatar(userId, avatarPath) > 0;
    }

    @Override
    public boolean verifyUserIdentity(String username, String email) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }
        if (email != null && !email.isEmpty()) {
            return email.equals(user.getEmail());
        }
        return false;
    }

    @Override
    @Transactional
    public boolean resetPassword(String username, String newPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        return userMapper.updatePassword(user.getId(), encodedPassword) > 0;
    }
}