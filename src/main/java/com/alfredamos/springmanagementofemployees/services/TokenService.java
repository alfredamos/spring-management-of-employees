package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.entities.Token;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;
import com.alfredamos.springmanagementofemployees.repositories.TokenRepository;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    ////----> Create new token function.
    public ResponseMessage createToken(Token token){
        //----> Store the token object in the database.
        tokenRepository.save(token);

        //----> Send back the response.
        return new ResponseMessage();
    }

    ////----> Find all valid token function.
    public List<Token> findAllValidTokensByUserId(UUID userId){
        //----> Get all valid tokens associated with this user.
        return tokenRepository.findAllValidTokensByUser(userId);
    }

    ////----> Delete all invalid tokens
    public ResponseMessage deleteAllInvalidTokensByUserId(UUID userId){
        System.out.println("In token-service, delete all invalid tokens, userId: " + userId);
        //----> Delete all invalid tokens.
        tokenRepository.deleteAllInvalidTokensByUserId(userId);

        //----> Send back the response.
        return new ResponseMessage("All invalid tokens have been deleted successfully!", "success", HttpStatus.OK);
    }

    ////----> Delete all invalid tokens
    public ResponseMessage deleteAllInvalidTokens(){
        System.out.println("In token-service, delete all invalid tokens");
        //----> Delete all invalid tokens.
        tokenRepository.deleteAllInvalidTokens();

        //----> Send back the response.
        return new ResponseMessage("All invalid tokens have been deleted successfully!", "success", HttpStatus.OK);
    }

    ////----> Find token by access token.
    public Token findTokenByAccessToken(String accessToken){
        //----> Retrieve token object with the given access token and send back result.
        return tokenRepository.findByAccessToken(accessToken).orElseThrow(() -> new NotFoundException("Token not found"));
    }

    ////----> Find token by access token.
    public Token findTokenByRefreshToken(String refreshToken){
        //----> Retrieve token object with the given refresh token and send back response.
        return tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new NotFoundException("Token not found"));
    }

    ////----> Revoked all token by user-id function.
    public ResponseMessage revokedAllTokensByUserId(UUID userId){
        //----> Retrieve all valid tokens associated with this user.
        var tokens = findAllValidTokensByUserId(userId);

        //----> Invalidate all tokens associated with this user.
        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);

        });

        //----> Save all revoked tokens in the database.
        tokenRepository.saveAll(tokens);

        //----> Send back response.
        return new ResponseMessage("All invalid tokens have been updated successfully!", "success", HttpStatus.OK);

    }
}
