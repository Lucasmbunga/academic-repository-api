package com.lucas.academicrepository.modules.document.repository;

import com.lucas.academicrepository.modules.document.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
