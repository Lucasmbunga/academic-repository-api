package com.lucas.academicrepository.config.security;

import com.lucas.academicrepository.exception.BadCredentialsException;
import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.user.dto.UserRegisterDto;
import com.lucas.academicrepository.modules.user.dto.UserResponseDto;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.modules.user.repository.UserRepository;
import com.lucas.academicrepository.modules.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserService userService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        var user = (User) userRepository.findByEmail(loginRequestDto.email());
        if(user == null || !passwordEncoder.matches(loginRequestDto.password(), user.getPassword())){
            throw new BadCredentialsException("Email or password incorrect");
        }

        String token= tokenService.generateToken(user);
        return new TokenResponseDto(token);
    }

    public UserResponseDto register(UserRegisterDto userRegisterDto){
        return userService.register(userRegisterDto);
    }
}
