package com.lucas.academicrepository.modules.document.repository;

import com.lucas.academicrepository.modules.document.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating,Long> {
}
