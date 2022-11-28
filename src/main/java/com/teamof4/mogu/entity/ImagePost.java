package com.teamof4.mogu.entity;

import com.teamof4.mogu.repository.ImagePostRepository;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImagePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static ImagePost createImagePost(Image image, Post post) {
        return ImagePost.builder()
                .image(image)
                .post(post).build();
    }

}
