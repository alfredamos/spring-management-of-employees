package com.alfredamos.springmanagementofemployees.controllers;

import com.alfredamos.springmanagementofemployees.mapper.UserMapper;
import com.alfredamos.springmanagementofemployees.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
