package com.lucas.academicrepository.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FacultyCreateDto(@NotNull @NotBlank String name,String acronym,@NotNull @Positive Long institutionId) {
}
