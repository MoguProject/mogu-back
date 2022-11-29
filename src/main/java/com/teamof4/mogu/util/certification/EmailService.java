package com.teamof4.mogu.util.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.teamof4.mogu.constants.EmailConstants.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public String sendCertificationEmail(String email) {
        String code = createRandomCode();
        SimpleMailMessage mailMessage = createMailMessage(
                email, CERTIFICATION_TITLE, CERTIFICATION_CONTENT+code);
        javaMailSender.send(mailMessage);

        return code;
    }

    public SimpleMailMessage createMailMessage(String to, String title, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(EMAIL_FROM);
        mailMessage.setTo(to);
        mailMessage.setSubject(title);
        mailMessage.setText(content);

        return mailMessage;
    }

    private String createRandomCode() {
        Random random = new Random();
        StringBuffer code = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    code.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    code.append((char) ((int) random.nextInt(26) + 65));
                    break;
                case 2:
                    code.append(random.nextInt(9));
                    break;
            }
        }
        return code.toString();
    }
}
