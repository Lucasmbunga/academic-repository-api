package com.lucas.academicrepository.exception;

import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request){
        ApiResponse<Void> response= ResponseUtil.error(List.of(e.getMessage()),1000,"An error occurred processing your request.",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException e, HttpServletRequest request){
        ApiResponse<Void> response= ResponseUtil.error(List.of(e.getMessage()),404,"Resource not found",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAcessDaniedException(AuthenticationException e, HttpServletRequest request){
        ApiResponse<Void> response=ResponseUtil.error(List.of(e.getMessage()),401,"Acess danied. User not authenticated.",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(AccessDeniedException e, HttpServletRequest request){
        ApiResponse<Void> response=ResponseUtil.error(List.of(e.getMessage()),403,"Acess danied. User not authorized.",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityVioletionException(DataIntegrityViolationException e, HttpServletRequest request){
        ApiResponse<Void> response=ResponseUtil.error(List.of(e.getMessage()),1003,"Data Integrity Violation",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException e,HttpServletRequest request){
        ApiResponse<Void> response=ResponseUtil.error(List.of(e.getMessage()),1002,"Validation Failed",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        ApiResponse<Void> response=ResponseUtil.error(List.of(e.getMessage()),400,"Method Argument Not Valid",request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
