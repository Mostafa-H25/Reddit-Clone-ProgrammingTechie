package com.programmingtechie.redditclone.service;

import com.programmingtechie.redditclone.dto.PostRequest;
import com.programmingtechie.redditclone.dto.PostResponse;
import com.programmingtechie.redditclone.exception.PostNotFoundException;
import com.programmingtechie.redditclone.exception.SpringRedditException;
import com.programmingtechie.redditclone.model.Post;
import com.programmingtechie.redditclone.model.Subreddit;
import com.programmingtechie.redditclone.model.User;
import com.programmingtechie.redditclone.repository.PostRepository;
import com.programmingtechie.redditclone.repository.SubredditRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public void save(PostRequest postRequest) {
        Post post = mapDtoToPost(postRequest);
        postRepository.save(post);

        Subreddit subreddit = subredditRepository.findById(post.getSubreddit().getSubredditId())
                .orElseThrow(() -> new SpringRedditException("Subreddit " + postRequest.getSubredditName() + " is not found."));
        subreddit.getPosts().add(post);
        subredditRepository.save(subreddit);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Subreddit " + id + " is not found."));
        return postRepository.findAllBySubreddit(subreddit)
                .orElseThrow(() -> new PostNotFoundException("There are no posts for this subreddit: " + subreddit.getName()))
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findAllByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " is not found."));
        return postRepository.findByUser(user)
                .orElseThrow(() -> new PostNotFoundException("There are no posts for this user: " + username))
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post " + id.toString() + " is not found."));
        return mapPostToDto(post);
    }

    private Post mapDtoToPost(PostRequest postRequest){
        return Post.builder()
                .postName(postRequest.getPostName())
                .url(postRequest.getUrl())
                .description(postRequest.getDescription())
                .voteCount(0)
                .user(authService.getCurrentUser())
                .createdDate(Instant.now())
                .subreddit(subredditRepository.findByName(postRequest.getSubredditName())
                        .orElseThrow(() -> new SpringRedditException("Subreddit " + postRequest.getSubredditName() + " is not found.")))
                .build();
    }

    private PostResponse mapPostToDto(Post post){
        return PostResponse.builder()
                .postId(post.getPostId())
                .postName(post.getPostName())
                .description(post.getDescription())
                .url(post.getUrl())
                .subRedditName(post.getSubreddit().getName())
                .username(post.getUser().getUsername())
                .build();
    }
}
