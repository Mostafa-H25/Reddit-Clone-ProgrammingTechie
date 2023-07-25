//package com.programmingtechie.redditclone.mapper;
//
//import com.programmingtechie.redditclone.dto.SubredditDto;
//import com.programmingtechie.redditclone.model.Post;
//import com.programmingtechie.redditclone.model.Subreddit;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface SubredditMapper {
//
//
//    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
//    SubredditDto mapSubredditToDto(Subreddit subreddit);
//
//    default Integer mapPosts(List<Post> numberOfPosts) {
//        return numberOfPosts.size();
//    }
//
//
//}
