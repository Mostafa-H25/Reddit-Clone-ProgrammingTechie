package com.programmingtechie.redditclone.service;

import com.programmingtechie.redditclone.dto.CommentDto;
import com.programmingtechie.redditclone.dto.PostRequest;
import com.programmingtechie.redditclone.dto.PostResponse;
import com.programmingtechie.redditclone.exception.PostNotFoundException;
import com.programmingtechie.redditclone.exception.SpringRedditException;
import com.programmingtechie.redditclone.model.*;
import com.programmingtechie.redditclone.repository.CommentRepository;
import com.programmingtechie.redditclone.repository.PostRepository;
import com.programmingtechie.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailService mailService;
    public void save(CommentDto commentDto) {
        Comment comment = mapDtoToComment(commentDto);
        commentRepository.save(comment);

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new SpringRedditException("Post " + commentDto.getPostId() + " is not found."));

        String postUrl=post.getUrl();

        mailService.sendMail(new NotificationEmail(comment.getUser().getUsername() + " commented on your post",
                post.getUser().getEmail(),
                comment.getUser().getUsername() + " commented on your post: " + post.getUrl()));
    }

    public List<CommentDto> getAllCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SpringRedditException("Post " + postId + " is not found."));
        return commentRepository.findAllByPost(post)
                .orElseThrow(() -> new PostNotFoundException("There are no comments for this post: " + post.getPostName()))
                .stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllCommentsByUser(String username) {
        User user = userRepository.findAllByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " is not found."));
        return commentRepository.findAllByUser(user)
                .orElseThrow(() -> new PostNotFoundException("There are no comments for this user: " + username))
                .stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    private Comment mapDtoToComment(CommentDto commentDto){
        return Comment.builder()
                .text(commentDto.getText())
                .user(authService.getCurrentUser())
                .createdDate(Instant.now())
                .post(postRepository.findById(commentDto.getPostId())
                        .orElseThrow(() -> new SpringRedditException("Post " + commentDto.getPostId() + " is not found.")))
                .build();
    }

    private CommentDto mapCommentToDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .text(comment.getText())
                .createdDate(Instant.now())
                .username(comment.getUser().getUsername())
                .build();
    }

}
