package com.lucas.academicrepository.modules.academic.repository;

import com.lucas.academicrepository.modules.academic.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution,Long> {
}
