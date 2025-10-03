package com.alfredamos.springmanagementofemployees.services;

import com.alfredamos.springmanagementofemployees.dto.EmployeeDto;
import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.exceptions.NotFoundException;
import com.alfredamos.springmanagementofemployees.mapper.EmployeeMapper;
import com.alfredamos.springmanagementofemployees.repositories.EmployeeRepository;
import com.alfredamos.springmanagementofemployees.repositories.UserRepository;
import com.alfredamos.springmanagementofemployees.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    ////----> Delete employee by id method.
    public ResponseMessage deleteEmployeeById(UUID id) {
        //----> Fetch the employee with the given id.
        getOneEmployerById(id);

        //----> Delete the employee with the given id.
        employeeRepository.deleteById(id);

        //----> Send back response.
        return new ResponseMessage("Employee has been deleted successfully!", "success", HttpStatus.OK);
    }

    ////----> Edit employee with the given id method.
    public EmployeeDto editEmployeeById(UUID id, EmployeeDto employeeDto) {
        //----> Fetch employee with the given id.
        var employee = getOneEmployerById(id);

        //----> Get the user associated with this employee.
        var user = userRepository.findById(employee.getUser().getId()).orElseThrow(() -> new NotFoundException("User not found in the database!"));

        //----> Update employee with the given id.
        employeeRepository.save(employeeMapper.toEmployeeEntity(employeeDto, user));

        //----> Send back response.
        return employeeDto;
    }

    ////----> Get one employee by id method.
    public EmployeeDto getEmployeeById(UUID id) {
        //----> Fetch employee by id.
        return employeeMapper.toEmployeeDto(getOneEmployerById(id));
    }

    ////----> Get all employees' method.
    public List<EmployeeDto> getAllEmployees() {
        //----> Fetch all employees from database.
        var employees = employeeRepository.findAll();

        //----> Check for empty list of employees.
        if (employees.isEmpty()) {
            throw new NotFoundException("Employees not found in the database!");
        }

        return employees.stream().map(employee -> employeeMapper.toEmployeeDto(employee)).toList();
    }

    ////----> Get one employee by id method.
    private Employee getOneEmployerById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with the given id is not found in the database!"));
    }


}
