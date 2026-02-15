package it.impronta_studentesca_be.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriveFileInfo {
    private String fileId;
    private String name;
    private String mimeType;
    private String webViewLink;
    private String webContentLink;
    private Long size;
    private String md5Checksum;
}