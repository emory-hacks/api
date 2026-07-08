package com.example.demo.user;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User findByEmail(String email) {
       return userRepository.findByEmail(email).orElse(null);
    }
    public User registerNewUser(User user){
        user.setRole("participant");
        return userRepository.save(user);
    }
}
