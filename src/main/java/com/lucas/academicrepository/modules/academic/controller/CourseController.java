package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.CourseCreateDto;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.CourseUpdateDto;
import com.lucas.academicrepository.modules.academic.service.CourseService;
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
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<CourseResponseDto>>> getAllCourses(@PageableDefault(sort = "name") Pageable pageable){
        var response= ResponseUtil.sucess(courseService.getAll(pageable),"Success","/api/v1/courses");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> getById(@PathVariable Long id){
        var response=ResponseUtil.sucess(courseService.getById(id),"Success","/api/v1/courses/"+id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/faculties/{id}")
    public ResponseEntity<ApiResponse<PagedModel<CourseResponseDto>>> getByFaculty(@PathVariable Long id,@PageableDefault Pageable pageable){
        var response=ResponseUtil.sucess(courseService.getByFaculty(id,pageable),"Success","/api/v1/courses/"+id);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDto>> create(@Valid @RequestBody CourseCreateDto courseCreateDto){
        var response=ResponseUtil.sucess(courseService.create(courseCreateDto),"Course registered successfully","/api/v1/courses");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> update(@PathVariable Long id, @Valid @RequestBody CourseUpdateDto courseUpdateDto){
        var response=ResponseUtil.sucess(courseService.update(id,courseUpdateDto),"Course updated successfully","/api/v1/courses/"+id);
     return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> delete(@PathVariable Long id){
        var response=ResponseUtil.sucess(courseService.delete(id),"Course deleted successfully","/api/v1/courses/"+id);
        return ResponseEntity.ok(response);
    }
}
