package com.alfredamos.springmanagementofemployees.controllers;

import com.alfredamos.springmanagementofemployees.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerErrorException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleErrors(MethodArgumentNotValidException exception){
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleSecurityException(Exception ex) {
        ProblemDetail errorDetail;

        // TODO send this stack trace to an observability tool
        //exception.printStackTrace()
        System.out.println("In globalExceptionHandler ex : " + ex);

        //System.out.println("Global exception handler" + ex.getMessage());
        if (ex instanceof CredentialsExpiredException){
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        //----> Bad credentials.
        if (ex instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        //----> Access-denied exception
        if (ex instanceof AccessDeniedException) {
            System.out.println("I am in the right place!!!!!!");
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("description", "Invalid credentials");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        //----> Internal-authentication-service-exception.
        if (ex instanceof InternalAuthenticationServiceException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Internal authentication issues with login credentials!");
            errorDetail.setProperty("description", "Invalid credentials");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        //----> Authentication exception
        if (ex instanceof AuthenticationException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("description", "Invalid credentials");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        //----> Illegal-argument-exception.
        if (ex instanceof IllegalArgumentException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Internal authentication issues with login credentials!");
            errorDetail.setProperty("description", "Invalid credentials");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (ex instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (ex instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (ex instanceof BadRequestException){
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
            errorDetail.setProperty("description", "Please provide all the necessary values.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
        }

        if (ex instanceof ForbiddenException){
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("description", "You are not permitted to view, delete and edit this resource.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (ex instanceof NotFoundException){
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
            errorDetail.setProperty("description", "This resource is not available!");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetail);
        }

        if (ex instanceof UnAuthorizedException){
            System.out.println("I am in the right place!!!!!!");
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Problems with invalid credentials and authorization related issues!");
            errorDetail.setProperty("description", "Invalid credentials!");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (ex instanceof PaymentException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        if (ex instanceof JwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        if (ex instanceof ServletException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", "authentication issues with login credentials affecting routing!.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        if (ex instanceof DataIntegrityViolationException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", "duplicate is now allowed!.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        //----> Server error
        if (ex instanceof IOException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", "Input and output error.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        //----> Server error
        if (ex instanceof ServerErrorException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
        }

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
        errorDetail.setProperty("description", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
    }

}

