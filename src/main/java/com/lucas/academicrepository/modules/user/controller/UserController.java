package com.lucas.academicrepository.modules.user.controller;

import com.lucas.academicrepository.modules.user.dto.UserResponseDto;
import com.lucas.academicrepository.modules.user.dto.UserUpdateDto;
import com.lucas.academicrepository.modules.user.service.UserService;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<UserResponseDto>>> getAll(@PageableDefault(sort="name") Pageable pageable){
        var response= ResponseUtil.sucess(userService.getAll(pageable),"Success","/api/v1/users");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Long id){
        var response=ResponseUtil.sucess(userService.getById(id),"Success","/api/v1/users/"+id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/institutions/{id}")
    public ResponseEntity<ApiResponse<PagedModel<UserResponseDto>>> getByInstitution(@PathVariable Long id,@PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(userService.getByInstitution(id, pageable),"Success","/api/v1/users/"+id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/faculties/{id}")
    public ResponseEntity<ApiResponse<PagedModel<UserResponseDto>>> getByFaculty(@PathVariable Long id,@PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(userService.getByFaculty(id, pageable),"Success","/api/v1/users");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<PagedModel<UserResponseDto>>> getByCourse(@PathVariable Long id,@PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(userService.getByCourse(id, pageable),"Success","/api/v1/users");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(@PathVariable Long id,@Valid @RequestBody UserUpdateDto userUpdateDto){
        var response=ResponseUtil.sucess(userService.update(id, userUpdateDto),"User updated successfully","/api/v1/users/"+id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteById(@PathVariable Long id){
        var response=ResponseUtil.sucess(userService.delete(id),"User deleted successfully","/api/v1/users/"+id);
        return ResponseEntity.ok(response);
    }
}
