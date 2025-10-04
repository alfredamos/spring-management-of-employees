package com.alfredamos.springmanagementofemployees.controllers;

import com.alfredamos.springmanagementofemployees.dto.EmployeeDto;
import com.alfredamos.springmanagementofemployees.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/employees")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editEmployee(@PathVariable(value = "id") UUID id, @Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.ok(employeeService.editEmployeeById(id, employeeDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }
}
