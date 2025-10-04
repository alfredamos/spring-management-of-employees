package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.dto.EmployeeDto;
import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;
import com.alfredamos.springmanagementofemployees.mapper.EmployeeMapper;
import com.alfredamos.springmanagementofemployees.mapper.UserMapper;
import com.alfredamos.springmanagementofemployees.repositories.EmployeeRepository;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import com.alfredamos.springmanagementofemployees.utils.SameUserAndAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final SameUserAndAdmin sameUserAndAdmin;
    private final UserService userService;
    private final UserMapper userMapper;

    ////----> Delete employee by id method.
    public ResponseMessage deleteEmployeeById(UUID id) {
        //----> Fetch the employee with the given id.
        var employee = getOneEmployerById(id);

        //----> Only admin can delete an employee.
        sameUserAndAdmin.checkForAdmin();

        //----> Delete the user and associated employee.
        userService.deleteUserById(employee.getUser().getId());

        //----> Send back response.
        return new ResponseMessage("Employee has been deleted successfully!", "success", HttpStatus.OK);
    }

    ////----> Edit employee with the given id method.
    public EmployeeDto editEmployeeById(UUID id, EmployeeDto employeeDto) {
        //----> Fetch employee with the given id.
        var employee = getOneEmployerById(id);

        //----> Only owner or admin can edit employee data.
        sameUserAndAdmin.checkForOwnerShipOrAdmin(employee.getUser().getId());

        //----> Get the user associated with this employee.
        var user = userService.fetchUser(employee.getUser().getId());

        //----> Get the year of birth of the given employee.
        var year = employeeDto.getDateOfBirth().getYear();

        //----> Update employee fields.
        employeeDto.setEmail(employee.getUser().getEmail());
        employeeDto.setId(employee.getId());
        employeeDto.setAge(LocalDate.now().getYear() -   (year != 0 ? year : employee.getDateOfBirth().getYear()));

        //----> Save the edited employee data in the employee database.
        var editedEmployee = employeeRepository.save(employeeMapper.toEmployeeEntity(employeeDto, user));

        //----> Edit all related fields in user.
        user.setName(editedEmployee.getName());
        user.setImage(editedEmployee.getImage());

        //----> Save the edited user data in the user database.
        userService.editUserById(editedEmployee.getUser().getId(), userMapper.toDTO(user));

        //----> Send back response.
        return employeeDto;
    }

    ////----> Get one employee by id method.
    public EmployeeDto getEmployeeById(UUID id) {
        //----> Fetch employee by id.
        var employee = employeeMapper.toEmployeeDto(getOneEmployerById(id));

        //----> Only owner and admin can retrieve employee detail.
        sameUserAndAdmin.checkForOwnerShipOrAdmin(employee.getUserId());

        //----> Send back the response.
        return employee;
    }

    ////----> Get all employees' method.
    public List<EmployeeDto> getAllEmployees() {
        //----> Only admin can retrieve all employees.
        sameUserAndAdmin.checkForAdmin();

        //----> Fetch all employees from database.
        var employees = employeeRepository.findAll();

        //----> Check for empty list of employees.
        if (employees.isEmpty()) {
            throw new NotFoundException("Employees not found in the database!");
        }

        //----> Send back response.
        return employees.stream().map(employeeMapper::toEmployeeDto).toList();
    }

    ////----> Get one employee by id method.
    private Employee getOneEmployerById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with the given id is not found in the database!"));
    }


}
