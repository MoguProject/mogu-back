package com.teamof4.mogu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Where(clause = "is_deleted = '0'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Valid
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Size(max = 50)
    private String title;

    private String content;

    private int view;

    private boolean isDeleted;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<ImagePost> images;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Like> likes;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replies;

    @OneToOne(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private ProjectStudy projectStudies;

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeStatus() {
        this.isDeleted = true;
    }

    public void addViewCount(int view) {
        this.view = view + 1;
    }

}
