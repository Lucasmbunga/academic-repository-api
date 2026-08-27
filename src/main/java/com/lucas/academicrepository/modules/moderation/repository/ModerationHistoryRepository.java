package com.lucas.academicrepository.modules.moderation.repository;

import com.lucas.academicrepository.modules.moderation.entity.ModerationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationHistoryRepository extends JpaRepository<ModerationHistory,Long> {
}
