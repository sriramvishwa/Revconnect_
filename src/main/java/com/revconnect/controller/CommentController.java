package com.revconnect.controller;

import com.revconnect.entity.Comment;
import com.revconnect.entity.Notification;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationRepository notificationRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

    /** Return comments for a post as JSON (for inline AJAX loading) */
    @GetMapping("/{postId}/list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> listComments(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            return ResponseEntity.notFound().build();

        List<Map<String, Object>> result = commentRepository
                .findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getId(),
                        "content", c.getContent(),
                        "author", c.getAuthor().getUsername(),
                        "avatarUrl", c.getAuthor().getProfilePictureUrl() != null
                                ? c.getAuthor().getProfilePictureUrl()
                                : "",
                        "time", c.getCreatedAt().format(FMT)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /** Add a comment to a post */
    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Long postId,
            @RequestParam("content") String content,
            Principal principal) {
        if (principal == null)
            return "redirect:/login";
        if (content == null || content.isBlank())
            return "redirect:/feed";

        User author = userService.findByUsername(principal.getName());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Save comment
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        // Increment commentCount on the post
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        // Notify post author (if commenter is not the same person)
        if (!post.getAuthor().getId().equals(author.getId())) {
            Notification n = new Notification();
            n.setRecipient(post.getAuthor());
            n.setSender(author);
            n.setType("COMMENT");
            n.setMessage(author.getUsername() + " commented on your post");
            n.setRelatedEntityId(post.getId());
            n.setRead(false);
            n.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(n);
        }

        return "redirect:/feed";
    }

    /** Delete own comment */
    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Principal principal) {
        if (principal == null)
            return "redirect:/login";
        User user = userService.findByUsername(principal.getName());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Only author can delete their comment
        if (comment.getAuthor().getId().equals(user.getId())) {
            Post post = comment.getPost();
            commentRepository.delete(comment);
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        }
        return "redirect:/feed";
    }
}
