package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.SubjectCreateDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectResponseDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectUpdateDto;
import com.lucas.academicrepository.modules.academic.service.SubjectService;
import com.lucas.academicrepository.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getByCourse(@PathVariable Long courseId)
    {
        return ResponseEntity.ok(subjectService.getByCourse(courseId,"/api/v1/subjects/courses/"+courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> getById(@PathVariable Long id){
        return ResponseEntity.ok(subjectService.getById(id,"/api/v1/subjects/"+id));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDto>> create(@Valid @RequestBody SubjectCreateDto subjectCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(subjectCreateDto,"/api/v1/subjects/"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> update(@PathVariable Long id, @Valid @RequestBody SubjectUpdateDto subjectUpdateDto){
        return ResponseEntity.ok(subjectService.update(id,subjectUpdateDto,"/api/v1/subjects/"+id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> delete(@PathVariable Long id){
        return ResponseEntity.ok(subjectService.delete(id,"/api/v1/subjects/"+id));
    }
}
