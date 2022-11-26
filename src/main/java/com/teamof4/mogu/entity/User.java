package com.teamof4.mogu.entity;

import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.LoginResponse;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.security.TokenService;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
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

    @OneToMany(mappedBy = "user")
    private List<UserSkill> userSkills = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<PostLiked> postLikeds = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Reply> replies = new ArrayList<>();

    public void setImage(Image image) {
        this.image = image;
    }

    public LoginResponse toLoginResponse() {

        return LoginResponse.builder()
                .profileImageUrl(this.image.getImageUrl())
                .build();
    }
}
