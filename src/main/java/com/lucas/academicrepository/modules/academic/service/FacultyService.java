package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.FacultyCreateDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyResponseDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final InstitutionRepository institutionRepository;

    public FacultyService(FacultyRepository facultyRepository, InstitutionRepository institutionRepository) {
        this.facultyRepository = facultyRepository;
        this.institutionRepository = institutionRepository;
    }

    public ApiResponse<List<FacultyResponseDto>> getAll(Pageable pageable, String path) {
        List<FacultyResponseDto> faculties = facultyRepository.findAll(pageable).getContent().stream().map(this::toDto).toList();
        return ResponseUtil.sucess(faculties, "Sucess", path);
    }

    public ApiResponse<FacultyResponseDto> getById(Long id, String path) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        return ResponseUtil.sucess(toDto(faculty), "Sucess", path);
    }

    public ApiResponse<List<FacultyResponseDto>> getByInstitution(Long id, String path) {

        Institution institution = institutionRepository.findById(id).orElseThrow(() -> new NotFoundException("Institution not found"));
        List<FacultyResponseDto> faculties = facultyRepository.findByInstitution(institution).stream()
                .map(this::toDto).toList();

        return ResponseUtil.sucess(faculties, "Sucess", path);
    }

    public ApiResponse<FacultyResponseDto> create(FacultyCreateDto facultyCreateDto, String path) {
        Institution institution = institutionRepository.findById(facultyCreateDto.institutionId()).orElseThrow(() -> new NotFoundException("Institution not found"));
        Faculty newFaculty = new Faculty();
        newFaculty.setName(facultyCreateDto.name());
        newFaculty.setAcronym(facultyCreateDto.acronym());
        newFaculty.setInstitution(institution);

        Faculty createdFaculty = facultyRepository.save(newFaculty);

        return ResponseUtil.sucess(toDto(createdFaculty), "Faculty created sucessfully.", path);
    }

    public ApiResponse<FacultyResponseDto> update(Long id, FacultyUpdateDto facultyUpdateDto, String path){
        Faculty faculty=facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        faculty.setName(facultyUpdateDto.name());
        faculty.setAcronym(facultyUpdateDto.acronym());

        Faculty updatedFaculty=facultyRepository.save(faculty);
        return ResponseUtil.sucess(toDto(updatedFaculty), "Faculty updated successfully.", path);
    }

    public ApiResponse<FacultyResponseDto> delete(Long id, String path) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        facultyRepository.delete(faculty);
        return ResponseUtil.sucess(toDto(faculty), "Sucess", path);
    }

    private FacultyResponseDto toDto(Faculty faculty) {
        return new FacultyResponseDto(faculty.getId(), faculty.getName(), faculty.getAcronym(), faculty.getInstitution().getName());
    }
}
