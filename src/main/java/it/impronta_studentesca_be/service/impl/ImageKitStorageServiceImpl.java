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
        try {
            // Nome file “pulito”
            String originalName = file.getOriginalFilename();
            String safeName = (originalName != null && !originalName.isBlank())
                    ? originalName
                    : "persona-" + personaId + ".jpg";

            // Contenuto in base64 (come vuole l’SDK)
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());

            FileCreateRequest request = new FileCreateRequest(base64, safeName);
            request.setFolder(personaFolder);
            request.setUseUniqueFileName(true);

            Result result = imageKit.upload(request);

            ImageUploadResponseDTO dto = new ImageUploadResponseDTO();
            dto.setFileId(result.getFileId());
            dto.setUrl(result.getUrl());
            dto.setThumbnail(result.getThumbnail());
            dto.setWidth(result.getWidth());
            dto.setHeight(result.getHeight());

            log.info("Upload foto persona {} completato. fileId={}, url={}",
                    personaId, result.getFileId(), result.getUrl());

            return dto;

        } catch (Exception e) {
            log.error("Errore durante upload foto persona {} su ImageKit", personaId, e);
            throw new RuntimeException("Errore upload immagine", e);
        }
    }



}
