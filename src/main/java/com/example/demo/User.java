package com.example.demo;
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
    @ElementCollection
    private ArrayList<String> s = new ArrayList<String>();
    private int numEvents = 0;
    public User(){}
    public User(String name, long Id){
        this.name = name;
        this.Id = Id;
    }
    public String getName(){
        return name;
    }
    public Long getId(){
        return Id;
    }
    public int getNumEvents(){
        return numEvents;
    }
    public void addEvent(String event){
        s.add(event);
        numEvents++;
    }

}
