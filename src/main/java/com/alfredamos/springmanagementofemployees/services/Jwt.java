package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.entities.Role;
import com.alfredamos.springmanagementofemployees.exceptions.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Data
public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public boolean isExpired(){  return claims.getExpiration().before(new Date());}

    public UUID getUserId(){
        var userId = claims.getSubject();
        return UUID.fromString(userId);
    }

    public Role getUserRole(){
        return Role.valueOf(claims.get("role",String.class));
    }

    public String getUserEmail(){
        return claims.get("email", String.class);
    }

    public String toString(){
        if (claims.isEmpty()){
            throw new UnAuthorizedException("Invalid claims");
        }
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

}
