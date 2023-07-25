package com.programmingtechie.redditclone.service;

import com.programmingtechie.redditclone.dto.CommentDto;
import com.programmingtechie.redditclone.dto.VoteDto;
import com.programmingtechie.redditclone.exception.SpringRedditException;
import com.programmingtechie.redditclone.model.Comment;
import com.programmingtechie.redditclone.model.Post;
import com.programmingtechie.redditclone.model.Vote;
import com.programmingtechie.redditclone.model.VoteType;
import com.programmingtechie.redditclone.repository.PostRepository;
import com.programmingtechie.redditclone.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public void vote(VoteDto voteDto) {
        Vote vote = mapDtoToVote(voteDto);

        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new SpringRedditException("Post " + voteDto.getPostId() + " is not found."));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())){
            throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d this post.");
        }
        if(voteDto.getVoteType().equals(VoteType.UPVOTE)){
            post.setVoteCount(post.getVoteCount() + 1);
        }else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(vote);
        postRepository.save(post);
    }


    private Vote mapDtoToVote(VoteDto voteDto){
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .user(authService.getCurrentUser())
                .post(postRepository.findById(voteDto.getPostId())
                        .orElseThrow(() -> new SpringRedditException("Post " + voteDto.getPostId() + " is not found.")))
                .build();
    }

    private VoteDto mapCommentToDto(Vote vote){
        return VoteDto.builder()
                .postId(vote.getPost().getPostId())
                .voteType(vote.getVoteType())
//                .username(comment.getUser().getUsername())
                .build();
    }

}
