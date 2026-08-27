package com.lucas.academicrepository.config.security;

import jakarta.validation.constraints.NotNull;

public record TokenResponseDto(@NotNull @NotNull String token) {
}
