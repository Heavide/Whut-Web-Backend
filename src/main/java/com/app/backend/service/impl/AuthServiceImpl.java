package com.app.backend.service.impl;

import com.app.backend.mapper.UserMapper;
import com.app.backend.model.User;
import com.app.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.hideSensitiveInfo();
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        return result;
    }

    @Override
    public Map<String, Object> register(User user) {
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && userMapper.countByEmail(user.getEmail()) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败，请稍后再试");
        }
        user.hideSensitiveInfo();
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        return data;
    }
}