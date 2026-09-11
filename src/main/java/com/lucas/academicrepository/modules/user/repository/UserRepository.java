package com.lucas.academicrepository.modules.user.repository;

import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    UserDetails findByEmail(String email);
    Page<User> findAllByInstitution(Institution institution, Pageable pageable);
    Page<User> findAllByFaculty(Faculty faculty,Pageable pageable);
    Page<User> findByCourse(Course course,Pageable pageable);
    boolean existsByEmail(String email);
}
