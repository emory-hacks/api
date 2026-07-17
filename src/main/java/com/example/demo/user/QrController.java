package com.example.demo.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/qr")
public class QrController {

    private final UserRepository userRepository;

    public QrController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{email}/qr-token")
    public ResponseEntity<?> getQrToken(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found.");
        }
        return ResponseEntity.ok(userOptional.get().getQrToken());
    }

    @PostMapping("/{email}/checkin")
    public ResponseEntity<?> checkinUser(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found.");
        }
        if (user.get().isCheckedIn()) {
            return ResponseEntity.status(409).body("Error: User already checked in.");
        }
        user.get().setCheckedIn(true);
        userRepository.save(user.get());
        return ResponseEntity.ok(user.get().getName() + " has checked in!");
    }

    @PostMapping("/score")
    public ResponseEntity<?> updateScore(@RequestBody ScoreRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found.");
        }
        User user = userOptional.get();
        user.setScore(user.getScore() + request.getDelta());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "score", user.getScore()
        ));
    }
}
