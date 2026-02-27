package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.UserService;
import com.revconnect.service.PostService;
import com.revconnect.service.ProductService;
import com.revconnect.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

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
    private AnalyticsService analyticsService;

    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username, Model model, Principal principal) {
        User profileUser = userService.findByUsername(username);
        if (profileUser == null)
            return "error/404";

        User currentUser = principal != null ? userService.findByUsername(principal.getName()) : null;

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", postService.getPostsByUserWithPinned(profileUser.getId()));

        if (profileUser.getRole() == com.revconnect.entity.UserRole.BUSINESS) {
            model.addAttribute("products", productService.getProductsByBusiness(profileUser));
        }

        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User userUpdate, Principal principal) {
        // Logic to update user fields would go here.
        // For brevity in this demo expansion, we'll assume a service method handles the
        // merge.
        return "redirect:/profile/" + principal.getName();
    }
}
