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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home() {
        return "redirect:/feed";
    }

    @GetMapping("/feed")
    public String feed(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        Page<Post> feedPage = postService.getFeed(user.getId(), 0, 10);
        model.addAttribute("user", user);
        model.addAttribute("posts", feedPage.getContent());
        return "feed";
    }

    @PostMapping("/post")
    public String createPost(Principal principal, @RequestParam("content") String content) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        postService.createPost(user.getId(), content);
        return "redirect:/feed";
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        postService.deletePost(id, user.getId());
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
