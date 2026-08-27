--  Initial Database Schema for Academic Repository

-- 1. INSTITUTIONS
CREATE TABLE institution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    acronym VARCHAR(10) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. FACULTIES
CREATE TABLE faculty (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    acronym VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_faculty_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. COURSES
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    degree VARCHAR(50) NOT NULL,
    duration INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. SUBJECTS
CREATE TABLE subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    academic_year INT NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. USERS
CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id BIGINT NULL,
    faculty_id BIGINT NULL,
    course_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMINISTRATOR','MODERATOR','STUDENT','VISITOR') NOT NULL DEFAULT 'VISITOR',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE SET NULL,
    CONSTRAINT fk_user_faculty FOREIGN KEY(faculty_id) REFERENCES faculty(id) ON DELETE SET NULL,
    CONSTRAINT fk_user_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. DOCUMENTS
CREATE TABLE document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    document_type ENUM('ENTRANCE_EXAM','NORMAL_EXAM','RETAKE_EXAM','EXERCISE','PARTIAL_TEST','SUMMARY','ASSESSMENT') NOT NULL,
    academic_year INT NOT NULL,
    teacher_name VARCHAR(100),
    file_url VARCHAR(255) NOT NULL,
    file_length FLOAT NOT NULL,
    file_format VARCHAR(20) NOT NULL,
    is_solved BOOLEAN DEFAULT FALSE,
    status ENUM('PENDING','APROVED','REJECTED','QUARANTINE') NOT NULL DEFAULT 'PENDING',
    hash_sha256 VARCHAR(64) UNIQUE,
    thumbnail_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_document_subject FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. COMMENTS
CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. RATINGS
CREATE TABLE rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stars_count INT NOT NULL,
    quality_feedback VARCHAR(100),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. REPORTS
CREATE TABLE report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. MODERATION HISTORY
CREATE TABLE moderation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    moderator_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_moderation_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE,
    CONSTRAINT fk_moderation_moderator FOREIGN KEY (moderator_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. DOWNLOAD HISTORY
CREATE TABLE download_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    credits_spent INT DEFAULT 0,
    downloaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_download_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE,
    CONSTRAINT fk_download_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- INDEXES FOR OPTIMIZED SEARCH
CREATE INDEX idx_document_search ON document(subject_id, document_type, status);
CREATE INDEX idx_subject_course ON subject(course_id);