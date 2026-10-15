package com.lucas.academicrepository.modules.document.service;

import com.lucas.academicrepository.exception.BusinessRuleException;
import com.lucas.academicrepository.exception.NotFoundException;
import com.lucas.academicrepository.modules.academic.entity.Course;
import com.lucas.academicrepository.modules.academic.entity.Faculty;
import com.lucas.academicrepository.modules.academic.entity.Institution;
import com.lucas.academicrepository.modules.academic.entity.Subject;
import com.lucas.academicrepository.modules.academic.repository.CourseRepository;
import com.lucas.academicrepository.modules.academic.repository.FacultyRepository;
import com.lucas.academicrepository.modules.academic.repository.InstitutionRepository;
import com.lucas.academicrepository.modules.academic.repository.SubjectRepository;
import com.lucas.academicrepository.modules.document.dto.DocumentResponseDto;
import com.lucas.academicrepository.modules.document.dto.DocumentStatusUpdateDto;
import com.lucas.academicrepository.modules.document.dto.DocumentUpdateDto;
import com.lucas.academicrepository.modules.document.dto.DocumentUploadDto;
import com.lucas.academicrepository.modules.document.entity.Document;
import com.lucas.academicrepository.modules.document.enums.DocumentStatus;
import com.lucas.academicrepository.modules.document.repository.DocumentRepository;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.modules.user.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final InstitutionRepository institutionRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final StorageService storageService;

    public DocumentService(DocumentRepository documentRepository, InstitutionRepository institutionRepository,FacultyRepository facultyRepository,CourseRepository  courseRepository, SubjectRepository subjectRepository, StorageService storageService) {
        this.documentRepository = documentRepository;
        this.institutionRepository = institutionRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getAll(Pageable pageable) {
        return getPageModel(documentRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public DocumentResponseDto getById(Long id) {
        return mapToResponse(findDocumentById(id));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> searchByTitle(String query, Pageable pageable) {
        return getPageModel(documentRepository.findByTitleContainingIgnoreCase(query, pageable));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getByInstitution(Long institutionId, Pageable pageable) {
        Institution institution = getInstitution(institutionId);
        return getPageModel(documentRepository.findByInstitutionId(institution.getId(), pageable));
    }

    private Institution getInstitution(Long institutionId){
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new NotFoundException("Institution not found with id: " + institutionId));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getByFaculty(Long facultyId, Pageable pageable) {
        Faculty faculty = getFaculty(facultyId);
        return getPageModel(documentRepository.findByFacultyId(faculty.getId(), pageable));
    }

    private Faculty getFaculty(Long facultyId) {
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NotFoundException("Faculty not found with id: " + facultyId));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getByCourse(Long courseId, Pageable pageable) {
        Course course = getCourse(courseId);
        return getPageModel(documentRepository.findByCourseId(course.getId(), pageable));
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
    }

    public PagedModel<DocumentResponseDto> getBySubject(Long subjectId, Pageable pageable) {
        Subject subject = getSubject(subjectId);
        return getPageModel(documentRepository.findBySubjectId(subject.getId(), pageable));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getApprovedDocuments(Pageable pageable) {
        return getPageModel(documentRepository.findByStatus(DocumentStatus.APPROVED, pageable));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getPendingDocuments(Pageable pageable) {
        return getPageModel(documentRepository.findByStatus(DocumentStatus.PENDING, pageable));
    }

    @Transactional(readOnly = true)
    public PagedModel<DocumentResponseDto> getByUploader(Long uploaderId, Pageable pageable) {
        return getPageModel(documentRepository.findAllByUserId(uploaderId, pageable));
    }

    public PagedModel<DocumentResponseDto>  getByTitleOrDescriptionContaining( String topic, Pageable pageable) {
        return getPageModel(documentRepository.findByTitleOrDescriptionContainingIgnoreCase(topic,topic,pageable));
    }

    @Transactional
    public DocumentResponseDto uploadDocument(DocumentUploadDto dto, User uploader) {
        if (dto.file() == null || dto.file().isEmpty()) {
            throw new BusinessRuleException("The document file cannot be empty");
        }

        String hashSha256 = calculateHash(dto.file());
        if(documentRepository.existsByHashSha256(hashSha256)){
            throw new BusinessRuleException("The document file already exists(Duplicated file was found");
        }

        Subject subject = getSubject(dto.subjectId());
        String fileUrl = storageService.uploadFile(dto.file());

        Document doc = new Document();
        doc.setTitle(dto.title());
        doc.setDescription(dto.description());
        doc.setSubject(subject);
        doc.setUser(uploader);
        doc.setDocumentType(dto.documentType());
        doc.setAcademicYear(dto.academicYear());
        doc.setTeacherName(dto.teacherName());
        doc.setFileUrl(fileUrl);
        doc.setFileLength((float) dto.file().getSize());
        doc.setFileFormat(dto.file().getContentType());
        doc.setIsSolved(dto.isSolved() != null ? dto.isSolved() : false);
        doc.setStatus(DocumentStatus.PENDING);
        doc.setHashSha256(hashSha256);

        Document saved = documentRepository.save(doc);
        return mapToResponse(saved);
    }

    private String calculateHash(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to calculate file hash SHA-256");
        }
    }

    public Subject getSubject(Long subjectId){
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + subjectId));
    }

    @Transactional
    public DocumentResponseDto updateDocument(Long id, DocumentUpdateDto dto, User currentUser) {
        Document doc = findDocumentById(id);

        verifyOwnershipOrAdmin(doc,currentUser);

        if (dto.title() != null) doc.setTitle(dto.title());
        if (dto.description() != null) doc.setDescription(dto.description());
        if (dto.teacherName() != null) doc.setTeacherName(dto.teacherName());
        if (dto.academicYear() != null) doc.setAcademicYear(dto.academicYear());
        if (dto.isSolved() != null) doc.setIsSolved(dto.isSolved());

        Document updated = documentRepository.save(doc);
        return mapToResponse(updated);
    }

    @Transactional
    public DocumentResponseDto updateDocumentStatus(Long id, DocumentStatusUpdateDto statusUpdateDto,User   user) {
        if(!(user.getRole() ==Role.ADMINISTRATOR) && !(user.getRole() ==Role.MODERATOR)){
            throw new BusinessRuleException("Only administrators or moderators can update status of a document");
        }
        Document doc = findDocumentById(id);
        doc.setStatus(statusUpdateDto.documentStatus());
        Document updated = documentRepository.save(doc);
        return mapToResponse(updated);
    }

    @Transactional
    public DocumentResponseDto delete(Long id, User currentUser) {
        Document document = findDocumentById(id);

        verifyOwnershipOrAdmin(document, currentUser);
        storageService.deleteFile(document.getFileUrl());

        documentRepository.delete(document);
        return mapToResponse(document);
    }

    private void verifyOwnershipOrAdmin(Document document, User currentUser) {
        boolean isOwner = document.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMINISTRATOR;

        if (!isOwner && !isAdmin) {
            throw new BusinessRuleException("You do not have permission to modify or delete this document");
        }
    }

    private PagedModel<DocumentResponseDto> getPageModel(Page<Document> data){
        return new PagedModel<>(data.map(this::mapToResponse));
    }

    private Document findDocumentById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found with id: " + id));
    }

    private DocumentResponseDto mapToResponse(Document doc) {
        return new DocumentResponseDto(
                doc.getId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getSubject().getId(),
                doc.getSubject().getName(),
                doc.getSubject().getCourse().getId(),
                doc.getSubject().getCourse().getName(),
                doc.getUser().getId(),
                doc.getUser().getName(),
                doc.getDocumentType(),
                doc.getAcademicYear(),
                doc.getTeacherName(),
                doc.getFileUrl(),
                doc.getFileLength(),
                doc.getFileFormat(),
                doc.getIsSolved(),
                doc.getStatus().name(),
                doc.getHashSha256(),
                doc.getThumbnailUrl(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}