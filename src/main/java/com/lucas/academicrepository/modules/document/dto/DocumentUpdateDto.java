package com.lucas.academicrepository.modules.document.dto;

import jakarta.validation.constraints.NotNull;

public record DocumentUpdateDto(
        @NotNull String title,
        @NotNull String description,
        String teacherName,
        Integer academicYear,
        Boolean isSolved
) {
}
