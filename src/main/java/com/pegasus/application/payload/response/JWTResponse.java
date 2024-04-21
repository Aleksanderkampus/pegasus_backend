package com.pegasus.application.payload.response;

public class JWTResponse {

    private String jwtToken;

    private String refreshToken;

    public JWTResponse(String jwtToken, String refreshToken) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
