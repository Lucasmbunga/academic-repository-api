package com.lucas.academicrepository.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FacultyUpdateDto(@NotNull @NotBlank String name,@NotNull @NotBlank String acronym) {
}
