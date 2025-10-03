package com.alfredamos.springmanagementofemployees.controllers;

import com.alfredamos.springmanagementofemployees.mapper.AuthMapper;
import com.alfredamos.springmanagementofemployees.dto.*;
import com.alfredamos.springmanagementofemployees.services.AuthService;
import com.alfredamos.springmanagementofemployees.utils.AuthParams;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthMapper authMapper;

    @PatchMapping("/change-password")
    public ResponseEntity<ResponseMessage> changePassword(@Valid @RequestBody ChangePassword changePassword) {
        return ResponseEntity.ok(authService.changePassword(changePassword));
    }

    @PatchMapping("/change-name")
    public ResponseEntity<ResponseMessage> changeName(@Valid @RequestBody ChangeName changeName) {
        return ResponseEntity.ok(authService.changeName(changeName));
    }

    @PatchMapping("/change-image")
    public ResponseEntity<ResponseMessage> changeImage(@Valid @RequestBody ChangeImage changeImage) {
        return ResponseEntity.ok(authService.changeImage(changeImage));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(authMapper.toUserDto(authService.getCurrentLoggedInUser()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody Login login, HttpServletResponse  response) {
        return ResponseEntity.ok(authService.loginUser(login, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue(value = AuthParams.refreshToken) String refresh, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshUserToken(refresh, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody Signup signup) {
        return ResponseEntity.ok(authService.signupUser(signup));
    }

}
