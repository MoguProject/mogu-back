package com.teamof4.mogu.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    private String imageUrl;

    @Builder
    public Image(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
