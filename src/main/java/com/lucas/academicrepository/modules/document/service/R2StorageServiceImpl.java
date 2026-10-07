package com.lucas.academicrepository.modules.document.service;

import com.lucas.academicrepository.config.storage.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class R2StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    public R2StorageServiceImpl(S3Client s3Client, StorageProperties storageProperties) {
        this.s3Client = s3Client;
        this.storageProperties = storageProperties;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String destinationFileName = "documents/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(destinationFileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Retorna a URL pública ou o caminho do ficheiro no R2
            return String.format("%s/%s", storageProperties.getPublicUrl(), destinationFileName);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar e carregar o ficheiro para o Cloudflare R2", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        // Extrai a chave relativa caso receba a URL pública completa
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(storageProperties.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String extractKeyFromUrl(String fileUrlOrKey) {
        String publicUrl = storageProperties.getPublicUrl();
        if (fileUrlOrKey.startsWith(publicUrl)) {
            return fileUrlOrKey.substring(publicUrl.length() + 1);
        }
        return fileUrlOrKey;
    }
}