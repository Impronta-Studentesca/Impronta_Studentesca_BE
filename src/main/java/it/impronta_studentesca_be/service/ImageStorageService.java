package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.ImageUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    /**
     * Carica la foto di una persona su ImageKit e ritorna i dati dell'immagine caricata.
     */
    ImageUploadResponseDTO uploadPersonaPhoto(Long personaId, MultipartFile file);

    void deleteFileById(String fileId);

/**
 * IL DOWNLOAD VERRA' FATTO DIRETTAMENTE NEL FE CON FORO_URL
 */

}