import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @PostMapping("/create")
    public boolean createUser(@RequestBody User user){
        try{
            userRepository.save(user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        // hash password here
        return userRepository.save(user);
    }
}