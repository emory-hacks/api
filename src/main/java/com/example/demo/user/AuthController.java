package com.example.demo.user;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        //make sure to generate JWT string (example might be String jwtToken = jwtUtils.generateToken(username);)
        //REPLACE BELOW WITH GENERATED CRYPTOGRAPHIC STRING FROM JAVA LIBRARY FOR COMPLETE SECURITY
        String jwtToken = "put_jwt_place_hodler_here";
        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                //Set this to true, as brosere sreject cross-domain cookeis unlesss they run over HTTPS, so switch once you take this live and move from production env
                .secure(false)
                .path("/")
                .maxAge(15*60)
                //CHANGE THIS TO NONE when implementing frontend/backend, tells browser cookie is traveling across diff domains
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logged in successfully!"));
    }
}
