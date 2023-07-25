package com.programmingtechie.redditclone.repository;

import com.programmingtechie.redditclone.model.Comment;
import com.programmingtechie.redditclone.model.Post;
import com.programmingtechie.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByPost(Post post);

    Optional<List<Comment>> findAllByUser(User user);
}
