package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.UserService;
import com.revconnect.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private UserService userService;
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public String showAnalytics(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        if (user.getRole() == com.revconnect.entity.UserRole.PERSONAL) {
            return "redirect:/feed";
        }

        Map<String, Object> stats = analyticsService.getCreatorAnalytics(user);
        model.addAllAttributes(stats);
        model.addAttribute("user", user);

        return "analytics";
    }
}
