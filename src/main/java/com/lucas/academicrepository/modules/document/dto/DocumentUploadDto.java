package com.lucas.academicrepository.modules.document.dto;

import com.lucas.academicrepository.modules.document.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record DocumentUploadDto(
        @NotBlank(message = "Title is required") String title,
        String description,
        @NotNull(message = "Subject ID is required") Long subjectId,
        @NotNull(message = "Document Type is required") DocumentType documentType,
        Integer academicYear,
        String teacherName,
        Boolean isSolved,
        @NotNull(message = "File is required") MultipartFile file
) {}


