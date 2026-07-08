package com.example.demo.user;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    public AuthController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        //make sure to generate JWT string (example might be String jwtToken = jwtUtils.generateToken(username);)
        //REPLACE BELOW WITH GENERATED CRYPTOGRAPHIC STRING FROM JAVA LIBRARY FOR COMPLETE SECURITY
        // important things to store in jwt token: username, role, and expiration date.
        //String jwtToken = "put_jwt_place_hodler_here";
        User user = userService.findByEmail(loginRequest.getEmail());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials (user is null)");

        }
        if(user.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        List<String> roles = user.getRoles();
        String realJwtToken = jwtUtils.generateToken(user.getEmail(), roles);
        ResponseCookie cookie = ResponseCookie.from("token", realJwtToken)
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
                .body(new LoginResponse("Logged in successfully!", realJwtToken));
    }
}
