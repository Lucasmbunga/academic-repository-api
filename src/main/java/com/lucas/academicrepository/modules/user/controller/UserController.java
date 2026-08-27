package com.lucas.academicrepository.modules.user.controller;

import com.lucas.academicrepository.modules.user.dto.UserResponseDto;
import com.lucas.academicrepository.modules.user.dto.UserUpdateDto;
import com.lucas.academicrepository.modules.user.service.UserService;
import com.lucas.academicrepository.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll(@PageableDefault(sort="name") Pageable pageable){
        return ResponseEntity.ok(userService.getAll(pageable,"/api/v1/users"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id,"/api/v1/users/"+id));
    }
    @GetMapping("/institutions/{id}")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getByInstitution(@PathVariable Long id){
        return ResponseEntity.ok(userService.getByInstitution(id,"/api/v1/users/institutions/"+id));
    }

    @GetMapping("/faculties/{id}")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getByFaculty(@PathVariable Long id){
        return ResponseEntity.ok(userService.getByFaculty(id,"/api/v1/users/faculties/"+id));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getByCourse(@PathVariable Long id){
        return ResponseEntity.ok(userService.getByCourse(id,"/api/v1/users/courses/"+id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(@PathVariable Long id,@Valid @RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok(userService.update(id,userUpdateDto,"/api/v1/users/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(userService.delete(id,"/api/v1/users/"+id));
    }
}
