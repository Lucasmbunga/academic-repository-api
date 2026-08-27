package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.InstitutionCreateDto;
import com.lucas.academicrepository.modules.academic.dto.InstitutionResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public InstitutionService (InstitutionRepository institutionRepository){
        this.institutionRepository=institutionRepository;
    }

    public ApiResponse<List<InstitutionResponseDto>> getAll(Pageable pageable,String path) {
        List<InstitutionResponseDto> institutions = institutionRepository.findAll(pageable).getContent().stream().map(this::toDto).toList();
        return ResponseUtil.sucess(institutions,"Found",path);
    }

    public ApiResponse<InstitutionResponseDto> getById(Long id,String path) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        return ResponseUtil.sucess(toDto(institution),"Found",path);
    }

    public ApiResponse<InstitutionResponseDto> create(InstitutionCreateDto institutionCreateDto, String path) {
        Institution newInstitution=new Institution();
        newInstitution.setName(institutionCreateDto.name());
        newInstitution.setAcronym(institutionCreateDto.acronym());
        newInstitution.setType(institutionCreateDto.type());

        var createdInstitution=institutionRepository.save(newInstitution);
        return ResponseUtil.sucess(toDto(createdInstitution),"Institution created sucessfully",path);
    }

    public ApiResponse<InstitutionResponseDto> update(Long id,InstitutionCreateDto institutionCreateDto,String path) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        institution.setName(institutionCreateDto.name());
        institution.setAcronym(institutionCreateDto.acronym());
        institution.setType(institutionCreateDto.type());
        Institution updatedInstitution=institutionRepository.save(institution);

        return ResponseUtil.sucess(toDto(updatedInstitution),"Institution updated sucessfully",path);
    }

    public ApiResponse<InstitutionResponseDto> delete(Long id,String path) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        institutionRepository.delete(institution);
        return ResponseUtil.sucess(toDto(institution),"Institution deleted sucessfully",path);
    }
    public InstitutionResponseDto toDto(Institution institution){
        return new InstitutionResponseDto(institution.getId(),institution.getName(),institution.getAcronym(),institution.getType(),institution.getCreatedAt());
    }
}
