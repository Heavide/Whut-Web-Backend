package com.app.backend.service;

import com.app.backend.model.User;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String username, String password);
    Map<String, Object> register(User user);
}