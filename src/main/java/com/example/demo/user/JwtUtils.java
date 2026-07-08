package com.example.demo.user;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
@Component
public class JwtUtils {
    //TEMP KEY. MAKE PRIVATE
    private final String secretString = "0qlksjepoqsadnfa;sejpawoietq-9548uqw045u1-34q50waoeiszpx/fz.m,x";
    private final long expirationTime = 900_000;
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    public String generateToken(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    public String getEmailFromToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }


}
