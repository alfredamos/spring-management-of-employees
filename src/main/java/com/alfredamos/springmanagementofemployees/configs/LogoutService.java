package com.alfredamos.springmanagementofemployees.configs;

import com.alfredamos.springmanagementofemployees.repositories.TokenRepository;
import com.alfredamos.springmanagementofemployees.utils.AuthParams;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //----> Get the access-token from cookies.
        var accessCookies = request.getCookies(); //----> Get all cookies.
        var accessToken = mySpecificCookieValue(accessCookies); //---> Get access-token.

        //----> Get the first valid token.
        var storedAccessToken = tokenRepository.findByAccessToken(accessToken);

        //----> Invalidate the tokens by setting expire and revoke to true.
        if (storedAccessToken.isPresent()) {
            storedAccessToken.get().setExpired(true);
            storedAccessToken.get().setRevoked(true);

            tokenRepository.save(storedAccessToken.get());
        }
    }

    private String mySpecificCookieValue(Cookie[] cookies){
        if(cookies == null) return "";

        //----> Fetch access-token from cookies.
        return Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals(AuthParams.accessToken))
                .map(Cookie::getValue)
                .findFirst().orElse(null);

    }
}
