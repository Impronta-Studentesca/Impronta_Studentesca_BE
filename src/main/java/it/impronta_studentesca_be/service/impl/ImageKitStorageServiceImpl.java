package it.impronta_studentesca_be.service.impl;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import it.impronta_studentesca_be.dto.ImageUploadResponseDTO;
import it.impronta_studentesca_be.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageKitStorageServiceImpl implements ImageStorageService {

    private final ImageKit imageKit;

    @Value("${impronta.imagekit.persona-folder:/impronta/persone}")
    private String personaFolder;

    @Override
    public ImageUploadResponseDTO uploadPersonaPhoto(Long personaId, MultipartFile file) {

        log.info("INIZIO UPLOAD IMAGEKIT. PERSONA_ID={}", personaId);

        if (personaId == null) {
            log.error("UPLOAD IMAGEKIT FALLITO: PERSONA_ID NULL");
            throw new IllegalArgumentException("personaId non valido");
        }

        if (file == null || file.isEmpty()) {
            log.warn("UPLOAD IMAGEKIT BLOCCATO: FILE NULL O VUOTO. PERSONA_ID={}", personaId);
            throw new IllegalArgumentException("File immagine mancante o vuoto");
        }

        try {
            // Nome file “pulito”
            String originalName;
            try {
                originalName = file.getOriginalFilename();
            } catch (Exception e) {
                log.error("ERRORE LETTURA ORIGINAL FILENAME. PERSONA_ID={}", personaId, e);
                originalName = null;
            }

            String safeName = (originalName != null && !originalName.isBlank())
                    ? originalName
                    : "persona-" + personaId + ".jpg";

            log.info("PREPARAZIONE FILE UPLOAD. PERSONA_ID={}, SAFE_NAME={}, SIZE_BYTES={}",
                    personaId, safeName, file.getSize());

            // Contenuto in base64 (come vuole l’SDK)
            String base64;
            try {
                base64 = Base64.getEncoder().encodeToString(file.getBytes());
            } catch (Exception e) {
                log.error("ERRORE LETTURA BYTES/BASE64. PERSONA_ID={}, SAFE_NAME={}", personaId, safeName, e);
                throw new RuntimeException("Errore lettura file immagine", e);
            }

            FileCreateRequest request;
            try {
                request = new FileCreateRequest(base64, safeName);
                request.setFolder(personaFolder);
                request.setUseUniqueFileName(true);
            } catch (Exception e) {
                log.error("ERRORE COSTRUZIONE REQUEST IMAGEKIT. PERSONA_ID={}, SAFE_NAME={}", personaId, safeName, e);
                throw new RuntimeException("Errore preparazione richiesta ImageKit", e);
            }

            Result result;
            try {
                log.info("CHIAMATA IMAGEKIT UPLOAD. PERSONA_ID={}, FOLDER={}", personaId, personaFolder);
                result = imageKit.upload(request);
            } catch (Exception e) {
                log.error("ERRORE CHIAMATA IMAGEKIT UPLOAD. PERSONA_ID={}, SAFE_NAME={}", personaId, safeName, e);
                throw new RuntimeException("Errore upload immagine su ImageKit", e);
            }

            if (result == null || result.getFileId() == null || result.getUrl() == null) {
                log.error("RISPOSTA IMAGEKIT NON VALIDA. PERSONA_ID={}, RESULT={}", personaId, result);
                throw new RuntimeException("Risposta ImageKit non valida");
            }

            ImageUploadResponseDTO dto = new ImageUploadResponseDTO();
            dto.setFileId(result.getFileId());
            dto.setUrl(result.getUrl());
            dto.setThumbnail(result.getThumbnail());
            dto.setWidth(result.getWidth());
            dto.setHeight(result.getHeight());

            log.info("UPLOAD IMAGEKIT COMPLETATO. PERSONA_ID={}, FILE_ID={}, URL={}",
                    personaId, result.getFileId(), result.getUrl());

            return dto;

        } catch (IllegalArgumentException e) {
            log.warn("UPLOAD IMAGEKIT FALLITO PER INPUT NON VALIDO. PERSONA_ID={}. MSG={}", personaId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("UPLOAD IMAGEKIT FALLITO. PERSONA_ID={}", personaId, e);
            throw new RuntimeException("Errore upload immagine", e);
        }
    }




}
