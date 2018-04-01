package com.omwan.latestadditions.spotify;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    void authorize(HttpServletResponse response);

    void setToken(String token, HttpServletResponse response);
}
