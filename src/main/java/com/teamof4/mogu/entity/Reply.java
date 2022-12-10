package com.teamof4.mogu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_reply_id")
    private Reply parentReply;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "to_user_id")
    private User parentUser;

    @OneToMany(mappedBy = "parentReply")
    @OrderBy("createdAt asc")
    private List<Reply> children = new ArrayList<>();

    private String content;

    private boolean isDeleted;

    public void setParentReply(Reply parentReply) {
        this.parentReply = parentReply;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public static Reply createReply(Post post, User user, String content) {
        return Reply.builder()
                .post(post)
                .user(user)
                .content(content).build();
    }

    public void updateReply(String content) {
        this.content = content;
    }

    public void changeDeleteStatus() {
        this.isDeleted = true;
    }

}
