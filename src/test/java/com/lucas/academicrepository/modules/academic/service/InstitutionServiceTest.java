package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.InstitutionCreateDto;
import com.lucas.academicrepository.modules.academic.dto.InstitutionResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Institution;
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
class InstitutionServiceTest {

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private InstitutionService institutionService;

    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = new Institution();
        institution.setId(1L);
        institution.setName("Universidade Kimpa Vita");
        institution.setAcronym("UNIKIVI");
    }

    @Test
    @DisplayName("Deve criar uma nova instituição com sucesso")
    void shouldCreateInstitutionSuccessfully() {
        InstitutionCreateDto dto = new InstitutionCreateDto("Universidade Kimpa Vita", "UNIKIVI","PUBLIC");

        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocation -> {
            Institution saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        InstitutionResponseDto response = institutionService.create(dto);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Universidade Kimpa Vita", response.name());
        assertEquals("UNIKIVI", response.acronym());
        verify(institutionRepository, times(1)).save(any(Institution.class));
    }

    @Test
    @DisplayName("Deve buscar instituição por ID com sucesso")
    void shouldGetInstitutionByIdSuccessfully() {
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));

        InstitutionResponseDto response = institutionService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("UNIKIVI", response.acronym());
        verify(institutionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar ID inexistente")
    void shouldThrowNotFoundExceptionWhenInstitutionDoesNotExist() {
        when(institutionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> institutionService.getById(99L));
        verify(institutionRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todas as instituições paginadas")
    void shouldListInstitutionsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Institution> page = new PageImpl<>(List.of(institution));

        when(institutionRepository.findAll(pageable)).thenReturn(page);

        PagedModel<InstitutionResponseDto> result = institutionService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getMetadata().totalElements());
        assertEquals("UNIKIVI", result.getContent().getFirst().acronym());
        verify(institutionRepository, times(1)).findAll(pageable);
    }
}
