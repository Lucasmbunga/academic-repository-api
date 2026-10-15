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
import com.lucas.academicrepository.modules.document.enums.DocumentType;
import com.lucas.academicrepository.modules.document.repository.DocumentRepository;
import com.lucas.academicrepository.modules.user.entity.User;
import com.lucas.academicrepository.modules.user.enums.Role;
import com.lucas.academicrepository.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.lucas.academicrepository.modules.document.enums.DocumentStatus.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private DocumentService documentService;

    private Document document;
    private User studentUser;
    private User adminUser;
    private Institution institution;
    private Institution institutionForFalseTest;
    private Faculty faculty;
    private Course course;
    private Subject subject;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setName("Estudante Teste");
        studentUser.setRole(Role.STUDENT);
        studentUser.setFaculty(faculty);
        studentUser.setCourse(course);
        studentUser.setInstitution(institution);
        studentUser.setEmail("student@gmail.com");

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setName("Admin Teste");
        adminUser.setRole(Role.ADMINISTRATOR);
        adminUser.setFaculty(faculty);
        adminUser.setCourse(course);
        adminUser.setInstitution(institution);
        adminUser.setEmail("admin@gmail.com");

        institution = new Institution();
        institution.setId(10L);
        institution.setName("Kimpa Vita");
        institution.setAcronym("UNIKIVI");
        institution.setType("PUBLIC");

        institutionForFalseTest=new Institution();
        institutionForFalseTest.setId(20L);
        institutionForFalseTest.setName("Institutio for false test");
        institutionForFalseTest.setAcronym("IFFT");
        institutionForFalseTest.setType("PUBLIC");

        faculty = new Faculty();
        faculty.setId(20L);
        faculty.setName("Instituto Politécnico");
        faculty.setAcronym("IP");
        faculty.setInstitution(institution);

        course = new Course();
        course.setId(30L);
        course.setName("Engenharia Informática");
        course.setFaculty(faculty);
        course.setDuration(4);
        course.setDegree("Licenciatura");


        subject = new Subject();
        subject.setId(100L);
        subject.setName("Programação I");
        subject.setCourse(course);
        subject.setSemester(1);
        subject.setAcademicYear(1);

        document = new Document();
        document.setId(1L);
        document.setTitle("Exame de Época Normal");
        document.setDescription("Exame resolvido");
        document.setSubject(subject);
        document.setUser(studentUser);
        document.setDocumentType(DocumentType.NORMAL_EXAM);
        document.setAcademicYear(2026);
        document.setFileUrl("https://pub.r2.dev/documents/file.pdf");
        document.setStatus(PENDING);
        document.setHashSha256("d41d8cd98f00b204e9800998ecf8427e");
    }

    @Nested
    @DisplayName("Testes de Leitura / Consultas (GET)")
    class GetMethodsTests {

        @Test
        @DisplayName("Deve retornar todos os documentos paginados")
        void getAllShouldReturnPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findAll(pageable)).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Exame de Época Normal",result.getContent().getFirst().title());
            verify(documentRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Deve buscar documento por ID com sucesso")
        void getByIdWhenExistsShouldReturnDocument() {
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            DocumentResponseDto response = documentService.getById(1L);

            assertAll(
                    ()->assertNotNull(response),
                    ()->assertEquals("Exame de Época Normal", response.title()),
                    ()->assertEquals("Engenharia Informática",response.courseName()),
                    ()->assertEquals("Programação I",response.subjectName()),
                    ()->assertEquals(PENDING.name(),response.status())
            );
            verify(documentRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar NotFoundException quando documento não existir pelo ID")
        void getByIdWhenNotExistsShouldThrowNotFoundException() {
            when(documentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> documentService.getById(99L));
        }

        @Test
        @DisplayName("Deve buscar documentos por instituição")
        void getByInstitution_ShouldReturnDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(institutionRepository.findById(10L)).thenReturn(Optional.of(institution));
            when(documentRepository.findByInstitutionId(eq(10L), any(Pageable.class))).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByInstitution(institution.getId(), pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            verify(documentRepository).findByInstitutionId(10L, pageable);
        }
        @Test
        @DisplayName("Deve retornar uma lista vazia quando não forem encontrados documentos para uma instituição específica")
        void shouldReturnAnEmptyPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of());
            when(institutionRepository.findById(20L)).thenReturn(Optional.of(institutionForFalseTest));
            when(documentRepository.findByInstitutionId(eq(20L), any(Pageable.class))).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByInstitution(20L, pageable);

            assertNotNull(result);
            assertEquals(0, result.getContent().size());
            assertTrue(result.getContent().isEmpty());
            verify(documentRepository).findByInstitutionId(20L, pageable);
        }

        @Test
        @DisplayName("Deve lançar uma exceção quando o id fornecido para buscar os documentos de uma instituição for inválido.")
        void shouldThrowsNotFoundExceptionWhenTheInstitutionWasNotFound(){
            when(institutionRepository.findById(30L)).thenReturn(Optional.empty());

            NotFoundException notFoundException=assertThrows(NotFoundException.class, () -> documentService.getByInstitution(30L, pageable));
            assertEquals("Institution not found with id: 30",notFoundException.getMessage());
            assertTrue(notFoundException.getMessage().contains("Institution not found with id: 30"));
            verifyNoInteractions(documentRepository);
        }

        @Test
        @DisplayName("Deve buscar documentos aprovados")
        void getApprovedDocuments_ShouldReturnApprovedOnly() {
            document.setStatus(DocumentStatus.APPROVED);
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByStatus(eq(DocumentStatus.APPROVED), any(Pageable.class))).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getApprovedDocuments(pageable);

            assertNotNull(result);
            assertTrue(result.getContent().getFirst().title().contains(document.getTitle()));
            assertEquals("APPROVED", result.getContent().getFirst().status());
            verify(documentRepository).findByStatus(eq(DocumentStatus.APPROVED), any(Pageable.class));
        }
        @Test
        @DisplayName("Deve buscar documentos aprovados")
        void getPendingDocuments_ShouldReturnApprovedOnly() {
            document.setStatus(PENDING);
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByStatus(eq(PENDING), any(Pageable.class))).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getPendingDocuments(pageable);

            assertNotNull(result);
            assertTrue(result.getContent().getFirst().title().contains(document.getTitle()));
            assertEquals("PENDING", result.getContent().getFirst().status());
            verify(documentRepository).findByStatus(eq(PENDING), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar documentos cujo o título ou descrição inclui o termo de pesquisa")
        void shouldReturnDocumentosWithTitleOrDescriptioContaingTheParamSearch(){
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByTitleOrDescriptionContainingIgnoreCase(eq("Exame"),eq("Exame"),any(Pageable.class))).thenReturn(page);

            PagedModel<DocumentResponseDto> response=documentService.getByTitleOrDescriptionContaining("Exame",pageable);
            assertNotNull(response);
            assertTrue(response.getContent().getFirst().title().contains("Exame"));
            assertEquals("Exame de Época Normal", response.getContent().getFirst().title());
            assertEquals("Programação I",response.getContent().getFirst().subjectName());
            verify(documentRepository).findByTitleOrDescriptionContainingIgnoreCase("Exame","Exame",pageable);
        }
    }

    @Nested
    @DisplayName("Testes de Submissão de Documento (Upload)")
    class UploadTests {

        @Test
        @DisplayName("Deve realizar o upload do documento com sucesso")
        void uploadDocument_Success() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file", "teste.pdf", "application/pdf", "Conteúdo do Ficheiro".getBytes()
            );

            DocumentUploadDto uploadDto = new DocumentUploadDto(
                    "Algoritmos",
                    "Resumo da matéria",
                    100L,
                    DocumentType.SUMMARY,
                    2026,
                    "Prof. Silva",
                    true,
                    mockFile
            );

            when(documentRepository.existsByHashSha256(anyString())).thenReturn(false);
            when(subjectRepository.findById(100L)).thenReturn(Optional.of(subject));
            when(storageService.uploadFile(mockFile)).thenReturn("https://pub.r2.dev/documents/uuid_teste.pdf");
            when(documentRepository.save(any(Document.class))).thenReturn(document);

            DocumentResponseDto response = documentService.uploadDocument(uploadDto, studentUser);

            assertNotNull(response);
            verify(documentRepository).existsByHashSha256(anyString());
            verify(storageService).uploadFile(mockFile);
            verify(documentRepository).save(any(Document.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando o ficheiro enviado for vazio")
        void uploadDocument_EmptyFile_ShouldThrowException() {
            MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/plain", new byte[0]);

            DocumentUploadDto uploadDto = new DocumentUploadDto(
                    "Título", "Desc", 100L, DocumentType.EXERCISE, 2026, "Prof", false, emptyFile
            );

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> documentService.uploadDocument(uploadDto, studentUser));

            assertEquals("The document file cannot be empty", ex.getMessage());
            verifyNoInteractions(storageService);
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando o hash do ficheiro já existir (RN02)")
        void uploadDocument_DuplicateFile_ShouldThrowException() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file", "duplicado.pdf", "application/pdf", "Ficheiro Repetido".getBytes()
            );

            DocumentUploadDto uploadDto = new DocumentUploadDto(
                    "Título", "Desc", 100L, DocumentType.EXERCISE, 2026, "Prof", false, mockFile
            );

            when(documentRepository.existsByHashSha256(anyString())).thenReturn(true);

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> documentService.uploadDocument(uploadDto, studentUser));

            assertTrue(ex.getMessage().contains("The document file already exists(Duplicated file was found"));
            verify(storageService, never()).uploadFile(any());
        }
    }

    @Nested
    @DisplayName("Testes de Edição, Estado e Eliminação")
    class MutationTests {

        @Test
        @DisplayName("Deve permitir ao proprietário atualizar os metadados do documento")
        void updateDocument_AsOwner_Success() {
            DocumentUpdateDto updateDto = new DocumentUpdateDto(
                    "Novo Título", "Nova Descrição", "Prof. Santos", 2026, true
            );

            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentResponseDto response = documentService.updateDocument(1L, updateDto, studentUser);

            assertEquals("Novo Título", response.title());
            assertEquals("Nova Descrição", response.description());
            verify(documentRepository).save(document);
        }

        @Test
        @DisplayName("Deve lançar exceção se outro utilizador tentar atualizar documento que não lhe pertence")
        void updateDocument_AsOtherUser_ShouldThrowException() {
            User unauthorizedUser = new User();
            unauthorizedUser.setId(99L);
            unauthorizedUser.setRole(Role.STUDENT);

            DocumentUpdateDto updateDto = new DocumentUpdateDto(
                    "Novo Título", null, null, null, null
            );

            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            assertThrows(BusinessRuleException.class,
                    () -> documentService.updateDocument(1L, updateDto, unauthorizedUser));

            verify(documentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve atualizar o estado do documento (Ex: aprovação pela moderação)")
        void updateDocumentStatus_Success() {
            DocumentStatusUpdateDto statusUpdateDto=new DocumentStatusUpdateDto(DocumentStatus.APPROVED);
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentResponseDto response = documentService.updateDocumentStatus(1L, statusUpdateDto, adminUser);

            assertEquals("APPROVED", response.status());
            verify(documentRepository).save(document);
        }

        @Test
        @DisplayName("Deve eliminar o documento e remover o ficheiro do R2")
        void delete_AsAdmin_Success() {
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            documentService.delete(1L, adminUser);

            verify(storageService).deleteFile(document.getFileUrl());
            verify(documentRepository).delete(document);
        }
    }
    @Nested
    @DisplayName("Testes Complementares de Consultas Paginadas")
    class AdditionalQueriesTests {

        @Test
        @DisplayName("Deve buscar documentos por título")
        void searchByTitle_ShouldReturnPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByTitleContainingIgnoreCase(eq("Exame"), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.searchByTitle("Exame", pageable);

            assertNotNull(result);
            verify(documentRepository).findByTitleContainingIgnoreCase("Exame", pageable);
        }

        @Test
        @DisplayName("Deve buscar documentos por faculdade")
        void getByFaculty_ShouldReturnPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(facultyRepository.findById(20L)).thenReturn(Optional.of(faculty));
            when(documentRepository.findByFacultyId(eq(20L), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByFaculty(20L, pageable);

            assertNotNull(result);
            verify(documentRepository).findByFacultyId(20L, pageable);
        }

        @Test
        @DisplayName("Deve buscar documentos por curso")
        void getByCourse_ShouldReturnPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(courseRepository.findById(30L)).thenReturn(Optional.of(course));
            when(documentRepository.findByCourseId(eq(30L), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByCourse(30L, pageable);

            assertNotNull(result);
            verify(documentRepository).findByCourseId(30L, pageable);
        }

        @Test
        @DisplayName("Deve buscar documentos pendentes de moderação")
        void getPendingDocuments_ShouldReturnPendingOnly() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByStatus(eq(PENDING), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getPendingDocuments(pageable);

            assertNotNull(result);
            assertEquals("PENDING", result.getContent().get(0).status());
            verify(documentRepository).findByStatus(PENDING, pageable);
        }

        @Test
        @DisplayName("Deve buscar documentos por utilizador/uploader")
        void getByUploader_ShouldReturnUserDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findAllByUserId(eq(1L), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByUploader(1L, pageable);

            assertNotNull(result);
            verify(documentRepository).findAllByUserId(1L, pageable);
        }

        @Test
        @DisplayName("Deve buscar documentos por tópico (Título ou Descrição)")
        void getByTopic_ShouldReturnPagedDocuments() {
            Page<Document> page = new PageImpl<>(List.of(document));
            when(documentRepository.findByTitleOrDescriptionContainingIgnoreCase(eq("Java"), eq("Java"), any())).thenReturn(page);

            PagedModel<DocumentResponseDto> result = documentService.getByTitleOrDescriptionContaining("Java", pageable);

            assertNotNull(result);
            verify(documentRepository).findByTitleOrDescriptionContainingIgnoreCase("Java", "Java", pageable);
        }
    }

    @Nested
    @DisplayName("Testes de Casos Limite e Erros de I/O")
    class EdgeCasesAndBranchingTests {

        @Test
        @DisplayName("Deve manter os valores originais ao atualizar com DTO contendo campos nulos")
        void updateDocument_WithNullFields_ShouldKeepOriginalValues() {
            DocumentUpdateDto nullUpdateDto = new DocumentUpdateDto(null, null, null, null, null);

            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentResponseDto response = documentService.updateDocument(1L, nullUpdateDto, studentUser);

            // Garante que o título original não virou null
            assertEquals("Exame de Época Normal", response.title());
            assertEquals("Exame resolvido", response.description());
            verify(documentRepository).save(document);
        }

        @Test
        @DisplayName("Deve permitir ao proprietário (STUDENT) eliminar o seu próprio documento")
        void delete_AsOwner_Success() {
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            documentService.delete(1L, studentUser);

            verify(storageService).deleteFile(document.getFileUrl());
            verify(documentRepository).delete(document);
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException se ocorrer erro de I/O na leitura do ficheiro para Hash")
        void uploadDocument_IOException_ShouldThrowBusinessRuleException() throws IOException {
            MultipartFile faultyFile = mock(MultipartFile.class);
            when(faultyFile.isEmpty()).thenReturn(false);
            when(faultyFile.getInputStream()).thenThrow(new IOException("Erro de leitura no disco"));

            DocumentUploadDto uploadDto = new DocumentUploadDto(
                    "Título", "Desc", 100L, DocumentType.EXERCISE, 2026, "Prof", false, faultyFile
            );

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> documentService.uploadDocument(uploadDto, studentUser));

            assertTrue(ex.getMessage().contains("Failed to calculate file hash SHA-256"));
            verify(storageService, never()).uploadFile(any());
        }
    }

}