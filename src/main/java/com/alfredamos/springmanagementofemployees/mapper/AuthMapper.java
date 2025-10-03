package com.alfredamos.springmanagementofemployees.mapper;


import com.alfredamos.springmanagementofemployees.dto.Signup;
import com.alfredamos.springmanagementofemployees.dto.UserDto;
import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.entities.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Data
@Builder
@Service
public class AuthMapper {
    public User toUserCreateEntity(Signup signup) {
        return User.builder()
                .email(signup.getEmail())
                .password(signup.getPassword())
                .name(signup.getName())
                .role(signup.getRole())
                .image(signup.getImage())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .image(user.getImage())
                .build();
    }

    public Employee toEmployeeCreateEntity(Signup signup, User user) {
        return Employee.builder()
                .email(signup.getEmail())
                .age(LocalDate.now().getYear() - signup.getDateOfBirth().getYear())
                .name(signup.getName())
                .phone(signup.getPhone())
                .image(signup.getImage())
                .address(signup.getAddress())
                .gender(signup.getGender())
                .user(user)
                .dateOfBirth(signup.getDateOfBirth())
                .build();
    }
}
