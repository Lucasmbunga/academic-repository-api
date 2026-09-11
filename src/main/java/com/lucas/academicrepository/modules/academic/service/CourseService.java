package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.CourseCreateDto;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.CourseUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;

    public CourseService(FacultyRepository facultyRepository, CourseRepository courseRepository) {
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
    }

    public PagedModel<CourseResponseDto> getAll(Pageable pageable) {
        PagedModel<CourseResponseDto> courses = new PagedModel<>(courseRepository.findAll(pageable).map(this::toDto));
        return courses;
    }

    public CourseResponseDto getById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        return toDto(course);
    }

    public PagedModel<CourseResponseDto> getByFaculty(Long id,Pageable pageable) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty not found"));
        PagedModel<CourseResponseDto> courses = new PagedModel<>(courseRepository.findByFaculty(faculty,pageable).map(this::toDto));
        return courses;
    }

    public CourseResponseDto create(CourseCreateDto courseCreateDto) {
        Faculty faculty = facultyRepository.findById(courseCreateDto.facultyId()).orElseThrow(() -> new NotFoundException("Faculty not found"));
        if (courseRepository.existsByFacultyAndName(faculty, courseCreateDto.name())){
            throw new IllegalStateException("Course name already exists in Selected faculty");
        }

        Course newCourse = new Course();
        newCourse.setName(courseCreateDto.name());
        newCourse.setDegree(courseCreateDto.degree());
        newCourse.setDuration(courseCreateDto.duration());
        newCourse.setFaculty(faculty);

        Course savedCourse = courseRepository.save(newCourse);

        return toDto(savedCourse);
    }

    public CourseResponseDto update(Long id, CourseUpdateDto courseUpdateDto) {
        Course course=courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getName().equals(courseUpdateDto.name()) && courseRepository.existsByFacultyAndName(course.getFaculty(), courseUpdateDto.name())){
            throw new IllegalStateException("Course name already exists in Selected faculty");
        }

        course.setName(courseUpdateDto.name());
        course.setDegree(courseUpdateDto.degree());
        course.setDuration(courseUpdateDto.duration());
        Course updatedCourse= courseRepository.save(course);
        return toDto(updatedCourse);
    }
    public CourseResponseDto delete(Long id) {
        Course course= courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
        courseRepository.delete(course);
        return toDto(course);
    }

    private CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(course.getId(), course.getName(), course.getDegree(), course.getDuration(), course.getFaculty().getName());
    }


}
