package com.revconnect.service;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationDto registrationDto);

    User findByUsername(String username);

    User findById(Long id);

    List<User> searchUsers(String query);
}
