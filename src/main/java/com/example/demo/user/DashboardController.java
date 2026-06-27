package com.example.demo.user;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api")
public class DashboardController {
    @GetMapping("/dashboard-data")
    public ResponseEntity<?> getDashboardData(
            @CookieValue(name = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }
        try{
            return ResponseEntity.ok("Secure data from PostgreSQL");

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token");
        }
    }

}
