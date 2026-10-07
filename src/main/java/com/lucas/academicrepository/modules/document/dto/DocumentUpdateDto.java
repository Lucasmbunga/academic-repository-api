package com.lucas.academicrepository.modules.document.dto;

public record DocumentUpdateDto(
        String title,
        String description,
        String teacherName,
        Integer academicYear,
        Boolean isSolved
) {}
