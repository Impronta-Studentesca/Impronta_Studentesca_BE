package it.impronta_studentesca_be.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentoLinkResponseDTO {
    private Long id;
    private String nome;
    private String mimeType;

    private String webViewLink;       // apri nel browser
    private String webContentLink;    // link diretto Google (se presente)
    private String downloadDirectUrl; // comodo per download: /uc?export=download&id=

    private boolean daModificare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
