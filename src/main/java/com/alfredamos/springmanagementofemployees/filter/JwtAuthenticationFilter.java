package com.alfredamos.springmanagementofemployees.filter;

import com.alfredamos.springmanagementofemployees.exceptions.UnAuthorizedException;
import com.alfredamos.springmanagementofemployees.services.JwtService;
import com.alfredamos.springmanagementofemployees.utils.AuthParams;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.alfredamos.springmanagementofemployees.services.UserAuthService;
import com.alfredamos.springmanagementofemployees.repositories.TokenRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserAuthService userAuthService;
    private final TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        var accessCookies = request.getCookies(); //----> Get all cookies.
        var accessToken = mySpecificCookieValue(accessCookies); //----> Get access token

        var requestURI = request.getRequestURI(); //----> Get current uri.



        //----> Check token only for non-public routes.
        if(!publicRoutes().contains(requestURI) && accessToken != null && !accessToken.isEmpty()) {
            //----> Get the jwt token.
            var jwt = jwtService.parseToken(accessToken);

            //----> Check the validity of jwt.
            if(jwt == null || jwt.isExpired()) {
                throw new JwtException("Invalid JWT token");
            }

            var role = jwt.getUserRole(); //----> Get the role of the current user.
            var email = jwt.getUserEmail(); //----> Get the email of current user.

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userAuthService.loadUserByUsername(email);

                var isTokenValid = tokenRepository.findByAccessToken(accessToken)
                        .map(t ->  !t.isExpired() && !t.isRevoked()).orElse(false);

                if (!jwt.isExpired() && userDetails != null && isTokenValid) {

                    //----> Authenticate the current user.
                    var authentication = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority(AuthParams.role + role))
                    );

                    //----> Set authentication details.
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //----> Update security context info.
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        }

            try {
                filterChain.doFilter(request, response);
            }catch (Exception ex){
               throw new UnAuthorizedException("You need to log in to access this resource");
            }



    }

    private String mySpecificCookieValue(Cookie[] cookies) {
        if(cookies == null || cookies.length == 0) return null;

        return Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals(AuthParams.accessToken))
                .map(Cookie::getValue)
                .findFirst().orElse(null);

    }

    private List<String> publicRoutes(){
        return Arrays.asList("/api/auth/login", "/api/auth/refresh", "/api/auth/signup");

    }

}