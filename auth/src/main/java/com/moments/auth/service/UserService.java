package com.moments.auth.service;

import com.moments.auth.model.User;
import com.moments.auth.payload.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface UserService {
    public User registerUser(User user);
    public Optional<User> findByUsername(String username);
    public List<User> findAll();
    public List<User> findByUsernameIn(List<String> userNames);
}
