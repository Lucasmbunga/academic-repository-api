package com.lucas.academicrepository.config.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(@NotNull @NotBlank @Email(message = "Invalid email. Introduce an email like youname@gmail.com") String email, @NotNull @NotBlank String password) {
}
