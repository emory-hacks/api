package com.example.demo;
import org.springframework.web.bind.annotation.*;
import com.example.demo.user.UserRepository;
import com.example.demo.user.User;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class HelloController {
    private final UserRepository userRepository;
    public HelloController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    
   @GetMapping("/")
    public String index(){
       return "Spring is officially working!";
   }
}
