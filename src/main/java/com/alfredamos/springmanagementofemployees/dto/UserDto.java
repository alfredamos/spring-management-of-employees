package com.alfredamos.springmanagementofemployees.dto;

import com.alfredamos.springmanagementofemployees.entities.Gender;
import com.alfredamos.springmanagementofemployees.entities.Role;
import com.alfredamos.springmanagementofemployees.validations.ValueOfEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;

    private String name;

    private String email;

    private String image;

    private Role role;

}
