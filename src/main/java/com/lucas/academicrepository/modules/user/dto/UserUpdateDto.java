package com.lucas.academicrepository.modules.user.dto;

import com.lucas.academicrepository.modules.user.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(@NotNull @NotBlank String name,@NotNull @NotBlank @Email String email,@NotNull Long institutionId,@NotNull  Long facultyId,@NotNull Long courseId,@NotNull Role role) {
}
