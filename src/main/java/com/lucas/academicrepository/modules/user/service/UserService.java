package com.lucas.academicrepository.modules.user.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import com.lucas.academicrepository.modules.user.dto.UserRegisterDto;
import com.lucas.academicrepository.modules.user.dto.UserResponseDto;
import com.lucas.academicrepository.modules.user.dto.UserUpdateDto;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.modules.user.enums.Role;
import com.lucas.academicrepository.modules.user.repository.UserRepository;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    public UserService(UserRepository userRepository,InstitutionRepository institutionRepository,FacultyRepository facultyRepository,CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
    }

    public ApiResponse<List<UserResponseDto>> getAll(Pageable pageable,String path){
        List<UserResponseDto> users = userRepository.findAll(pageable).getContent().stream().map(this::dto).toList();
        return ResponseUtil.sucess(users,"Sucess",path);
    }

    public ApiResponse<UserResponseDto> getById(Long id,String path){
        User user=userRepository.findById(id).orElseThrow(()->new NotFoundException("User not found"));
        return ResponseUtil.sucess(dto(user),"Sucess",path);
    }

    public ApiResponse<List<UserResponseDto>> getByInstitution(Long institutionId,String path){
        Institution institution=institutionRepository.findById(institutionId).orElseThrow(()->new NotFoundException("Institution not found"));
        List<UserResponseDto> users=userRepository.findAllByInstitution(institution).stream().map(this::dto).toList();
        return ResponseUtil.sucess(users,"Sucess",path);
    }

    public ApiResponse<List<UserResponseDto>> getByFaculty(Long facultyId,String path){
        Faculty faculty=facultyRepository.findById(facultyId).orElseThrow(()->new NotFoundException("Faculty not found"));
        List<UserResponseDto>    users=userRepository.findAllByFaculty(faculty).stream().map(this::dto).toList();
        return ResponseUtil.sucess(users,"Sucess",path);
    }
    public ApiResponse<List<UserResponseDto>> getByCourse(Long courseId,String path){
        Course course=courseRepository.findById(courseId).orElseThrow(()->new NotFoundException("Course not found"));
        List<UserResponseDto> users=userRepository.findByCourse(course).stream().map(this::dto).toList();
        return ResponseUtil.sucess(users,"Sucess",path);
    }

    public ApiResponse<UserResponseDto> register( UserRegisterDto userRegisterDto,String path) {
        if(userRepository.existsByEmail(userRegisterDto.email())){
            throw new DataIntegrityViolationException("Email already exists");
        }

        User newUser = new User();
        if(!(userRegisterDto.role() == Role.VISITOR)){
            Institution institution=institutionRepository.findById(userRegisterDto.institutionId()).orElseThrow(()->new NotFoundException("Institution not found"));
            Faculty faculty=facultyRepository.findById(userRegisterDto.facultyId()).orElseThrow(()->new NotFoundException("Faculty not found"));
            Course course=courseRepository.findById(userRegisterDto.courseId()).orElseThrow(()->new NotFoundException("Course not found"));
            newUser.setInstitution(institution);
            newUser.setFaculty(faculty);
            newUser.setCourse(course);
        }

        String encriptedPassword=new BCryptPasswordEncoder().encode(userRegisterDto.password());

        newUser.setName( userRegisterDto.name());
        newUser.setEmail(userRegisterDto.email());
        newUser.setPassword(encriptedPassword);
        newUser.setRole(userRegisterDto.role());

        var userRegistered=userRepository.save(newUser);
        return ResponseUtil.sucess(dto(userRegistered),"User registered sucessfully",path);
    }

    public ApiResponse<UserResponseDto> update(Long id,UserUpdateDto userUpdateDto, String path) {
        User user=userRepository.findById(id).orElseThrow(()->new NotFoundException("User not found"));
        if(!user.getEmail().equals(userUpdateDto.email()) && userRepository.existsByEmail(userUpdateDto.email())){
            throw new DataIntegrityViolationException("Email already exists");
        }

        if(!(userUpdateDto.role() == Role.VISITOR)){
            Institution institution=institutionRepository.findById(userUpdateDto.institutionId()).orElseThrow(()->new NotFoundException("Institution not found"));
            Faculty faculty=facultyRepository.findById(userUpdateDto.facultyId()).orElseThrow(()->new NotFoundException("Faculty not found"));
            Course course=courseRepository.findById(userUpdateDto.courseId()).orElseThrow(()->new NotFoundException("Course not found"));
            user.setInstitution(institution);
            user.setFaculty(faculty);
            user.setCourse(course);
        }

        user.setName( userUpdateDto.name());
        user.setEmail(userUpdateDto.email());
        User updatedUser=userRepository.save(user);
        return ResponseUtil.sucess(dto(updatedUser),"User updated successfully",path);
    }

    public ApiResponse<UserResponseDto> delete(Long id,String path){
        User user=userRepository.findById(id).orElseThrow(()->new NotFoundException("User not found"));
        userRepository.delete(user);
        return ResponseUtil.sucess(dto(user),"User deleted successfully",path);
    }
    public UserResponseDto dto(User user){
        return new UserResponseDto(user.getName(),user.getEmail(),user.getPassword(),user.getInstitution().getName(),user.getFaculty().getName(),user.getCourse().getName(),user.getRole());
    }
}
