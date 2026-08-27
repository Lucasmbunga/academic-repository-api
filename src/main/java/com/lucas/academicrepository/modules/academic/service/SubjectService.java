package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.CourseResponseDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectCreateDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectResponseDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Subject;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.SubjectRepository;
import com.lucas.academicrepository.util.ApiResponse;
import com.lucas.academicrepository.util.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    public SubjectService(CourseRepository courseRepository, SubjectRepository subjectRepository) {
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
    }

    public ApiResponse<SubjectResponseDto> getById(Long id,String path) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        return ResponseUtil.sucess(toDto(subject),"Sucess",path);
    }
    public ApiResponse<List<SubjectResponseDto>> getByCourse(Long courseId, String path) {
        Course course = courseRepository.findById(courseId).orElseThrow(()->new NotFoundException("Course not found"));
        List<SubjectResponseDto>  subjects=subjectRepository.findByCourse(course).stream().map(this::toDto).toList();

        return ResponseUtil.sucess(subjects,"Sucess",path);
    }

    public ApiResponse<SubjectResponseDto> create(SubjectCreateDto subjectCreateDto,String path) {
        Course course=courseRepository.findById(subjectCreateDto.courseId()).orElseThrow(()->new NotFoundException("Course not found"));
        Subject newSubject=new Subject();
        newSubject.setName(subjectCreateDto.name());
        newSubject.setSemester(subjectCreateDto.semester());
        newSubject.setAcademicYear(subjectCreateDto.academicYear());
        newSubject.setCourse(course);

        Subject createdSubject=subjectRepository.save(newSubject);
        return ResponseUtil.sucess(toDto(createdSubject),"Subject created sucessfully",path);
    }

    public ApiResponse<SubjectResponseDto> update(Long id, SubjectUpdateDto subjectUpdateDto, String path) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        subject.setName(subjectUpdateDto.name());
        subject.setSemester(subjectUpdateDto.semester());
        subject.setAcademicYear(subjectUpdateDto.academicYear());

        Subject updatedSubject=subjectRepository.save(subject);
        return ResponseUtil.sucess(toDto(updatedSubject),"Subject updated sucessfully",path);

    }

    public ApiResponse<SubjectResponseDto> delete(Long id, String path) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        subjectRepository.delete(subject);
        return ResponseUtil.sucess(toDto(subject),"Subject deleted sucessfully",path);
    }
    private SubjectResponseDto toDto(Subject subject) {
        return new SubjectResponseDto(subject.getId(), subject.getName(),subject.getAcademicYear(),subject.getSemester(),subject.getCourse().getName());
    }
}
