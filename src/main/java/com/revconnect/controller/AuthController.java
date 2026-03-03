package com.revconnect.controller;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.service.PostService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.revconnect.repository.NotificationRepository;
import com.revconnect.service.MessageService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private MessageService messageService;

    /**
     * External upload directory served at /uploads/ via WebConfig resource handler
     */
    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/")
    public String home(Principal principal) {
        if (principal != null)
            return "redirect:/feed";
        return "home";
    }

    @GetMapping("/feed")
    public String feed(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        User user = userService.findByUsername(principal.getName());
        Page<Post> feedPage = postService.getFeed(user.getId(), 0, 10);
        long unreadCount = notificationRepository.countByRecipientAndIsReadFalse(user);
        model.addAttribute("user", user);
        model.addAttribute("posts", feedPage.getContent());
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(user));
        return "feed";
    }

    @PostMapping("/post")
    public String createPost(Principal principal,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) MultipartFile media) {
        if (principal == null)
            return "redirect:/login";
        if (content == null || content.isBlank())
            return "redirect:/feed";

        User user = userService.findByUsername(principal.getName());
        Post post = postService.createPost(user.getId(), content);

        // Save uploaded media file if present
        if (media != null && !media.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                String ext = getExtension(media.getOriginalFilename());
                String fileName = UUID.randomUUID() + ext;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(media.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the public URL on the post
                post.setMediaUrl("/uploads/" + fileName);
                postService.save(post);
            } catch (IOException e) {
                // Log and continue — post still created without media
                e.printStackTrace();
            }
        }

        return "redirect:/feed";
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        postService.deletePost(id, user.getId());
        return "redirect:/feed";
    }

    /** GAP 1 — Edit post */
    @PostMapping("/post/edit/{id}")
    public String editPost(@PathVariable Long id,
            @RequestParam("content") String content,
            Principal principal) {
        if (principal == null)
            return "redirect:/login";
        if (content == null || content.isBlank())
            return "redirect:/feed";
        User user = userService.findByUsername(principal.getName());
        postService.editPost(id, user.getId(), content.trim());
        return "redirect:/feed";
    }

    /** GAP 7 — Schedule post */
    @PostMapping("/post/schedule")
    public String schedulePost(Principal principal,
            @RequestParam("content") String content,
            @RequestParam("scheduledAt") String scheduledAt,
            @RequestParam(value = "media", required = false) MultipartFile media) {
        if (principal == null)
            return "redirect:/login";
        if (content == null || content.isBlank())
            return "redirect:/feed";
        User user = userService.findByUsername(principal.getName());
        LocalDateTime scheduleTime = LocalDateTime.parse(scheduledAt);
        Post post = postService.schedulePost(user.getId(), content.trim(), scheduleTime);
        // Attach media if provided
        if (media != null && !media.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                String ext = getExtension(media.getOriginalFilename());
                String fileName = UUID.randomUUID() + ext;
                Files.copy(media.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                post.setMediaUrl("/uploads/" + fileName);
                postService.save(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/feed";
    }

    @PostMapping("/post/pin/{id}")
    public String pinPost(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        postService.pinPost(id, user.getId());
        return "redirect:/profile/" + user.getUsername();
    }

    @PostMapping("/post/repost")
    public String repost(@RequestParam("originalPostId") Long originalId,
            @RequestParam(value = "comment", required = false) String comment,
            Principal principal) {
        User user = userService.findByUsername(principal.getName());
        postService.repost(user.getId(), originalId, comment);
        return "redirect:/feed";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto registrationDto, Model model) {
        try {
            userService.registerUser(registrationDto);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
