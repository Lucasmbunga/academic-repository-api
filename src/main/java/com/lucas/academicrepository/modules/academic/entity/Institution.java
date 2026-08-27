package com.lucas.academicrepository.modules.academic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "institution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String acronym;

    //O tipo de instituicao poderá mudar, tornando assim um emun('PRIVATE','PUBLIC','PRIVATE_PUBLIC')
    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}