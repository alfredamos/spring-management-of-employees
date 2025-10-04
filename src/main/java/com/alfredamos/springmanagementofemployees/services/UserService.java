package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.dto.UserDto;
import com.alfredamos.springmanagementofemployees.entities.User;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;
import com.alfredamos.springmanagementofemployees.mapper.UserMapper;
import com.alfredamos.springmanagementofemployees.repositories.EmployeeRepository;
import com.alfredamos.springmanagementofemployees.repositories.UserRepository;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import com.alfredamos.springmanagementofemployees.utils.SameUserAndAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SameUserAndAdmin sameUserAndAdmin;
    private final EmployeeRepository employeeRepository;

    ////----> Get one user by email method.
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    ////----> Delete one user by id method.
    @Transactional
    public ResponseMessage deleteUserById(UUID id) {
        //----> Only admin can delete user.
        sameUserAndAdmin.checkForAdmin();

        //----> Check for existence of user.
        var user = fetchUser(id);

        //----> Delete the employee with the given user.
        employeeRepository.deleteByUser(user);

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

    ////----> Edit user method.
    public User editUserById(UUID id, UserDto userDto) {
        //----> Fetch the user with this id.
        var user = fetchUser(id);

        //----> Update user fields.
        var updatedUserFields = updateUserFields(user, userDto);

        //----> Edit user with the given id and send back response.
        return userRepository.save(updatedUserFields);
    }

    ////----> Fetch user by id and validate the existence of user.
    public User fetchUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found in the database!"));
    }

    ////----> Set user update.
    private User updateUserFields(User user, UserDto userDto) {
        //----> Mapped userDto to user.
        var mappedUser = userMapper.toEntity(userDto);

        //----> Update user field
        mappedUser.setId(user.getId());
        mappedUser.setPassword(user.getPassword());
        mappedUser.setRole(user.getRole());
        mappedUser.setEmail(user.getEmail());

        //----> Set back response.
        return mappedUser;
    }


}
