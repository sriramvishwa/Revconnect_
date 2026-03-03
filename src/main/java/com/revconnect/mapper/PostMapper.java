//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.revconnect.mapper;

import com.revconnect.dto.PostDto;
import com.revconnect.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostDto toDto(Post post) {
        if (post == null) {
            return null;
        } else {
            PostDto dto = new PostDto();
            dto.setId(post.getId());
            dto.setContent(post.getContent());
            dto.setMediaUrl(post.getMediaUrl());
            dto.setLikeCount(post.getLikeCount());
            dto.setCommentCount(post.getCommentCount());
            dto.setShareCount(post.getShareCount());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setPinned(post.isPinned());
            if (post.getAuthor() != null) {
                dto.setAuthorUsername(post.getAuthor().getUsername());
                dto.setAuthorRole(post.getAuthor().getRole() != null ? post.getAuthor().getRole().name() : "");
                dto.setAuthorVerified(post.getAuthor().isVerified());
            }

            return dto;
        }
    }
}
