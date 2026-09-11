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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, InstitutionRepository institutionRepository, FacultyRepository facultyRepository, CourseRepository courseRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PagedModel<UserResponseDto> getAll(Pageable pageable) {
        PagedModel<UserResponseDto> users = new PagedModel<>(userRepository.findAll(pageable).map(this::dto));
        return users;
    }

    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return dto(user);
    }

    public PagedModel<UserResponseDto> getByInstitution(Long institutionId,Pageable pageable) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new NotFoundException("Institution not found"));
        PagedModel<UserResponseDto> users = new PagedModel<>(userRepository.findAllByInstitution(institution,pageable).map(this::dto));
        return users;
    }

    public PagedModel<UserResponseDto> getByFaculty(Long facultyId, Pageable pageable) {
        Faculty faculty = facultyRepository.findById(facultyId).orElseThrow(() -> new NotFoundException("Faculty not found"));
        PagedModel<UserResponseDto> users = new PagedModel<>(userRepository.findAllByFaculty(faculty,pageable).map(this::dto));
        return users;
    }

    public PagedModel<UserResponseDto> getByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        PagedModel<UserResponseDto> users = new PagedModel<>(userRepository.findByCourse(course,pageable).map(this::dto));
        return users;
    }

    public UserResponseDto register(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByEmail(userRegisterDto.email())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        User newUser = new User();
        if (!(userRegisterDto.role() == Role.VISITOR)) {
            Institution institution = institutionRepository.findById(userRegisterDto.institutionId()).orElseThrow(() -> new NotFoundException("Institution not found"));
            Faculty faculty = facultyRepository.findById(userRegisterDto.facultyId()).orElseThrow(() -> new NotFoundException("Faculty not found"));
            Course course = courseRepository.findById(userRegisterDto.courseId()).orElseThrow(() -> new NotFoundException("Course not found"));
            newUser.setInstitution(institution);
            newUser.setFaculty(faculty);
            newUser.setCourse(course);
        }

        String encriptedPassword = passwordEncoder.encode(userRegisterDto.password());

        newUser.setName(userRegisterDto.name());
        newUser.setEmail(userRegisterDto.email());
        newUser.setPassword(encriptedPassword);
        newUser.setRole(userRegisterDto.role());

        var userRegistered = userRepository.save(newUser);
        return dto(userRegistered);
    }

    public UserResponseDto update(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.getEmail().equals(userUpdateDto.email()) && userRepository.existsByEmail(userUpdateDto.email())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        if (!(userUpdateDto.role() == Role.VISITOR)) {

            Institution institution = institutionRepository.findById(userUpdateDto.institutionId()).orElseThrow(() -> new NotFoundException("Institution not found"));
            Faculty faculty = facultyRepository.findById(userUpdateDto.facultyId()).orElseThrow(() -> new NotFoundException("Faculty not found"));
            Course course = courseRepository.findById(userUpdateDto.courseId()).orElseThrow(() -> new NotFoundException("Course not found"));

            if(institution!=user.getInstitution() || faculty!=user.getFaculty() || course!=user.getCourse()) {
                verifyIfIsAllowedToUpdateInstitutionData(user.getCreatedAt());
            }
            user.setInstitution(institution);
            user.setFaculty(faculty);
            user.setCourse(course);
        }

        user.setName(userUpdateDto.name());
        user.setEmail(userUpdateDto.email());
        User updatedUser = userRepository.save(user);
        return dto(updatedUser);
    }

    private void verifyIfIsAllowedToUpdateInstitutionData(LocalDateTime createdAt){
        LocalDateTime   now = LocalDateTime.now();
        int monthsPeriod=6;

        //Um estudante só pode atualizar os dados da instituição após 6 meses, desde o seu cadastro.
        if(ChronoUnit.MONTHS.between(createdAt,now)<monthsPeriod){
            throw new IllegalStateException("You can update your Institutions data only after 6 months");
        }
    }

    public UserResponseDto delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
        return dto(user);
    }

    public UserResponseDto dto(User user) {
        String institutionName = null;
        String facultyName = null;
        String courseName = null;

        if (user.getInstitution() != null) {
            institutionName = user.getInstitution().getName();
        }
        if (user.getFaculty() != null) {
            facultyName = user.getFaculty().getName();
        }
        if (user.getCourse() != null) {
            courseName = user.getCourse().getName();
        }

        return new UserResponseDto(user.getName(), user.getEmail(), institutionName, facultyName, courseName, user.getRole());
    }
}
