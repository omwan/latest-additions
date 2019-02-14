package com.omwan.latestadditions.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Utils class to handle functionality relating to cookies.
 */
@Component
public class CookieUtils {

    @Value("${cookie.domain}")
    private String cookieDomain;

    /**
     * Create a cookie with the given name and value and set the domain and related
     * attributes as needed.
     *
     * @param name  name of cookie
     * @param value value of cookie
     * @return cookie instance
     */
    public Cookie buildCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(cookieDomain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    /**
     * Retrieve the value of the given cookie name, or throw an appropriate exception
     * if the cookie does not exist.
     *
     * @param cookieName name of cookie to retrieve value for
     * @return value of cookie
     */
    public String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            throw new RuntimeException("Cookie " + cookieName + " does not exist");
        }
        return cookie.getValue();
    }
}
