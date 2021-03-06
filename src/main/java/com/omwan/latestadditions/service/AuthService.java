package com.omwan.latestadditions.service;

import javax.servlet.http.HttpServletResponse;

/**
 * Services relating to authentication.
 */
public interface AuthService {

    void authorize(HttpServletResponse response);

    void setToken(String token, HttpServletResponse response);
}
