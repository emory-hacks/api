package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private EmailCodeController emailCodeController;
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{email}")
    @PreAuthorize("authentication.name == #email or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found.");

        }
        return ResponseEntity.ok(userOptional.get());
    }


    @PostMapping("/register/initiate")
    public ResponseEntity<?> initiateRegistration(@RequestBody RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Error: Email already taken!");

        }
        GenerateUserCodeRequest gRequest = new GenerateUserCodeRequest();
        gRequest.setEmail(registerRequest.getEmail());
        emailCodeController.generateUserCode(gRequest);
        return ResponseEntity.ok("Verification code sent");

    }
    @PostMapping("/register/complete")
    public ResponseEntity<?> completeRegistration(@RequestBody VerifyUserCodeRequest completeRequest){
        VerifyUserCodeRequest vRequest = new VerifyUserCodeRequest();

        vRequest.setEmail(completeRequest.getEmail());
        vRequest.setNewPassword(completeRequest.getNewPassword());
        vRequest.setCurtime(java.time.LocalDateTime.now());
        emailCodeController.verifyUserCode(vRequest);
        User user = new User();
        user.setEmail(completeRequest.getEmail());
        String hashedPassword = passwordEncoder.encode(completeRequest.getNewPassword());
        user.setPassword(hashedPassword);
        user.setName(completeRequest.getEmail());
        user.setRole("participant");
        user.setTeamName("no team");
        // hash password here
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }
    @PutMapping("/promote/{email}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found.");
        }
        User user = userOptional.get();
        user.setRole("admin");
        userRepository.save(user);
        return ResponseEntity.ok("User " + email + " has been PROMOTED to admin");
    }
    @PutMapping("/{email}/add-points")
    public ResponseEntity<?> addPoints(@PathVariable String email, @RequestParam int amount){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");

        }
        User user = userOptional.get();
        user.setPoints(user.getPoints() + amount);
        userRepository.save(user);
        return ResponseEntity.ok("Successfuly added " + amount + " points to " + email + ". New balance: " + user.getPoints());

    }



}