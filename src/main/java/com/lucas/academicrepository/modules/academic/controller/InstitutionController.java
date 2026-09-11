package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.InstitutionCreateDto;
import com.lucas.academicrepository.modules.academic.dto.InstitutionResponseDto;
import com.lucas.academicrepository.modules.academic.service.InstitutionService;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/institutions")
public class InstitutionController {
    private final InstitutionService institutionService;
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<InstitutionResponseDto>>> getAllInstitutions(@PageableDefault(size = 10) Pageable pageable){
        var response=ResponseUtil.sucess(institutionService.getAll(pageable),"Success","/api/v1/institutions");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> getInstitutionById(@PathVariable Long id){
        var response= ResponseUtil.sucess(institutionService.getById(id),"Succes","/api/v1/institutions/"+id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> create(@Valid @RequestBody InstitutionCreateDto institutionCreateDto){
        var response=ResponseUtil.sucess(institutionService.create(institutionCreateDto),"Institution registered successfully","/api/v1/institutions");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> update(@PathVariable Long id,@Valid @RequestBody InstitutionCreateDto institutionCreateDto){
        var response=ResponseUtil.sucess(institutionService.update(id, institutionCreateDto),"Institution updated successfully","/api/v1/institutions/"+id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<InstitutionResponseDto>> delete(@PathVariable Long id){
        var response=ResponseUtil.sucess(institutionService.delete(id),"Institution deleted successfully","/api/v1/institutions/"+id);
        return ResponseEntity.ok(response);
    }
}
