package com.alfredamos.springmanagementofemployees.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class SetCookie {
    ////----> Make cookie method.
    static public Cookie makeCookie(CookieParameter  cookieParameter){
        //----> Set cookie.
        var cookie = getCookie(cookieParameter.getCookieName(), cookieParameter.getCookieValue().toString());

        cookie.setHttpOnly(true);
        cookie.setPath(cookieParameter.getCookiePath());
        cookie.setMaxAge(cookieParameter.getExpiration());
        cookie.setSecure(false);
        return cookie;
    }

    ////----> Delete cookie method.
    static public void deleteCookie(String cookieName, String cookiePath, HttpServletResponse response) {
        //----> Set cookie.
        var cookie = getCookie(cookieName, null);

        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        cookie.setSecure(false);

        //----> Set cookie on response.
        response.addCookie(cookie);
    }

    static public Cookie getCookie(String cookieName, String cookieValue) {
        //----> Set cookie.
        return new Cookie(cookieName, cookieValue);
    }
}
