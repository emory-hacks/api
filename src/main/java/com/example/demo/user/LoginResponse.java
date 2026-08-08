package com.example.demo.user;

public class LoginResponse {
    private String message;
    private String token;
    private String email;

    public LoginResponse(String message, String token){
        this.message = message;
        this.token = token;
    }

    public LoginResponse(String message, String token, String email){
        this.message = message;
        this.token = token;
        this.email = email;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
