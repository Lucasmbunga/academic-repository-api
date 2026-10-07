package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyCreateDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyResponseDto;
import com.lucas.academicrepository.modules.academic.dto.FacultyUpdateDto;
import com.lucas.academicrepository.modules.academic.service.CourseService;
import com.lucas.academicrepository.modules.academic.service.FacultyService;
import com.lucas.academicrepository.modules.document.dto.DocumentResponseDto;
import com.lucas.academicrepository.modules.document.service.DocumentService;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/faculties")
public class FacultyController {
    private final FacultyService facultyService;
    private  final CourseService courseService;
    private final DocumentService documentService;
    public FacultyController(FacultyService facultyService,CourseService courseService,DocumentService documentService) {
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.documentService = documentService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<FacultyResponseDto>>> getAll(@PageableDefault(sort="name",direction = Sort.Direction.DESC) Pageable pageable){
        var response= ResponseUtil.sucess(facultyService.getAll(pageable),"Success","/api/v1/faculties");
        return  ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> getById(@PathVariable Long id){
        var response=ResponseUtil.sucess(facultyService.getById(id),"Success","/api/v1/faculties");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<PagedModel<CourseResponseDto>>> getCoursesByFaculty(@PathVariable Long id,@PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(courseService.getByFaculty(id,pageable),"Success","/api/v1/faculties/"+id+"/courses");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<PagedModel<DocumentResponseDto>>> getDocumentsByFaculty(@PathVariable Long id, @PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(documentService.getByFaculty(id,pageable),"Success","/api/v1/faculties/"+id+"/courses");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponseDto>> create(@Valid @RequestBody FacultyCreateDto facultyCreateDto){
        var response=ResponseUtil.sucess(facultyService.create(facultyCreateDto),"Faculty registered successfully.","/api/v1/faculties");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> update(@PathVariable Long id, @Valid @RequestBody FacultyUpdateDto facultyUpdateDto){
        var response=ResponseUtil.sucess(facultyService.update(id,facultyUpdateDto),"Faculty updated successfully.","/api/v1/faculties");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponseDto>> delete(@PathVariable Long id){
        var response=ResponseUtil.sucess(facultyService.delete(id),"Faculty deleted successfully.","/api/v1/faculties/"+id);
        return ResponseEntity.ok(response);
    }
}
