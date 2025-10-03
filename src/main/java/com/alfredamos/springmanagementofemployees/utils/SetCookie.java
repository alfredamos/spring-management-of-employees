package com.alfredamos.springmanagementofemployees.utils;

import jakarta.servlet.http.Cookie;

public class SetCookie {
    static public Cookie makeCookie(CookieParameter  cookieParameter){
        //----> Set cookie.
        var cookie = new Cookie(cookieParameter.getCookieName(), cookieParameter.getCookieValue() == null ? null : cookieParameter.getCookieValue().toString());

        cookie.setHttpOnly(true);
        cookie.setPath(cookieParameter.getCookiePath());
        cookie.setMaxAge(cookieParameter.getExpiration());
        cookie.setSecure(false);

        return  cookie;
    }
}
