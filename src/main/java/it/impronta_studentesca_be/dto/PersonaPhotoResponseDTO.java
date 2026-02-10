package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaPhotoResponseDTO {

    private String url;
    private String thumbnailUrl;
    private String fileId;
    private Integer width;
    private Integer height;
}