package com.alfredamos.springmanagementofemployees.configs;


import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfig {
    private String secret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(
             secret.getBytes()
        );
    }
}
