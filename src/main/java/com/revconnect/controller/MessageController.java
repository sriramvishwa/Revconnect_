package com.revconnect.controller;

import com.revconnect.entity.Message;
import com.revconnect.entity.User;
import com.revconnect.service.MessageService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    /** List all conversations for the logged-in user */
    @GetMapping
    public String inbox(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        User me = userService.findByUsername(principal.getName());
        List<User> conversations = messageService.getConversations(me);

        model.addAttribute("user", me);
        model.addAttribute("conversations", conversations);
        model.addAttribute("lastMessages", buildLastMessages(me, conversations));
        model.addAttribute("selectedUser", null);
        model.addAttribute("thread", List.of());
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(me));
        return "messages";
    }

    /** Open a chat thread with a specific user */
    @GetMapping("/{username}")
    public String thread(@PathVariable String username, Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        User me = userService.findByUsername(principal.getName());
        User other = userService.findByUsername(username);
        if (other == null)
            return "redirect:/messages";

        // Mark messages from 'other' to 'me' as read when opening the thread
        messageService.markThreadAsRead(other, me);

        List<User> conversations = messageService.getConversations(me);
        // make sure the open user appears in the sidebar even if no message yet
        if (!conversations.contains(other))
            conversations.add(0, other);

        List<Message> thread = messageService.getThread(me, other);

        model.addAttribute("user", me);
        model.addAttribute("conversations", conversations);
        model.addAttribute("lastMessages", buildLastMessages(me, conversations));
        model.addAttribute("selectedUser", other);
        model.addAttribute("thread", thread);
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(me));
        return "messages";
    }

    /** Send a message to a user */
    @PostMapping("/{username}")
    public String send(@PathVariable String username,
            @RequestParam("content") String content,
            Principal principal) {
        if (principal == null)
            return "redirect:/login";
        User me = userService.findByUsername(principal.getName());
        User other = userService.findByUsername(username);
        if (other != null && !content.isBlank()) {
            messageService.sendMessage(me, other, content);
        }
        return "redirect:/messages/" + username;
    }

    /** Build a username → last message preview map */
    private Map<String, String> buildLastMessages(User me, List<User> conversations) {
        Map<String, String> map = new LinkedHashMap<>();
        for (User partner : conversations) {
            Message last = messageService.getLastMessage(me, partner);
            if (last != null) {
                String preview = last.getContent();
                if (preview.length() > 45)
                    preview = preview.substring(0, 45) + "…";
                // Prefix "You: " if we were the sender
                if (last.getSender().getId().equals(me.getId()))
                    preview = "You: " + preview;
                map.put(partner.getUsername(), preview);
            } else {
                map.put(partner.getUsername(), "");
            }
        }
        return map;
    }
}
