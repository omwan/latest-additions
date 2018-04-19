package com.omwan.latestadditions.service;

/**
 * Services relating to authentication.
 */
public interface AuthService {

    void authorize();

    void setToken(String token);
}
