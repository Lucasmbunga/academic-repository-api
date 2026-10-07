package com.lucas.academicrepository.modules.document.controller;

import com.lucas.academicrepository.modules.document.dto.DocumentResponseDto;
import com.lucas.academicrepository.modules.document.dto.DocumentStatusUpdateDto;
import com.lucas.academicrepository.modules.document.dto.DocumentUpdateDto;
import com.lucas.academicrepository.modules.document.dto.DocumentUploadDto;
import com.lucas.academicrepository.modules.document.service.DocumentService;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")

public class DocumentController {

    private final DocumentService documentService;
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getAllDocuments(@PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.getAll(pageable),"Success","/api/v1/documents");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getDocumentById(@PathVariable Long id ) {
        var response=ResponseUtil.sucess(documentService.getById(id),"Success","/api/v1/documents/"+id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getAprovedDocuments( @PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.getApprovedDocuments(pageable),"Success","/api/v1/documents/approved");
        return ResponseEntity.ok(response);

    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getPendingDocuments( @PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.getPendingDocuments(pageable),"Success","/api/v1/documents/pending");
        return ResponseEntity.ok(response);

    }

    @GetMapping("/titles")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> searchDocumentsByTitle(@RequestParam("title") String title, @PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.searchByTitle(title, pageable),"Success","/api/v1/documents/titles?q="+title);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/topic")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getDocumentsByTitleOrDescriptionContaining(@RequestParam(name = "topic") String topic,@PageableDefault Pageable pageable) {
        var response=ResponseUtil.sucess(documentService.getByTitleOrDescriptionContaining(topic,pageable),"Success","/api/v1/documents/topic?q="+topic);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @Valid @ModelAttribute DocumentUploadDto dto,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        var response = ResponseUtil.sucess(documentService.uploadDocument(dto, authenticatedUser),"File uploaded successfully.","/api/v1/documents");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> updateDocument(@PathVariable Long id,@RequestBody DocumentUpdateDto dto,@AuthenticationPrincipal User authenticatedUser) {
        var response=ResponseUtil.sucess(documentService.updateDocument(id,dto,authenticatedUser),"Document updated successfully","/api/v1/documents/"+id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> updateDocumentStatus(@PathVariable Long id, @RequestBody DocumentStatusUpdateDto documentStatus,@AuthenticationPrincipal User user){
        var response=ResponseUtil.sucess(documentService.updateDocumentStatus(id,documentStatus,user),"Document status updated successfully","/api/v1/documents/"+id+"/status");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> deleteDocument(@PathVariable Long id,@AuthenticationPrincipal User authenticatedUser) {
        var response=ResponseUtil.sucess(documentService.delete(id,authenticatedUser),"Document deleted successfully","/api/v1/documents/"+id);
        return ResponseEntity.ok(response);
    }

}