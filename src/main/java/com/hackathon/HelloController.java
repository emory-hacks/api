package com.hackathon;
import org.springframework.web.bind.annotation.*;

import com.hackathon.user.User;
import com.hackathon.user.UserRepository;

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
        return null;
        //return new User("Owen", 92374);
    }
}
