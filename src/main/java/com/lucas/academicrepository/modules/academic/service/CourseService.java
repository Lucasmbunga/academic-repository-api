package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.CourseCreateDto;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.CourseUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;

    public CourseService(FacultyRepository facultyRepository, CourseRepository courseRepository) {
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
    }

    public ApiResponse<List<CourseResponseDto>> getAll(Pageable pageable, String path) {

        List<CourseResponseDto> courses = courseRepository.findAll(pageable).map(this::toDto).toList();
        return ResponseUtil.sucess(courses, "Sucess", path);
    }

    public ApiResponse<CourseResponseDto> getById(Long id, String path) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        return ResponseUtil.sucess(toDto(course), "Sucess", path);
    }

    public ApiResponse<List<CourseResponseDto>> getByFaculty(Long id, String path) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        List<CourseResponseDto> courses = courseRepository.findByFaculty(faculty).stream().map(this::toDto).toList();
        return ResponseUtil.sucess(courses, "Sucess", path);
    }

    public ApiResponse<CourseResponseDto> create(CourseCreateDto courseCreateDto, String path) {
        Faculty faculty = facultyRepository.findById(courseCreateDto.facultyId()).orElseThrow(() -> new NotFoundException("Faculty not found"));
        if (courseRepository.existsByFacultyAndName(faculty, courseCreateDto.name())){
            throw new IllegalStateException("Course name already exists in Selected faculty");
        }

        Course newCourse = new Course();
        newCourse.setName(courseCreateDto.name());
        newCourse.setDegree(courseCreateDto.degree());
        newCourse.setFaculty(faculty);

        Course savedCourse = courseRepository.save(newCourse);

        return ResponseUtil.sucess(toDto(savedCourse), "Sucess", path);
    }

    public ApiResponse<CourseResponseDto> update(Long id, CourseUpdateDto courseUpdateDto, String path) {
        Course course=courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getName().equals(courseUpdateDto.name()) && courseRepository.existsByFacultyAndName(course.getFaculty(), courseUpdateDto.name())){
            throw new IllegalStateException("Course name already exists in Selected faculty");
        }

        course.setName(courseUpdateDto.name());
        course.setDegree(courseUpdateDto.degree());
        course.setDuration(courseUpdateDto.duration());
        Course updatedCourse= courseRepository.save(course);
        return ResponseUtil.sucess(toDto(updatedCourse), "Sucess", path);
    }
    public ApiResponse<CourseResponseDto> delete(Long id, String path) {
        Course course= courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        courseRepository.delete(course);
        return ResponseUtil.sucess(toDto(course), "Course deleted sucessfully.", path);
    }

    private CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(course.getId(), course.getName(), course.getDegree(), course.getDuration(), course.getFaculty().getName());
    }


}
