package com.revconnect.service;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.User;

public interface UserService {
    User registerUser(UserRegistrationDto registrationDto);

    User findByUsername(String username);
}
