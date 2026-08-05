package com.example.demo.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubAuthRequest {
    @JsonProperty("accessToken")
    @JsonAlias({"access_token", "accessToken"})
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
