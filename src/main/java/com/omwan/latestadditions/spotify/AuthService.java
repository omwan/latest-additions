package com.omwan.latestadditions.spotify;

/**
 * Services relating to authentication.
 */
public interface AuthService {

    void authorize();

    void setToken(String token);
}
