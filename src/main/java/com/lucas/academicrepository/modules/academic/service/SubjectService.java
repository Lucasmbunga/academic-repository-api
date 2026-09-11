package com.lucas.academicrepository.modules.academic.service;

import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.dto.SubjectCreateDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectResponseDto;
import com.lucas.academicrepository.modules.academic.dto.SubjectUpdateDto;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Subject;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.SubjectRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    public SubjectService(CourseRepository courseRepository, SubjectRepository subjectRepository) {
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
    }

    public SubjectResponseDto getById(Long id) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        return toDto(subject);
    }
    public PagedModel<SubjectResponseDto> getByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId).orElseThrow(()->new NotFoundException("Course not found"));
        PagedModel<SubjectResponseDto>  subjects=new PagedModel<>(subjectRepository.findByCourse(course,pageable).map(this::toDto));

        return subjects;
    }

    public SubjectResponseDto create(SubjectCreateDto subjectCreateDto) {
        Course course=courseRepository.findById(subjectCreateDto.courseId()).orElseThrow(()->new NotFoundException("Course not found"));
        Subject newSubject=new Subject();
        newSubject.setName(subjectCreateDto.name());
        newSubject.setSemester(subjectCreateDto.semester());
        newSubject.setAcademicYear(subjectCreateDto.academicYear());
        newSubject.setCourse(course);

        Subject createdSubject=subjectRepository.save(newSubject);
        return toDto(createdSubject);
    }

    public SubjectResponseDto update(Long id, SubjectUpdateDto subjectUpdateDto) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        subject.setName(subjectUpdateDto.name());
        subject.setSemester(subjectUpdateDto.semester());
        subject.setAcademicYear(subjectUpdateDto.academicYear());

        Subject updatedSubject=subjectRepository.save(subject);
        return toDto(updatedSubject);

    }

    public SubjectResponseDto delete(Long id) {
        Subject subject=subjectRepository.findById(id).orElseThrow(()->new NotFoundException("Subject not found"));
        subjectRepository.delete(subject);
        return toDto(subject);
    }
    private SubjectResponseDto toDto(Subject subject) {
        return new SubjectResponseDto(subject.getId(), subject.getName(),subject.getAcademicYear(),subject.getSemester(),subject.getCourse().getName());
    }
}
