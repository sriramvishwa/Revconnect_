package com.revconnect.controller;

import com.revconnect.service.DiscoveryService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        if (query.startsWith("#")) {
            model.addAttribute("results", discoveryService.searchPostsByHashtag(query.substring(1)));
            model.addAttribute("type", "POSTS");
        } else {
            // Simple username search
            model.addAttribute("results", userService.findByUsername(query));
            model.addAttribute("type", "USERS");
        }
        return "search-results";
    }
}
