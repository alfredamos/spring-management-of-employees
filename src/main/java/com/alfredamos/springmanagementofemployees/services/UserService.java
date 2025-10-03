package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.dto.UserDto;
import com.alfredamos.springmanagementofemployees.entities.User;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;
import com.alfredamos.springmanagementofemployees.mapper.UserMapper;
import com.alfredamos.springmanagementofemployees.repositories.UserRepository;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import com.alfredamos.springmanagementofemployees.utils.SameUserAndAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SameUserAndAdmin sameUserAndAdmin;

    ////----> Get one user by email method.
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    ////----> Delete one user by id method.
    public ResponseMessage deleteUserById(UUID id) {
        //----> Check for existence of user.
        var user = fetchUser(id);

        //----> Check for same user or admin privilege.
        sameUserAndAdmin.checkForOwnerShipOrAdmin(user.getId());

        //----> Delete the user with the given id from database.
        userRepository.deleteById(id);

        //----> Send back the response.
        return new ResponseMessage("User has been deleted successfully!", "success", HttpStatus.OK);
    }

    ////----> Get one user by id method.
    public UserDto getUserById(UUID id) {
        //----> Check for existence of user.
        var user = fetchUser(id);

        //----> Check for same user or admin privilege.
        sameUserAndAdmin.checkForOwnerShipOrAdmin(user.getId());

        //----> Send back the response.
        return userMapper.toDTO(user);
    }

    ////----> Get all users.
    public List<UserDto> getAllUsers() {
        //----> Only admin can perform this action
        sameUserAndAdmin.checkForAdmin();

        //----> Get all users from the database.
        var users = userRepository.findAll();
        return users.stream().map(userMapper::toDTO).toList();


    }

    ////----> Fetch user by id and validate the existence of user.
    private User fetchUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found in the database!"));
    }
}
