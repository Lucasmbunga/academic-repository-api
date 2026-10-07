package com.lucas.academicrepository.modules.document.repository;


import com.lucas.academicrepository.modules.document.entity.Document;
import com.lucas.academicrepository.modules.document.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @EntityGraph(value = "Document.fullDetails")
    Page<Document> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    @EntityGraph(value = "Document.fullDetails")
    Page<Document> findBySubjectId(Long subjectId, Pageable pageable);

    @EntityGraph(value = "Document.fullDetails")
    Page<Document> findByTitleOrDescriptionContainingIgnoreCase(String title,String description, Pageable pageable);

    @EntityGraph(value = "Document.fullDetails")
    Page<Document> findAllByUserId(Long uploaderId, Pageable pageable);
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    Boolean existsByHashSha256(String hashSha256);

    //  Filtrar por Curso (Document -> Subject -> Course)
    @EntityGraph(value = "Document.fullDetails")
    @Query(value = """
            SELECT d FROM Document d
            JOIN d.subject s
            WHERE s.course.id = :courseId
            """,
            countQuery = """
                    SELECT COUNT(d) FROM Document d
                    JOIN d.subject s
                    WHERE s.course.id = :courseId
                    """)
    Page<Document> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    //  Filtrar por Faculdade (Document -> Subject -> Course -> Faculty)
    @Query(value = """
            SELECT d FROM Document d
            JOIN d.subject s
            JOIN s.course c
            WHERE c.faculty.id = :facultyId
            """,
            countQuery = """
                    SELECT COUNT(d) FROM Document d
                    JOIN d.subject s
                    JOIN s.course c
                    WHERE c.faculty.id = :facultyId
                    """)
    Page<Document> findByFacultyId(@Param("facultyId") Long facultyId, Pageable pageable);

    // Filtrar por Instituição (Document -> Subject -> Course -> Faculty -> Institution)
    @EntityGraph(value = "Document.fullDetails")
    @Query(value = """
            SELECT d FROM Document d
            JOIN d.subject s
            JOIN s.course c
            JOIN c.faculty f
            WHERE f.institution.id = :institutionId
            """,
            countQuery = """
                    SELECT COUNT(d) FROM Document d
                    JOIN d.subject s
                    JOIN s.course c
                    JOIN c.faculty f
                    WHERE f.institution.id = :institutionId
                    """)
    Page<Document> findByInstitutionId(@Param("institutionId") Long institutionId, Pageable pageable);




}
