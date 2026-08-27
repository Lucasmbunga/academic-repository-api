package com.lucas.academicrepository.modules.academic.repository;

import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
    List<Subject> findByCourse(Course course);
}
