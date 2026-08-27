package com.lucas.academicrepository.modules.document.repository;

import com.lucas.academicrepository.modules.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
