package com.lucas.academicrepository.modules.academic.dto;

import java.time.LocalDateTime;

public record InstitutionResponseDto(Long id, String name, String acronym, String type, LocalDateTime createdAt) {
}
