package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.FacultyCreateDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyResponseDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyUpdateDto;
import com.lucas.academicrepository.modules.academic.service.FacultyService;
import com.lucas.academicrepository.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faculties")
public class FacultyController {
    private final FacultyService facultyService;
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<FacultyResponseDto>>> getAll(@PageableDefault(sort="name",direction = Sort.Direction.DESC) Pageable pageable){
        return  ResponseEntity.ok(facultyService.getAll(pageable,"/api/v1/faculties"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> getById(@PathVariable Long id){
        return ResponseEntity.ok(facultyService.getById(id,"/api/v1/faculties/"+id));
    }
    @GetMapping("/institutions/{institutionId}")
    public ResponseEntity<ApiResponse<List<FacultyResponseDto>>> geBytInstitution(@PathVariable Long institutionId){
        return ResponseEntity.ok(facultyService.getByInstitution(institutionId,"/api/v1/faculties/"+institutionId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponseDto>> create(@Valid @RequestBody FacultyCreateDto facultyCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.create(facultyCreateDto,"/api/v1/faculties"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> update(@PathVariable Long id, @Valid @RequestBody FacultyUpdateDto facultyUpdateDto){
        return ResponseEntity.ok(facultyService.update(id, facultyUpdateDto,"/api/v1/faculties/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> delete(@PathVariable Long id){
        return ResponseEntity.ok(facultyService.delete(id,"/api/v1/faculties"));
    }
}
