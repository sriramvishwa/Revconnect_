//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.revconnect.mapper;

import com.revconnect.dto.UserDto;
import com.revconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole() != null ? user.getRole().name() : null);
            dto.setBio(user.getBio());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setBusinessName(user.getBusinessName());
            dto.setIndustry(user.getIndustry());
            dto.setVerified(user.isVerified());
            dto.setPostsCount(user.getPostsCount());
            dto.setConnectionsCount(user.getConnectionsCount());
            dto.setFollowersCount(user.getFollowersCount());
            return dto;
        }
    }
}
