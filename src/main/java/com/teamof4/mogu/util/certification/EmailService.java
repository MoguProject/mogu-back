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
                email, CERTIFICATION_TITLE, CERTIFICATION_CONTENT + code);
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
                    code.append(generateRandomUpperCaseAlphabet(random));
                    break;
                case 1:
                    code.append(generateRandomLowerCaseAlphabet(random));
                    break;
                case 2:
                    code.append(generateRandomNumber(random));
                    break;
            }
        }
        return code.toString();
    }

    public String sendNewPasswordEmail(String email) {
        String newPassword = createRandomPassword();

        SimpleMailMessage mailMessage = createMailMessage(
                email, NEW_PASSWORD_TITLE, NEW_PASSWORD_CONTENT + newPassword);
        javaMailSender.send(mailMessage);

        return newPassword;
    }

    private String createRandomPassword() {
        Random random = new Random();
        StringBuffer newPassword = new StringBuffer();
        boolean[] checkCaseUsed = {false, false, false, false};

        for (int i = 0; i < 20; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    newPassword.append(generateRandomUpperCaseAlphabet(random));
                    checkCaseUsed[index] = true;
                    break;
                case 1:
                    newPassword.append(generateRandomLowerCaseAlphabet(random));
                    checkCaseUsed[index] = true;
                    break;
                case 2:
                    newPassword.append(generateRandomNumber(random));
                    checkCaseUsed[index] = true;
                    break;
                case 3:
                    newPassword.append(generateRandomSpecialSymbol(random));
                    checkCaseUsed[index] = true;
            }

            if(i==19&&!(checkCaseUsed[0]&&checkCaseUsed[1]&&checkCaseUsed[2]&&checkCaseUsed[3])) {
                System.out.println("===============new============");
                newPassword = new StringBuffer();
                i=-1;
            }
        }

        return newPassword.toString();
    }

    private char generateRandomUpperCaseAlphabet(Random random) {
        return (char) ((int) random.nextInt(26) + 97);
    }

    private char generateRandomLowerCaseAlphabet(Random random) {
        return (char) ((int) random.nextInt(26) + 65);
    }

    private int generateRandomNumber(Random random) {
        return random.nextInt(9);
    }

    private char generateRandomSpecialSymbol(Random random) {
        return SPECIAL_SYMBOL_LIST[(int) random.nextInt(8)];
    }
}
