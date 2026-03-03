package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.DiscoveryService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", defaultValue = "") String query,
            Model model, Principal principal) {
        if (query.startsWith("#")) {
            model.addAttribute("results", discoveryService.searchPostsByHashtag(query.substring(1)));
            model.addAttribute("type", "POSTS");
        } else {
            // LIKE search by username, first name, or last name
            List<User> users = userService.searchUsers(query.trim());
            model.addAttribute("results", users);
            model.addAttribute("type", "USERS");
        }
        if (principal != null) {
            User currentUser = userService.findByUsername(principal.getName());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("user", currentUser);
        }
        return "search-results";
    }
}
