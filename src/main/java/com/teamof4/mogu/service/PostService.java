package com.teamof4.mogu.service;

import com.teamof4.mogu.constants.SortStatus;
import com.teamof4.mogu.dto.LikeDto;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.entity.*;
import com.teamof4.mogu.exception.post.CategoryNotFoundException;
import com.teamof4.mogu.exception.post.LikeNotFoundException;
import com.teamof4.mogu.exception.post.PostNotFoundException;
import com.teamof4.mogu.exception.post.ReplyNotFoundException;
import com.teamof4.mogu.exception.user.UserNotFoundException;
import com.teamof4.mogu.exception.user.UserNotMatchException;
import com.teamof4.mogu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamof4.mogu.constants.SortStatus.*;
import static com.teamof4.mogu.dto.ReplyDto.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class PostService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final ReplyRepository replyRepository;

    public Page<PostDto.Response> getPostList(Long categoryId, Pageable pageable,
                                              Long currentUserId, SortStatus status) {

        Page<Post> posts = new PageImpl<>(Collections.emptyList());
        Category category = getCategory(categoryId);

        if (status.equals(DEFAULT)) {
            posts = postRepository.findAll(pageable, category);
        } else if(status.equals(LIKES)) {
            posts = postRepository.findAllLikesDesc(pageable, category);
        }

        return new PageImpl<>(entityToListDto(posts, currentUserId), pageable, posts.getTotalElements());
    }

    public PostDto.Response getPostDetails(Long postId, Long currentUserId) {

        Post post = getPost(postId);

        if (!post.getUser().getId().equals(currentUserId)) {
            post.addViewCount(post.getView());
            postRepository.save(post);
        }

        return PostDto.Response.builder()
                .post(post)
                .replies(replyConvertToDto(getReplies(post)))
                .isLiked(isLikedByCurrentUser(currentUserId, post)).build();

    }

    @Transactional
    public Long savePost(PostDto.SaveRequest requestDTO, Long currentUserId) {

        Post post = requestDTO.toEntity(getUser(currentUserId), getCategory(requestDTO.getCategoryId()));

        postRepository.save(post);

        return post.getId();

    }

    @Transactional
    public Long updatePost(Long postId, PostDto.UpdateRequest requestDTO, Long currentUserId) {

        Post post = getPost(postId);

        User user = getUser(currentUserId);
        if (user != post.getUser()) {
            throw new UserNotMatchException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        post.updatePost(requestDTO.getTitle(), requestDTO.getContent());

        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = getPost(postId);

        User user = getUser(currentUserId);
        if (user != post.getUser()) {
            throw new UserNotMatchException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        post.changeStatus();

        postRepository.save(post);
    }

    public LikeDto likeProcess(Long postId, Long currentUserId) {

        User user = getUser(currentUserId);
        Post post = getPost(postId);

        boolean likeStatus = false;
        if (!isLiked(user, post)) {
            likeRepository.save(Like.builder()
                    .user(user)
                    .post(post).build());
            likeStatus = true;
        } else {
            Like like = likeRepository.findByUserAndPost(user, post)
                    .orElseThrow(() -> new LikeNotFoundException("좋아요를 누른 게시물이 없습니다."));
            likeRepository.delete(like);
        }

        return LikeDto.builder()
                .likeStatus(likeStatus)
                .count(likeRepository.countByPost(post)).build();
    }

    public Long saveSuperReply(Long currentUserId, SuperRequest dto) {

        Reply reply = Reply.createReply(getPost(dto.getPostId()), getUser(currentUserId), dto.getContent());

        replyRepository.save(reply);
        return reply.getId();
    }

    public Long saveSubReply(Long currentUserId, Request dto) {
        Reply parentReply = replyRepository.findById(dto.getReplyId())
                .orElseThrow(() -> new IllegalStateException("댓글이 존재하지 않습니다."));

        Reply reply = Reply.createReply(parentReply.getPost(), getUser(currentUserId), dto.getContent());
        reply.setParentUser(parentReply.getUser());

        if (parentReply.getParentReply() == null) {
            reply.setParentReply(parentReply);
        } else {
            reply.setParentReply(parentReply.getParentReply());
        }

        replyRepository.save(reply);
        return reply.getId();
    }

    public Long updateReply(Request dto, Long currentUserId) {
        Reply reply = getReply(dto.getReplyId());

        User user = getUser(currentUserId);
        if (user != reply.getUser()) {
            throw new UserNotMatchException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        reply.updateReply(dto.getContent());

        replyRepository.save(reply);

        return reply.getId();
    }

    public void deleteReply(Long replyId, Long currentUserId) {
        Reply reply = getReply(replyId);

        User user = getUser(currentUserId);
        if (user != reply.getUser()) {
            throw new UserNotMatchException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        reply.changeDeleteStatus();
        replyRepository.save(reply);
    }

    private boolean isLiked(User user, Post post) {
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }

    public boolean isLikedByCurrentUser(Long currentUserId, Post post) {
        boolean isLiked = false;

        if (currentUserId != null && isLiked(getUser(currentUserId), post)) {
            isLiked = true;
        }

        return isLiked;
    }

    private List<PostDto.Response> entityToListDto(Page<Post> posts, Long currentUserId) {
        return posts.stream()
                .map(post -> PostDto.Response.builder()
                        .post(post)
                        .isLiked(isLikedByCurrentUser(currentUserId, post)).build())
                .collect(Collectors.toList());
    }

    public List<Response> replyConvertToDto(List<Reply> replies) {
        List<Response> responseList = new ArrayList<>();

        for (Reply reply : replies) {
            String targetUserNickname = "";
            if (reply.getParentUser() != null) {
                targetUserNickname = reply.getParentUser().getNickname();
            }

            List<Response> children = new ArrayList<>();
            if (!reply.getChildren().isEmpty()) {
                children = replyConvertToDto(reply.getChildren());
            }
            Response response = Response.builder()
                    .reply(reply).targetNickname(targetUserNickname)
                    .children(children)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException("댓글이 존재하지 않습니다."));
    }

    public List<Reply> getReplies(Post post) {
        return post.getReplies().stream()
                .filter(reply -> reply.getParentReply() == null)
                .collect(Collectors.toList());
    }
}