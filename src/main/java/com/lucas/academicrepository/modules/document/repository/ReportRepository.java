package com.lucas.academicrepository.modules.document.repository;

import com.lucas.academicrepository.modules.document.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
}
