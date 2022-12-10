package com.teamof4.mogu.service;

import com.teamof4.mogu.constants.DefaultImageConstants;
import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.repository.*;
import com.teamof4.mogu.util.certification.EmailService;
import com.teamof4.mogu.util.encryption.EncryptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.teamof4.mogu.constants.DefaultImageConstants.DEFAULT_PROFILE_IMAGE_ID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserService.class)
@MockBean(JpaMetamodelMappingContext.class)
public class UserServiceTest {

    @MockBean
    ImageService imageService;

    @MockBean
    ImageRepository imageRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostRepository postRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @MockBean
    UserSkillRepository userSkillRepository;

    @MockBean
    SkillRepository skillRepository;

    @MockBean
    EncryptionService encryptionService;

    @MockBean
    EmailService emailService;

    @InjectMocks
    UserService userService;

    SaveRequest createUserDto() {
        return SaveRequest.builder()
                .email("junesuck1234@gmail.com")
                .password("1234qwer!")
                .name("최준석")
                .nickname("frost11")
                .phone("01098765432")
                .build();
    }

    User createUser(SaveRequest saveRequest) {
        return saveRequest.toEntity();
    }

    @Test
    @DisplayName("회원가입 - 회원가입 성공")
    void create_Success() throws Exception {
//        SaveRequest saveRequest = createUserDto();
//        saveRequest.encryptPassword(encryptionService);
//        User user = saveRequest.toEntity();
//
//        doNothing().when(userRepository).save(user);
//        when(imageRepository.findById(DEFAULT_PROFILE_IMAGE_ID)).thenReturn(Image);

    }
}
