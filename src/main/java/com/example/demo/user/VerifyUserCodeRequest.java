package com.example.demo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class VerifyUserCodeRequest {
    private String email;
    @JsonProperty("inputted_code")
    private String inputtedCode;
    private LocalDateTime curtime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInputtedCode() {
        return inputtedCode;
    }

    public void setInputtedCode(String inputtedCode) {
        this.inputtedCode = inputtedCode;
    }

    public LocalDateTime getCurtime() {
        return curtime;
    }

    public void setCurtime(LocalDateTime curtime) {
        this.curtime = curtime;
    }
}
