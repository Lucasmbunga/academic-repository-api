package com.lucas.academicrepository.modules.academic.dto;

import com.lucas.academicrepository.modules.academic.repository.SubjectRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectCreateDto(@NotNull @NotBlank String name,Integer academicYear,Integer semester,@NotNull Long courseId) {
}
