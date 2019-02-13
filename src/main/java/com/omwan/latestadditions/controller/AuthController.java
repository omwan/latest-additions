package com.omwan.latestadditions.controller;

import com.omwan.latestadditions.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for authentication-related services.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(method = RequestMethod.GET, value = "")
    public void authorize(HttpServletResponse response) {
        authService.authorize(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tokens")
    public void setToken(@RequestParam(name = "code") String token,
                         HttpServletResponse response) {
        authService.setToken(token, response);
    }
}
