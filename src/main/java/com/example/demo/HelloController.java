package com.example.demo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable UUID id){
        return userRepository.findById(id).orElse(null);
    }

   @GetMapping("/")
    public String index(HttpServletRequest request){
       return "Spring is officially working! " + request.getSession().getId();
   }

   @GetMapping("/csrf-token")
   public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
   }
}
