package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.modules.academic.dto.SubjectResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.entity.Subject;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @InjectMocks
    private SubjectService subjectService;

    private Institution institution;
    private Faculty faculty;
    private Course course;
    private Subject subject;

    @BeforeEach
    public void setUp()
    {
        institution = new Institution();
        institution.setId(1L);
        institution.setName("Kimpa Vita");
        institution.setAcronym("UNIKIVI");
        institution.setType("PUBLIC");


        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Institutu Politécnico");
        faculty.setAcronym("IP");
        faculty.setInstitution(institution);

        course = new Course();
        course.setId(1L);
        course.setName("Engenharia Informática");
        course.setDegree("Licenciatura");
        course.setDuration(5);
        course.setFaculty(faculty);

        subject = new Subject();
        subject.setId(1L);
        subject.setAcademicYear(2);
        subject.setSemester(1);
        subject.setName("Redes I");
        subject.setCourse(course);
    }

    @Test
    @DisplayName("Deve retornar todos os cursos do Instituto Politécnico")
     void shouldReturnListOfSubjects(){

        Pageable pageable= PageRequest.of(0, 1);
        Page<Subject> page=new PageImpl<>(List.of(subject),pageable,1);

        when(subjectRepository.findAll(pageable)).thenReturn(page);

        PagedModel<SubjectResponseDto> response=subjectService.getAll(pageable);

        assertAll(
                ()->assertEquals("Redes I",response.getContent().getFirst().name()),
                ()->assertEquals("Engenharia Informática",response.getContent().getLast().courseName())
        );

    }
}
