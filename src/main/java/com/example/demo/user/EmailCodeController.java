package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
@Service
@RestController
@RequestMapping("/api")
public class EmailCodeController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    public EmailCodeController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

    @PostMapping("/verify-user-code")
    public ResponseEntity<?> verifyUserCode(@RequestBody VerifyUserCodeRequest request) {
        Optional<EmailCode> emailCodeOptional = emailCodeRepository.findById(request.getEmail());
        if (emailCodeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid or expired code.");
        }

        EmailCode emailCode = emailCodeOptional.get();
        boolean codeMatches = emailCode.getCode().equals(request.getInputtedCode());
        boolean notExpired = !request.getCurtime().isAfter(emailCode.getExpiration());

        if (!codeMatches || !notExpired) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid or expired code.");
        }
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User account not found.");
        }
        User user = userOptional.get();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        emailCodeRepository.delete(emailCode);


        return ResponseEntity.ok("Code verified, password updated successfully.");
    }
}
