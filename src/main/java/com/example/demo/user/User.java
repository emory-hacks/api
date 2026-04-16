package com.example.demo.user;
import jakarta.persistence.*;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "users")
public class User {

    @Column(columnDefinition = "VARCHAR")
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String username;
    private String password;
    private int points = 0;

    @ElementCollection
    private ArrayList<String> s = new ArrayList<String>();
    private int numEvents = 0;
    public User(){}
    public User(String name, long Id){
        this.name = name;
        this.Id = Id;
    }
    public String getName(){ return name; }
    public Long getId(){ return Id; }
    public int getNumEvents(){ return numEvents; }
    public void addEvent(String event){
        s.add(event);
        numEvents++;
    }
    public int getPoints(){ return points;}
    public void addPoints(int points){ this.points += points; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    //private final UserRepository userRepo = new UserRepository();
    public class e extends Exception{
        public e(String message) {
                super(message);
        }
    }
    public void setPassword(String password){
        this.password=password;
    }

    /*public void setPassword(String password) throws e {
        boolean digit = password.matches(".*[0-9].*");
        boolean characters = password.matches(".*\\d{8}.*");
        boolean specialCharacter = password.matches(".*[\\D&&\\W].*");
        if(digit && characters && specialCharacter){
            this.password = password;
        }
        else{
            throw new e("Password invalid. Must include at least eight characters, one letter, and one symbol.");

            }
        }*/


         /*@Repository
        public interface UserRepository extends JpaRepository<User, Long>{

         }*/
         //@Service
        //public class UserService(UserRepository userRepository){
            //private final UserRepository userRepo;
            //private final UserRepository userRepo;

        // }


}
