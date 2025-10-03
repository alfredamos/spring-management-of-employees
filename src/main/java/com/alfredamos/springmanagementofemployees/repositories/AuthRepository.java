package com.alfredamos.springmanagementofemployees.repositories;

import com.alfredamos.springmanagementofemployees.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {
    User findUserByEmail(String email);
}
