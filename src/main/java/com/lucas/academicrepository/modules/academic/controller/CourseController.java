package com.lucas.academicrepository.modules.academic.controller;

import com.lucas.academicrepository.modules.academic.dto.CourseCreateDto;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.CourseUpdateDto;
import com.lucas.academicrepository.modules.academic.service.CourseService;
import com.lucas.academicrepository.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getAllCourses(@PageableDefault(sort = "name") Pageable pageable){
        return ResponseEntity.ok(courseService.getAll(pageable,"/api/v1/courses"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> getById(@PathVariable Long id){
        return ResponseEntity.ok(courseService.getById(id,"/api/v1/courses/"+id));
    }
    @GetMapping("/faculties/{id}")
    public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getByFaculty(@PathVariable Long id){
        return ResponseEntity.ok(courseService.getByFaculty(id,"/api/v1/courses/faculty/"+id));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDto>> create(@Valid @RequestBody CourseCreateDto courseCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(courseCreateDto,"/api/v1/courses"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> update(@PathVariable Long id, @Valid @RequestBody CourseUpdateDto courseUpdateDto){
     return ResponseEntity.ok(courseService.update(id, courseUpdateDto,"/api/v1/courses/"+id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> delete(@PathVariable Long id){
        return ResponseEntity.ok(courseService.delete(id,"/api/v1/courses/"+id));
    }
}
