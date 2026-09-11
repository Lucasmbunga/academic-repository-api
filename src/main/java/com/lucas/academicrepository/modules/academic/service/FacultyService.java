package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.FacultyCreateDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyResponseDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final InstitutionRepository institutionRepository;

    public FacultyService(FacultyRepository facultyRepository, InstitutionRepository institutionRepository) {
        this.facultyRepository = facultyRepository;
        this.institutionRepository = institutionRepository;
    }

    public PagedModel<FacultyResponseDto> getAll(Pageable pageable) {
        PagedModel<FacultyResponseDto> faculties = new PagedModel<>(facultyRepository.findAll(pageable).map(this::toDto));
        return faculties;
    }

    public FacultyResponseDto getById(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        return toDto(faculty);
    }

    public PagedModel<FacultyResponseDto> getByInstitution(Long id,Pageable pageable) {

        Institution institution = institutionRepository.findById(id).orElseThrow(() -> new NotFoundException("Institution not found"));
        PagedModel<FacultyResponseDto> faculties = new PagedModel<>(facultyRepository.findByInstitution(institution,pageable)
                .map(this::toDto));

        return faculties;
    }

    public FacultyResponseDto create(FacultyCreateDto facultyCreateDto) {
        Institution institution = institutionRepository.findById(facultyCreateDto.institutionId()).orElseThrow(() -> new NotFoundException("Institution not found"));
        Faculty newFaculty = new Faculty();
        newFaculty.setName(facultyCreateDto.name());
        newFaculty.setAcronym(facultyCreateDto.acronym());
        newFaculty.setInstitution(institution);

        Faculty createdFaculty = facultyRepository.save(newFaculty);

        return toDto(createdFaculty);
    }

    public FacultyResponseDto update(Long id, FacultyUpdateDto facultyUpdateDto) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        faculty.setName(facultyUpdateDto.name());
        faculty.setAcronym(facultyUpdateDto.acronym());

        Faculty updatedFaculty = facultyRepository.save(faculty);
        return toDto(updatedFaculty);
    }

    public FacultyResponseDto delete(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        facultyRepository.delete(faculty);
        return toDto(faculty);
    }

    private FacultyResponseDto toDto(Faculty faculty) {
        return new FacultyResponseDto(faculty.getId(), faculty.getName(), faculty.getAcronym(), faculty.getInstitution().getName());
    }
}
