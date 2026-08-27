package com.lucas.academicrepository.modules.academic.dto;

import jakarta.validation.constraints.NotNull;

public record SubjectUpdateDto(@NotNull String name, @NotNull Integer academicYear,Integer semester) {
}
