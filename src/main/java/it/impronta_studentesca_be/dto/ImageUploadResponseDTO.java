package it.impronta_studentesca_be.dto;

import lombok.Data;

@Data
public class ImageUploadResponseDTO {

    private String fileId;        // id interno ImageKit
    private String url;           // url CDN da salvare in persona.fotoUrl
    private String thumbnail;  // opzionale, se lo usi
    private Integer width;        // opzionale
    private Integer height;       // opzionale
}