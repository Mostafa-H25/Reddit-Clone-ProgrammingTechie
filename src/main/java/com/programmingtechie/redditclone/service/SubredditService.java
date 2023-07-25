package com.programmingtechie.redditclone.service;

import com.programmingtechie.redditclone.dto.SubredditDto;
import com.programmingtechie.redditclone.exception.SpringRedditException;
import com.programmingtechie.redditclone.model.Subreddit;
import com.programmingtechie.redditclone.repository.SubredditRepository;
import com.programmingtechie.redditclone.repository.UserRepository;
import com.programmingtechie.redditclone.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    
    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = mapDtoToSubreddit(subredditDto);
        subredditRepository.save(subreddit);

        subredditDto.setId(subreddit.getSubredditId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapSubredditToDto)
                .collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException("No subreddit found with this id"));
        return mapSubredditToDto(subreddit);
    }

    private Subreddit mapDtoToSubreddit(SubredditDto subredditDto) {
        return Subreddit.builder()
                .name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .createdDate(Instant.now())
                .user(authService.getCurrentUser())
                .build();
    }

    private SubredditDto mapSubredditToDto(Subreddit subreddit) {
        return SubredditDto.builder()
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .id(subreddit.getSubredditId())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }
}
