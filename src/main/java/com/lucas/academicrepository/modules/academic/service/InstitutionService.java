package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.InstitutionCreateDto;
import com.lucas.academicrepository.modules.academic.dto.InstitutionResponseDto;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public InstitutionService (InstitutionRepository institutionRepository){
        this.institutionRepository=institutionRepository;
    }

    public PagedModel<InstitutionResponseDto> getAll(Pageable pageable) {
        PagedModel<InstitutionResponseDto> response=new PagedModel<>(institutionRepository.findAll(pageable).map(this::toDto));
        return response;
    }

    public InstitutionResponseDto getById(Long id) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        return toDto(institution);
    }

    public InstitutionResponseDto create(InstitutionCreateDto institutionCreateDto) {
        Institution newInstitution=new Institution();
        newInstitution.setName(institutionCreateDto.name());
        newInstitution.setAcronym(institutionCreateDto.acronym());
        newInstitution.setType(institutionCreateDto.type());

        var createdInstitution=institutionRepository.save(newInstitution);
        return toDto(createdInstitution);
    }

    public InstitutionResponseDto update(Long id,InstitutionCreateDto institutionCreateDto) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        institution.setName(institutionCreateDto.name());
        institution.setAcronym(institutionCreateDto.acronym());
        institution.setType(institutionCreateDto.type());
        Institution updatedInstitution=institutionRepository.save(institution);

        return toDto(updatedInstitution);
    }

    public InstitutionResponseDto delete(Long id) {
        Institution institution=institutionRepository.findById(id).orElseThrow(()->new NotFoundException("Insitution not found with id:"+id));
        institutionRepository.delete(institution);
        return toDto(institution);
    }
    public InstitutionResponseDto toDto(Institution institution){
        return new InstitutionResponseDto(institution.getId(),institution.getName(),institution.getAcronym(),institution.getType(),institution.getCreatedAt());
    }
}
