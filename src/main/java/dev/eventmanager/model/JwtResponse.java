package dev.eventmanager.model;


public class JwtResponse {

    public String jwtToken;

    public JwtResponse(String token) {
        this.jwtToken = token;
    }
}
