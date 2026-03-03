package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.UserService;
import com.revconnect.service.MessageService;
import com.revconnect.service.PostService;
import com.revconnect.service.ProductService;
import com.revconnect.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private FollowService followService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private NotificationRepository notificationRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username, Model model, Principal principal) {
        return loadProfile(username, "feed", model, principal);
    }

    @GetMapping("/{username}/about")
    public String viewProfileAbout(@PathVariable String username, Model model, Principal principal) {
        return loadProfile(username, "about", model, principal);
    }

    @GetMapping("/{username}/media")
    public String viewProfileMedia(@PathVariable String username, Model model, Principal principal) {
        return loadProfile(username, "media", model, principal);
    }

    private String loadProfile(String username, String activeTab, Model model, Principal principal) {
        User profileUser = userService.findByUsername(username);
        if (profileUser == null)
            return "redirect:/feed";

        User currentUser = principal != null ? userService.findByUsername(principal.getName()) : null;

        // Enforce privacy: if profile is private and visitor is not owner and not
        // connected, show restricted
        boolean isOwner = currentUser != null && currentUser.getId().equals(profileUser.getId());
        if (profileUser.isPrivate() && !isOwner) {
            boolean isConnected = currentUser != null &&
                    connectionService.getConnections(currentUser.getId()).stream()
                            .anyMatch(c -> c.getId().equals(profileUser.getId()));
            if (!isConnected) {
                model.addAttribute("profileUser", profileUser);
                model.addAttribute("user", currentUser);
                if (currentUser != null)
                    model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(currentUser));
                return "private-profile";
            }
        }

        java.util.List<com.revconnect.entity.Post> allPosts = postService.getPostsByUserWithPinned(profileUser.getId());

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", allPosts);
        model.addAttribute("activeTab", activeTab);
        // Media tab: only posts that have a mediaUrl
        model.addAttribute("mediaPosts", allPosts.stream()
                .filter(p -> p.getMediaUrl() != null && !p.getMediaUrl().isEmpty())
                .collect(java.util.stream.Collectors.toList()));
        model.addAttribute("user", currentUser); // needed by navbar
        if (currentUser != null) {
            model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(currentUser));
            long unreadNotifCount = notificationRepository.countByRecipientAndIsReadFalse(currentUser);
            model.addAttribute("unreadCount", unreadNotifCount);
            model.addAttribute("isFollowing", followService.isFollowing(currentUser.getId(), profileUser.getId()));
        } else {
            model.addAttribute("isFollowing", false);
        }

        if (profileUser.getRole() == com.revconnect.entity.UserRole.BUSINESS) {
            model.addAttribute("products", productService.getProductsByBusiness(profileUser));
        }
        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(user));
        return "edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(Principal principal,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam(value = "businessName", required = false) String businessName,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "businessAddress", required = false) String businessAddress,
            @RequestParam(value = "businessHours", required = false) String businessHours,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        if (principal == null)
            return "redirect:/login";

        User user = userService.findByUsername(principal.getName());

        // Update basic fields (only if provided)
        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        if (bio != null)
            user.setBio(bio);
        if (location != null)
            user.setLocation(location);
        if (website != null)
            user.setWebsite(website);
        if (category != null)
            user.setCategory(category);
        if (contactEmail != null)
            user.setContactEmail(contactEmail);
        if (businessName != null)
            user.setBusinessName(businessName);
        if (industry != null)
            user.setIndustry(industry);
        if (businessAddress != null)
            user.setBusinessAddress(businessAddress);
        if (businessHours != null)
            user.setBusinessHours(businessHours);

        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                String ext = getExtension(profilePicture.getOriginalFilename());
                String fileName = "avatar_" + user.getId() + "_" + UUID.randomUUID() + ext;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                user.setProfilePictureUrl("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace(); // Profile still saves without new picture on error
            }
        }

        userRepository.save(user);
        return "redirect:/profile/" + user.getUsername();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
