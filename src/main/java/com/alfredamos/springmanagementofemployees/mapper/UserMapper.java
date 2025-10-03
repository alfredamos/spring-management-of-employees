package com.alfredamos.springmanagementofemployees.mapper;

import com.alfredamos.springmanagementofemployees.dto.UserDto;
import com.alfredamos.springmanagementofemployees.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDTO(User user);

    List<UserDto> toDTOList(List<User> users);
}
