package com.teamof4.mogu.constants;

public enum CategoryNames {
    PROJECT("프로젝트"),
    STUDY("스터디"),
    FREEBOARD("자유로운 글"),
    PERSONALPROJECT("개인 프로젝트"),
    TEAMPROJECT("팀 프로젝트");

    private String korName;

    CategoryNames(String korName) {
        this.korName = korName;
    }

    public String getKorName() {
        return this.korName;
    }
}