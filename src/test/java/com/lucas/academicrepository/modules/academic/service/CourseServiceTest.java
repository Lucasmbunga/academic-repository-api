package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.CourseCreateDto;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FacultyRepository facultyRepository;



    @InjectMocks
    private CourseService courseService;

    private Institution institution;
    private Faculty faculty;
    private Course course;

    @BeforeEach
    void setUp() {

        institution = new Institution();
        institution.setId(1L);
        institution.setName("Kimpa Vita");
        institution.setAcronym("UNIKIVI");
        institution.setType("PUBLIC");

        faculty = new Faculty();
        faculty.setId(10L);
        faculty.setName("Faculdade de Engenharia");
        faculty.setAcronym("IP");
        faculty.setInstitution(institution);

        course = new Course();
        course.setId(100L);
        course.setName("Engenharia Informática");
        course.setDegree("Licenciatura");
        course.setDuration(5);
        course.setFaculty(faculty);
    }

    @Test
    @DisplayName("Deve criar um curso associado a uma faculdade existente")
    void shouldCreateCourseSuccessfully() {
        CourseCreateDto dto = new CourseCreateDto("Engenharia Informática","Licenciatura",5, 10L);

        when(facultyRepository.findById(10L)).thenReturn(Optional.of(faculty));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        CourseResponseDto response = courseService.create(dto);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Engenharia Informática", response.name());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando a faculdade informada não existir")
    void shouldThrowExceptionWhenFacultyNotFoundOnCourseCreation() {
        CourseCreateDto dto = new CourseCreateDto("Engenharia Informática","Licenciatura",5, 999L);

        when(facultyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courseService.create(dto));
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar cursos por faculdade")
    void shouldListCoursesByFaculty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> page = new PageImpl<>(List.of(course));

        when(facultyRepository.findById(10L)).thenReturn(Optional.of(faculty));
        when(courseRepository.findByFaculty(faculty, pageable)).thenReturn(page);

        PagedModel<CourseResponseDto> result = courseService.getByFaculty(10L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getMetadata().totalElements());
        assertEquals("Engenharia Informática", result.getContent().getFirst().name());
        verify(courseRepository, times(1)).findByFaculty(faculty, pageable);
    }
}