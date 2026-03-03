package com.revconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = { "follower_id", "following_id" }))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user who follows
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // user being followed
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Follow() {
    }

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowing() {
        return following;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowing(User following) {
        this.following = following;
    }
}
