package com.example.demo.user;
import jakarta.persistence.*;
import java.util.ArrayList;
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
    public void setPassword(String password) { this.password = password; }

}
