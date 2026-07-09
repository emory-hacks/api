package com.example.demo.user;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        var user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty() || !user.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password"));
        }

        //make sure to generate JWT string (example might be String jwtToken = jwtUtils.generateToken(username);)
        //REPLACE BELOW WITH GENERATED CRYPTOGRAPHIC STRING FROM JAVA LIBRARY FOR COMPLETE SECURITY
        // important things to store in jwt token: username, role, and expiration date.
        String jwtToken = "put_jwt_place_hodler_here";
        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                //Set this to true, as browser rejects cross-domain cookeis unlesss they run over HTTPS, so switch once you take this live and move to production env
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
