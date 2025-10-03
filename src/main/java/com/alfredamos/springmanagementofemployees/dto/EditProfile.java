package com.alfredamos.springmanagementofemployees.dto;

import com.alfredamos.springmanagementofemployees.entities.Gender;
import com.alfredamos.springmanagementofemployees.entities.Role;
import com.alfredamos.springmanagementofemployees.validations.ValueOfEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditProfile {
    private String address;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @ValueOfEnum(enumClass = Gender.class, message = "It must be either a Male or Female")
    private Gender gender;

    @NotBlank(message = "Image is required.")
    private String image;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Password is required.")
    private String password;

    private Role role;

}
