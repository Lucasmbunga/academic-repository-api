package com.lucas.academicrepository.modules.academic.repository;

import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByFaculty(Faculty faculty);
    boolean existsByFacultyAndName(Faculty faculty,String name);
}
