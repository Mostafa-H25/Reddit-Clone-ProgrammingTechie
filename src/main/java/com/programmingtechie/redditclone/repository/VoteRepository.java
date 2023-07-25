package com.programmingtechie.redditclone.repository;

import com.programmingtechie.redditclone.model.Post;
import com.programmingtechie.redditclone.model.User;
import com.programmingtechie.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
