package com.alfredamos.springmanagementofemployees.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeName {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Name is required.")
    private String name;

}
