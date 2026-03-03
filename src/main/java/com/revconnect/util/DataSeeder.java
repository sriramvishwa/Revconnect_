package com.revconnect.util;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.*;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.PostService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0)
            return;

        // 3 Personal Users
        createPersonalUser("alice", "Alice Johnson", "Tech enthusiast and coffee lover.");
        createPersonalUser("bob", "Bob Smith", "Exploring the world of connections.");
        createPersonalUser("charlie", "Charlie Brown", "Just a regular guy.");

        // 2 Creator Accounts
        createCreatorUser("creator_spark", "Spark Media", "Gaming & Tech Content", "Gaming");
        createCreatorUser("fashion_guru", "Elena Style", "Fashion & Lifestyle", "Fashion");

        // 2 Business Accounts
        createBusinessUser("tech_corp", "TechCorp Solutions", "Leading the future of AI.", "Technology");
        createBusinessUser("coffee_bean", "The Coffee Bean", "Best brew in town.", "Food & Beverage");

        // Seed some posts
        User alice = userRepository.findByUsername("alice").get();
        User spark = userRepository.findByUsername("creator_spark").get();
        User tech = userRepository.findByUsername("tech_corp").get();

        createPost(alice, "Hello RevConnect! Excited to be here. #FirstPost");
        createPost(alice, "Just finished a great coding session. #CodingLife");

        Post sparkPost = createPost(spark, "New video coming soon! Stay tuned. #Gaming");
        sparkPost.setPinned(true);
        sparkPost.setLikeCount(45);
        sparkPost.setViewCount(1200);
        postRepository.save(sparkPost);

        Post techPost = createPost(tech, "Check out our latest product release! #AI #Innovation");
        techPost.setCtaType(CTAType.LEARN_MORE);
        techPost.setCtaLink("https://example.com");
        techPost.setPromotional(true);
        techPost.setLikeCount(150);
        techPost.setViewCount(5000);
        postRepository.save(techPost);
    }

    private void createPersonalUser(String username, String name, String bio) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.PERSONAL);
        dto.setFirstName(name.split(" ")[0]);
        dto.setLastName(name.split(" ")[1]);
        User user = userService.registerUser(dto);
        user.setBio(bio);
        userRepository.save(user);
    }

    private void createCreatorUser(String username, String businessName, String bio, String niche) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.CREATOR);
        dto.setBusinessName(businessName);
        dto.setNiche(niche);
        User user = userService.registerUser(dto);
        user.setBio(bio);
        user.setVerified(true);
        userRepository.save(user);
    }

    private void createBusinessUser(String username, String businessName, String bio, String industry) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.BUSINESS);
        dto.setBusinessName(businessName);
        dto.setIndustry(industry);
        dto.setBusinessAddress("123 Tech Lane, Silicon Valley");
        dto.setBusinessHours("Mon-Fri: 9AM - 6PM");
        User user = userService.registerUser(dto);
        user.setBio(bio);
        user.setVerified(true);
        userRepository.save(user);
    }

    private Post createPost(User author, String content) {
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        // Extract hashtags
        Arrays.stream(content.split(" "))
                .filter(s -> s.startsWith("#"))
                .forEach(h -> post.getHashtags().add(h.substring(1)));
        return postRepository.save(post);
    }
}
