package com.dailelog.response;

import com.dailelog.domain.Comment;
import com.dailelog.domain.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
* 서비스 정책의 맞는 클래스
*/
@Getter

public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime regDate;
    private final List<Comment> comments;
    //생성자 오버로딩
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.regDate = post.getRegDate();
        this.comments = post.getComments();
    }

    @Builder
    public PostResponse(Long id, String title, String content,List<Comment> comments) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
        this.regDate = LocalDateTime.now();
        this.comments = comments;
    }
}
