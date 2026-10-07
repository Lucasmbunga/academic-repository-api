package com.lucas.academicrepository.modules.document.dto;

import com.lucas.academicrepository.modules.document.enums.DocumentType;

public record DocumentResponseDto(
        Long id,
        String title,
        String description,
        Long subjectId,
        String subjectName,
        Long courseId,
        String courseName,
        Long uploaderId,
        String uploaderName,
        DocumentType documentType,
        Integer academicYear,
        String teacherName,
        String fileUrl,
        Float fileSize,
        String fileFormat,
        Boolean isSolved,
        String status,
        String hashSha256,
        String thumbnailUrl,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
) {}