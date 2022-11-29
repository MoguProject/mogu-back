package com.teamof4.mogu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamof4.mogu.dto.ProjectStudyDto;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Size(max = 45)
    private String preferredMethod;

    private String region;

    private String period;

    @Column(name = "is_opened")
    private boolean openStatus;

    private int memberCount;

    private LocalDate startAt;

    @JsonIgnore
    @OneToMany(mappedBy = "projectStudy")
    private List<PostSkill> postSkills = new ArrayList<>();

    public void updateProjectStudy(ProjectStudyDto.Request dto) {
        this.preferredMethod = dto.getPreferredMethod();
        this.region = dto.getRegion();
        this.period = dto.getPeriod();
        this.openStatus = dto.isOpenStatus();
        this.memberCount = dto.getMemberCount();
        this.startAt = dto.getStartAt();
    }
}
