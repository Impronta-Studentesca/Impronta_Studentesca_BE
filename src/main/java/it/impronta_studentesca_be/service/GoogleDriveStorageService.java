package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.DriveFileInfo;

public interface GoogleDriveStorageService {
    DriveFileInfo uploadToFolder(byte[] content, String filename, String mimeType);
    void delete(String driveFileId);
    int deleteAllByNameInFolder(String filename);
}
