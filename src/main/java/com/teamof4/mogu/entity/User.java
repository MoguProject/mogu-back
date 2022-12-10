package com.teamof4.mogu.entity;

import com.teamof4.mogu.dto.UserDto.LoginInfoResponse;
import com.teamof4.mogu.dto.UserDto.UpdateRequest;
import com.teamof4.mogu.dto.UserDto.MyInfoResponse;
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

    @ManyToOne(fetch = LAZY)
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

    @OneToMany(mappedBy = "user")
    private List<Like> postLikeds = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Reply> replies = new ArrayList<>();

    public void setImage(Image image) {
        this.image = image;
    }

    public LoginInfoResponse toLoginInfoResponse() {
        return LoginInfoResponse.builder()
                .nickname(this.nickname)
                .profileImageUrl(this.image.getImageUrl())
                .build();
    }

    public MyInfoResponse toUserInfoResponse() {
        return MyInfoResponse.builder()
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
        this.nickname = updateRequest.getNickname();
        this.phone = updateRequest.getPhone();
        this.preferredMethod = updateRequest.getPreferredMethod();
        this.region = updateRequest.getRegion();
        this.information = updateRequest.getInformation();
        this.isActivated = updateRequest.getIsActivated();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}
