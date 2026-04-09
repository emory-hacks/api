package com.example.demo;
import org.springframework.web.bind.annotation.*;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import java.util.List;
@RestController
@CrossOrigin(origins = "*")
public class HelloController {
    private final UserRepository userRepository;
    public HelloController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @PostMapping("/user/add")
    public User addUser(@RequestBody User user){
        return userRepository.save(user);
    }
    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
   @GetMapping("/")
    public String index(){
       return "Spring is officially working!";
   }
    @GetMapping("/user")
   public User getUser(){
        return new User("Owen", 92374);
    }
}
