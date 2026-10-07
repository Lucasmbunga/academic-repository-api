package com.lucas.academicrepository.modules.document.dto;

import com.lucas.academicrepository.modules.document.enums.DocumentStatus;
import jakarta.validation.constraints.NotNull;

public record DocumentStatusUpdateDto(@NotNull DocumentStatus documentStatus) {
}
