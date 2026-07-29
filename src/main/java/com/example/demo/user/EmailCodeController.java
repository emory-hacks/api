package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class EmailCodeController {
    @Autowired
    private EmailCodeRepository emailCodeRepository;
    @Autowired
    private EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    @PostMapping("/generate-user-code")
    public ResponseEntity<?> generateUserCode(@RequestBody GenerateUserCodeRequest request) {
        String code = String.format("%05d", secureRandom.nextInt(100_000));

        EmailCode emailCode = new EmailCode();
        emailCode.setEmail(request.getEmail());
        emailCode.setCode(code);
        emailCode.setExpiration(LocalDateTime.now().plusMinutes(5));
        emailCodeRepository.save(emailCode);

        try {
            emailService.sendVerificationCode(request.getEmail(), code);
        } catch (MailException | IllegalStateException | IllegalArgumentException e) {
            Throwable cause = e instanceof MailException mailEx ? mailEx.getMostSpecificCause() : e;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + cause.getMessage());
        }

        return ResponseEntity.ok("Verification code sent.");
    }
}
