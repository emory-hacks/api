package com.example.demo.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    @JsonProperty("inputted_code")
    private String inputtedCode;

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){this.name = name;}
    public String getInputtedCode(){
        return inputtedCode;
    }
    public void setInputtedCode(String inputtedCode){
        this.inputtedCode = inputtedCode;
    }
}
