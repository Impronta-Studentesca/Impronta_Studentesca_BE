package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_documentale")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FileDocumentale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "drive_file_id", nullable = false, unique = true)
    private String driveFileId;

    @Column(name = "web_view_link")
    private String webViewLink;

    @Column(name = "web_content_link")
    private String webContentLink;

    private Long size;

    @Column(name = "md5_checksum")
    private String md5Checksum;

    @Column(name = "da_modificare", nullable = false)
    private boolean daModificare;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

