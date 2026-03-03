package com.revconnect.service;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        if (dto.getRole() == com.revconnect.entity.UserRole.PERSONAL) {
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
        } else {
            user.setBusinessName(dto.getBusinessName());
            user.setIndustry(dto.getIndustry());
            user.setCategory(dto.getNiche());
            user.setContactEmail(dto.getContactEmail());
            user.setBusinessAddress(dto.getBusinessAddress());
            user.setBusinessHours(dto.getBusinessHours());
        }

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public java.util.List<User> searchUsers(String query) {
        if (query == null || query.isBlank())
            return java.util.List.of();
        return userRepository.searchUsers(query);
    }
}
