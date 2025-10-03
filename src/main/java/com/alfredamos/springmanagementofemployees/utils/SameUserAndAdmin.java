package com.alfredamos.springmanagementofemployees.utils;

import com.alfredamos.springmanagementofemployees.entities.Role;
import com.alfredamos.springmanagementofemployees.entities.User;
import com.alfredamos.springmanagementofemployees.exceptions.ForbiddenException;
import com.alfredamos.springmanagementofemployees.mapper.UserMapper;
import com.alfredamos.springmanagementofemployees.repositories.AuthRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;

import java.util.UUID;

@Service
@Data
@AllArgsConstructor
public class SameUserAndAdmin {
    private final AuthRepository authRepository;
    private final UserMapper userMapper;

    public void checkForOwnerShipOrAdmin(UUID userId){
        var currentUserInfo = currentUserInfo(); //----> Current-user info.

        //----> Compare the giving user-id with the current-user id.
        var isSameUser = userId.equals(currentUserInfo.getUser().getId());
        var isAdmin = Role.Admin.equals(currentUserInfo.getRole());

        //----> Check for not same-user and not admin
        if (!isSameUser && !isAdmin){
            throw new ForbiddenException("You are not permitted to view or perform this operation");
        }

    }

    public void checkForAdmin(){
        var currentUserInfo = currentUserInfo(); //----> Current-user info.

        //----> Check for admin privilege.
        var isAdmin = Role.Admin.equals(currentUserInfo.getRole());

        //----> Is not admin.
        if(!isAdmin){
            throw new ForbiddenException("You are not permitted to view or perform this operation");
        }

    }

    private UserAndAdmin currentUserInfo(){
        var authUser = getUserFromContext(); //----> Current user.

        if  (authUser == null) {
            throw new NotFoundException("User not found!");
        }

        return new UserAndAdmin(authUser.getRole(), authUser);
    }

    public User getUserFromContext(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        var email = (String) authentication.getPrincipal();
        var user = authRepository.findUserByEmail(email);

        if (user == null){
            throw  new NotFoundException("Current user is not found!");
        }

        return user;
    }

}
