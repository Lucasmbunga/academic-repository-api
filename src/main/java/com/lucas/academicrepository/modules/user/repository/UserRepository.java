package com.lucas.academicrepository.modules.user.repository;

import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    UserDetails findByEmail(String email);
    List<User> findAllByInstitution(Institution institution);
    List<User> findAllByFaculty(Faculty faculty);
    List<User> findByCourse(Course course);
    boolean existsByEmail(String email);
}
