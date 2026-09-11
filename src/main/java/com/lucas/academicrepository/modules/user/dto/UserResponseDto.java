package com.lucas.academicrepository.modules.user.dto;

import com.lucas.academicrepository.modules.user.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserResponseDto(@NotBlank @NotNull String name, @NotBlank @NotNull String email, String institutionName,  String facultyName,  String courseName, @NotNull Role role) {
}
