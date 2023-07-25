package com.programmingtechie.redditclone.repository;

import com.programmingtechie.redditclone.model.Post;
import com.programmingtechie.redditclone.model.Subreddit;
import com.programmingtechie.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<List<Post>> findAllBySubreddit(Subreddit subreddit);

    Optional<List<Post>> findByUser(User user);
}
