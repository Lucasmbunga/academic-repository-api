package com.lucas.academicrepository.config.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(@NotNull @NotBlank String email, @NotNull @NotBlank String password) {
}
