package com.lucas.academicrepository.modules.user.repository;

import com.lucas.academicrepository.modules.user.entity.DownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadHistoryRepository extends JpaRepository<DownloadHistory,Long> {

}
