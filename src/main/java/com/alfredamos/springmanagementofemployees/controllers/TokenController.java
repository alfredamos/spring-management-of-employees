package com.alfredamos.springmanagementofemployees.controllers;

import com.alfredamos.springmanagementofemployees.entities.Token;
import com.alfredamos.springmanagementofemployees.services.TokenService;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createToken(@Valid @RequestBody Token token){
        return ResponseEntity.ok(tokenService.createToken(token));
    }

    @GetMapping("/get-all-valid-tokens/{userId}")
    public ResponseEntity<List<Token>> getAllValidTokensByUserId(@PathVariable("userId") UUID userId){
        return ResponseEntity.ok(tokenService.findAllValidTokensByUserId(userId));
    }

    @GetMapping("/access-token/{accessToken}")
    public ResponseEntity<Token> getTokenByAccessToken(@PathVariable(value = "accessToken") String accessToken){
        return ResponseEntity.ok(tokenService.findTokenByAccessToken(accessToken));
    }

    @GetMapping("/refresh-token/{refreshToken}")
    public ResponseEntity<Token> getTokenByRefreshToken(@PathVariable(value = "refreshToken") String refreshToken){
        return ResponseEntity.ok(tokenService.findTokenByRefreshToken(refreshToken));
    }

    @DeleteMapping("/delete-by-user-id/{userId}")
    public ResponseEntity<ResponseMessage> deleteTokensByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(tokenService.deleteAllInvalidTokensByUserId(userId));
    }

    @PatchMapping("/revoke-valid-tokens/{userId}")
    public ResponseEntity<ResponseMessage> revokeTokensByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(tokenService.revokedAllTokensByUserId(userId));
    }

    @DeleteMapping("/all/delete-all")
    public ResponseEntity<ResponseMessage> deleteAllTokens() {
        return ResponseEntity.ok(tokenService.deleteAllInvalidTokens());
    }
}
