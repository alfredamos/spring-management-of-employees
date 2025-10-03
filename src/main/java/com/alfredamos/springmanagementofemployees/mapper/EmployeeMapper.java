package com.alfredamos.springmanagementofemployees.mapper;

import com.alfredamos.springmanagementofemployees.dto.EmployeeDto;
import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.entities.User;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Data
@Builder
@Service
public class EmployeeMapper {
   ////----> EmployeeDto -> Employee for updating of employee.
   public Employee toEmployeeEntity(EmployeeDto employeeDto, User user) {
       return Employee.builder()
               .id(employeeDto.getId())
               .age(LocalDate.now().getYear() - employeeDto.getDateOfBirth().getYear())
               .dateOfBirth(employeeDto.getDateOfBirth())
               .image(employeeDto.getImage())
               .name(employeeDto.getName())
               .phone(employeeDto.getPhone())
               .email(employeeDto.getEmail())
               .user(user)
               .gender(employeeDto.getGender())
               .address(employeeDto.getAddress())
               .build();
   }

    ////----> Employee -> EmployeeDto for presentation.
   public EmployeeDto toEmployeeDto(Employee employee){
      return EmployeeDto.builder()
              .id(employee.getId())
              .age(employee.getAge())
              .dateOfBirth(employee.getDateOfBirth())
              .image(employee.getImage())
              .name(employee.getName())
              .phone(employee.getPhone())
              .email(employee.getEmail())
              .gender(employee.getGender())
              .address(employee.getAddress())
              .userId(employee.getUser().getId())
              .build();
   }
}
