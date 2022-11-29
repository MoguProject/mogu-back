package com.teamof4.mogu.entity;

import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.LoginResponse;
import com.teamof4.mogu.dto.UserDto.UpdateRequest;
import com.teamof4.mogu.dto.UserDto.UserInfoResponse;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

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
    @JoinColumn(name = "image_id")
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

    @OneToMany(mappedBy = "user", fetch = LAZY)
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
                .nickname(this.nickname)
                .profileImageUrl(this.image.getImageUrl())
                .build();
    }

    public UserInfoResponse toUserInfoResponse() {
        return UserInfoResponse.builder()
                .profileImageUrl(this.image.getImageUrl())
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .phone(this.phone)
                .isActivated(this.isActivated)
                .preferredMethod(this.preferredMethod)
                .region(this.region)
                .information(this.information)
                .skills(getUserSkillNames())
                .build();
    }

    public List<String> getUserSkillNames() {
        return this.userSkills
                .stream()
                .map(UserSkill::getSkill)
                .map(Skill::getSkillName)
                .collect(Collectors.toList());
    }

    public void updateUser(UpdateRequest updateRequest) {
        this.nickname=updateRequest.getNickname();
        this.phone=updateRequest.getPhone();
        this.information=updateRequest.getInformation();
        this.isActivated=updateRequest.isActivated();
        this.password=updateRequest.getPassword();
        this.preferredMethod=updateRequest.getPreferredMethod();
        this.region=updateRequest.getRegion();


    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}
