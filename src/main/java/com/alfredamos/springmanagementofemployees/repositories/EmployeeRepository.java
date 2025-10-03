package com.alfredamos.springmanagementofemployees.repositories;

import com.alfredamos.springmanagementofemployees.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findEmployeeByEmail(String email);

    @Override
    void deleteById(UUID uuid);
}
