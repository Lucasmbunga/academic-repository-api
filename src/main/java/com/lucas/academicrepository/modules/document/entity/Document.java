package com.lucas.academicrepository.modules.document.entity;

import com.lucas.academicrepository.modules.academic.entity.Subject;
import com.lucas.academicrepository.modules.document.enums.DocumentStatus;
import com.lucas.academicrepository.modules.document.enums.DocumentType;
import com.lucas.academicrepository.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "Document.fullDetails",
        attributeNodes = {
                @NamedAttributeNode(value = "user"),
                @NamedAttributeNode(value = "subject", subgraph = "subject-course")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "subject-course",
                        attributeNodes = @NamedAttributeNode("course")
                )
        }
)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "teacher_name", length = 100)
    private String teacherName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_length", nullable = false)
    private Float fileLength;

    @Column(name = "file_format", nullable = false, length = 20)
    private String fileFormat;

    @Column(name = "is_solved")
    private Boolean isSolved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    @Column(name = "hash_sha256", length = 64, unique = true)
    private String hashSha256;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}