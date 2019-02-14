package com.omwan.latestadditions.component;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for utils relating to cookies.
 */
public class CookieUtilsTest {

    private final static String COOKIE_DOMAIN = "domain";

    @Tested
    private CookieUtils cookieUtils;

    @Before
    public void setup() {
        cookieUtils = new CookieUtils();
        Deencapsulation.setField(cookieUtils, "cookieDomain", COOKIE_DOMAIN);
    }

    /**
     * Assert that cookies are built with the given parameters and have the correct
     * domain set.
     */
    @Test
    public void testBuildCookie() throws Exception {
        Cookie expected = new Cookie("name", "value");
        expected.setDomain(COOKIE_DOMAIN);

        Cookie actual = cookieUtils.buildCookie("name", "value");
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getDomain(), actual.getDomain());
    }

    /**
     * Assert that the value of a cookie with a given name can be retrieved.
     */
    @Test
    public void testGetCookieValue() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        String expectedValue = "value";

        new MockUp<WebUtils>() {
            @Mock
            public Cookie getCookie(HttpServletRequest request, String name) {
                return new Cookie(name, expectedValue);
            }
        };

        String actualValue = cookieUtils.getCookieValue(request, "name");
        assertEquals(expectedValue, actualValue);
    }

    /**
     * Assert that if the getCookieValue method is called with a name of a cookie
     * that does not exist, the appropriate exception is thrown.
     */
    @Test(expected = RuntimeException.class)
    public void testGetCookieValueNullCookie() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();

        new MockUp<WebUtils>() {
            @Mock
            public Cookie getCookie(HttpServletRequest request, String name) {
                return null;
            }
        };

        cookieUtils.getCookieValue(request, "name");
    }
}
