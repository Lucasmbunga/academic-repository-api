package com.lucas.academicrepository.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCreateDto(@NotBlank @NotBlank String name, @NotBlank @NotNull String degree,@NotNull int duration,@NotNull Long facultyId) {
}
