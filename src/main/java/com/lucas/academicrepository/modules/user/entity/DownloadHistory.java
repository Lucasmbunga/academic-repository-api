package com.lucas.academicrepository.modules.user.entity;

import com.lucas.academicrepository.modules.document.entity.Document;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "download_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "credits_spent")
    private Integer creditsSpent;

    @Column(name = "downloaded_at", updatable = false)
    private LocalDateTime downloadedAt;

    @PrePersist
    protected void onCreate() {
        this.downloadedAt = LocalDateTime.now();
        if (this.creditsSpent == null) this.creditsSpent = 0;
    }
}
