package com.lucas.academicrepository.config.security;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.user.dto.UserRegisterDto;
import com.lucas.academicrepository.modules.user.dto.UserResponseDto;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.modules.user.repository.UserRepository;
import com.lucas.academicrepository.modules.user.service.UserService;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
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
        if(user == null){
            throw new NotFoundException("Invalid email");
        }
        else if(!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect.");
        }
        String token= tokenService.generateToken(user);
        return new TokenResponseDto(token);
    }

    public ApiResponse<UserResponseDto> register(UserRegisterDto userRegisterDto,String path){
        return userService.register(userRegisterDto,path);
    }
}
