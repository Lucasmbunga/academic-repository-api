package com.lucas.academicrepository.modules.user.dto;

import com.lucas.academicrepository.modules.user.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegisterDto(@NotBlank @NotNull String name,@NotBlank @NotNull String email, @NotBlank @NotNull String password, Long institutionId, Long facultyId, Long courseId, @NotNull  Role role) {
}
