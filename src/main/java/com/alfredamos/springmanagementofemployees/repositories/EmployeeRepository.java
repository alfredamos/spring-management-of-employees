package com.alfredamos.springmanagementofemployees.repositories;

import com.alfredamos.springmanagementofemployees.entities.Employee;
import com.alfredamos.springmanagementofemployees.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findEmployeeByEmail(String email);
    void deleteById(UUID id);
    void deleteByUser(User user);
}
