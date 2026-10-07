package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.SubjectCreateDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectResponseDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectUpdateDto;
import com.lucas.academicrepository.modules.academic.service.SubjectService;
import com.lucas.academicrepository.modules.document.dto.DocumentResponseDto;
import com.lucas.academicrepository.modules.document.service.DocumentService;
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
@RequestMapping("/api/v1/subjects")
public class SubjectController {
    private final SubjectService subjectService;
    private final DocumentService documentService;

    public SubjectController(SubjectService subjectService,DocumentService documentService) {
        this.subjectService = subjectService;
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<SubjectResponseDto>>> getAll(@PageableDefault Pageable pageable) {
        var response = ResponseUtil.sucess(subjectService.getAll(pageable), "Success", "/api/v1/subjects");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> getById(@PathVariable Long id) {
        var response = ResponseUtil.sucess(subjectService.getById(id), "Success", "/api/v1/subjects/" + id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getDocumentsBySubject(@PathVariable Long id, @PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.getBySubject(id, pageable), "Success", "/api/v1/subjects/"+id+"/documents");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDto>> create(@Valid @RequestBody SubjectCreateDto subjectCreateDto) {
        var response = ResponseUtil.sucess(subjectService.create(subjectCreateDto), "Subject registered successfully", "/api/v1/subjects");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> update(@PathVariable Long id, @Valid @RequestBody SubjectUpdateDto subjectUpdateDto) {
        var response = ResponseUtil.sucess(subjectService.update(id, subjectUpdateDto), "Subject updated successfully", "/api/v1/subjects/" + id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> delete(@PathVariable Long id) {
        var response = ResponseUtil.sucess(subjectService.delete(id), "Subject deleted successfully", "/api/v1/subjects/" + id);
        return ResponseEntity.ok(response);
    }
}
