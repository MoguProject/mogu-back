package com.teamof4.mogu.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "user")
    private Image image;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private String phone;

    private Boolean isDeleted;

    private Boolean isActivated;

    private String preferredMethod;

    private String region;

    private String information;

//    @OneToMany(mappedBy = "user")
//    private List<UserSkill> userSkills = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<PostLiked> postLikeds = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Reply> replies = new ArrayList<>();

    @Builder
    public User(Long id, Image image, String email, String name, String nickname,
                String password, String phone, Boolean isDeleted, Boolean isActivated,
                String preferredMethod, String region, String information) {
        this.id = id;
        this.image = image;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.isDeleted = isDeleted;
        this.isActivated = isActivated;
        this.preferredMethod = preferredMethod;
        this.region = region;
        this.information = information;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
