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
            String originalName = null;
            try {
                originalName = file.getOriginalFilename();
            } catch (Exception ex) {
                log.error("ERRORE LETTURA ORIGINAL FILENAME. PERSONA_ID={}", personaId, ex);
            }

            String safeName = (originalName != null && !originalName.isBlank())
                    ? originalName
                    : "persona-" + personaId + ".jpg";

            log.info("PREPARAZIONE FILE UPLOAD. PERSONA_ID={}, SAFE_NAME={}, SIZE_BYTES={}",
                    personaId, safeName, file.getSize());

            byte[] bytes;
            try {
                bytes = file.getBytes();
            } catch (Exception ex) {
                log.error("ERRORE LETTURA BYTES FILE. PERSONA_ID={}, SAFE_NAME={}", personaId, safeName, ex);
                throw new RuntimeException("Errore lettura file immagine", ex);
            }

            FileCreateRequest request = new FileCreateRequest(bytes, safeName);
            request.setFolder(personaFolder);
            request.setUseUniqueFileName(true);

            log.info("CHIAMATA IMAGEKIT UPLOAD. PERSONA_ID={}, FOLDER={}", personaId, personaFolder);

            Result result = imageKit.upload(request);

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

        } catch (IllegalArgumentException ex) {
            log.warn("UPLOAD IMAGEKIT FALLITO PER INPUT NON VALIDO. PERSONA_ID={}. MSG={}", personaId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("UPLOAD IMAGEKIT FALLITO. PERSONA_ID={}", personaId, ex);
            throw new RuntimeException("Errore upload immagine", ex);
        }
    }

    @Override
    public void deleteFileById(String fileId) {

        log.info("INIZIO DELETE IMAGEKIT. FILE_ID={}", fileId);

        if (fileId == null || fileId.isBlank()) {
            log.warn("DELETE IMAGEKIT SKIPPATO: FILE_ID NULL O VUOTO");
            return;
        }

        try {
            Result result = imageKit.deleteFile(fileId);

            log.info("DELETE IMAGEKIT COMPLETATO. FILE_ID={}, RESULT={}", fileId, result);

        } catch (Exception ex) {
            log.error("DELETE IMAGEKIT FALLITO. FILE_ID={}", fileId, ex);
            throw new RuntimeException("Errore delete immagine", ex);
        }
    }





}
