package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.InstitutionCreateDto;
import com.lucas.academicrepository.modules.academic.dto.InstitutionResponseDto;
import com.lucas.academicrepository.modules.academic.service.InstitutionService;
import com.lucas.academicrepository.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions")
public class InstitutionController {
    private final InstitutionService institutionService;
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstitutionResponseDto>>> getAllInstitutions( @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(institutionService.getAll(pageable,"/api/v1/institutions"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> getInstitutionById(@PathVariable Long id){
        return ResponseEntity.ok(institutionService.getById(id,"/api/v1/institutions/"+id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> create(@Valid @RequestBody InstitutionCreateDto institutionCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(institutionService.create(institutionCreateDto,"/api/v1/institutions"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> update(@PathVariable Long id,@Valid @RequestBody InstitutionCreateDto institutionCreateDto){
        return ResponseEntity.ok(institutionService.update(id, institutionCreateDto,"/api/v1/institutions/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> delete(@PathVariable Long id){
        return ResponseEntity.ok(institutionService.delete(id,"/api/v1/institutions/"+id));
    }
}
