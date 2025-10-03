package com.alfredamos.springmanagementofemployees.utils;

import com.alfredamos.springmanagementofemployees.entities.Role;
import com.alfredamos.springmanagementofemployees.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAndAdmin {
    private Role role;
    private User user;

}
