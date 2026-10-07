package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.FacultyCreateDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
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
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private FacultyService facultyService;

    private Institution institution;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        institution = new Institution();
        institution.setId(1L);
        institution.setName("Universidade Kimpa Vita");

        faculty = new Faculty();
        faculty.setId(10L);
        faculty.setName("Faculdade de Engenharia");
        faculty.setInstitution(institution);
    }

    @Test
    @DisplayName("Deve criar uma faculdade vinculada a uma instituição válida")
    void shouldCreateFacultySuccessfully() {
        FacultyCreateDto dto = new FacultyCreateDto("Faculdade de Engenharia", "IP",1L);

        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));
        when(facultyRepository.save(any(Faculty.class))).thenAnswer(invocation -> {
            Faculty saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        FacultyResponseDto response = facultyService.create(dto);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Faculdade de Engenharia", response.name());
        verify(institutionRepository, times(1)).findById(1L);
        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar criar faculdade para instituição inexistente")
    void shouldThrowExceptionWhenInstitutionNotFoundOnFacultyCreation() {
        FacultyCreateDto dto = new FacultyCreateDto("Faculdade Inexistente", "PUBLIC",99L);

        when(institutionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.create(dto));
        verify(facultyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar faculdades filtradas por instituição")
    void shouldListFacultiesByInstitution() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Faculty> page = new PageImpl<>(List.of(faculty));

        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));
        when(facultyRepository.findByInstitution(institution, pageable)).thenReturn(page);

        PagedModel<FacultyResponseDto> result = facultyService.getByInstitution(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getMetadata().totalElements());
        assertEquals("Faculdade de Engenharia", result.getContent().getFirst().name());
        verify(facultyRepository, times(1)).findByInstitution(institution, pageable);
    }
}