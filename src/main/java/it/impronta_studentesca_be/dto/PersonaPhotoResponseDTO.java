package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonaPhotoResponseDTO {

    private Long personaId;
    private String fotoUrl;
}