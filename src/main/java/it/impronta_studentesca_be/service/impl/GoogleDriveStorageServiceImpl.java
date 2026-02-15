package it.impronta_studentesca_be.service.impl;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import it.impronta_studentesca_be.dto.DriveFileInfo;
import it.impronta_studentesca_be.service.GoogleDriveStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveStorageServiceImpl implements GoogleDriveStorageService {

    private final Drive drive;

    @Value("${google.drive.share-anyone-with-link:false}")
    private boolean shareAnyoneWithLink;

    @Value("${google.drive.share-emails:}")
    private String shareEmailsCsv;

    @Value("${google.drive.folder-id}")
    private String folderId;

    /**
     * METTI TRUE SE LA CARTELLA È IN UNO SHARED DRIVE
     * (puoi anche lasciarlo false se non ti serve)
     */
    @Value("${google.drive.supports-all-drives:false}")
    private boolean supportsAllDrives;

    @Override
    public DriveFileInfo uploadToFolder(byte[] content, String filename, String mimeType) {
        try {
            log.info("UPLOAD DRIVE START - FILE_NAME={} MIME_TYPE={} FOLDER_ID={} SHARE_ANYONE_WITH_LINK={} SHARE_EMAILS_CONFIGURED={}",
                    filename, mimeType, folderId, shareAnyoneWithLink, hasShareEmails());

            File meta = new File();
            meta.setName(filename);
            meta.setParents(List.of(folderId));

            ByteArrayContent media = new ByteArrayContent(mimeType, content);

            var createReq = drive.files()
                    .create(meta, media)
                    .setFields("id,name,mimeType,webViewLink,webContentLink,size,md5Checksum");

            if (supportsAllDrives) {
                createReq.setSupportsAllDrives(true);
            }

            File created = createReq.execute();

            log.info("UPLOAD DRIVE OK - FILE_ID={} FILE_NAME={}", created.getId(), created.getName());

            if (shareAnyoneWithLink) {
                log.info("DRIVE PERMISSION - GRANT ANYONE READER - FILE_ID={}", created.getId());
                grantAnyoneReader(created.getId());
            } else if (hasShareEmails()) {
                for (String email : parseEmails(shareEmailsCsv)) {
                    log.info("DRIVE PERMISSION - GRANT USER READER - FILE_ID={} EMAIL={}", created.getId(), email);
                    grantUserReader(created.getId(), email);
                }
            } else {
                log.info("DRIVE PERMISSION - SKIP - NO SHARING CONFIGURED - FILE_ID={}", created.getId());
            }

            return DriveFileInfo.builder()
                    .fileId(created.getId())
                    .name(created.getName())
                    .mimeType(created.getMimeType())
                    .webViewLink(created.getWebViewLink())
                    .webContentLink(created.getWebContentLink())
                    .size(created.getSize())
                    .md5Checksum(created.getMd5Checksum())
                    .build();

        } catch (GoogleJsonResponseException e) {
            var details = e.getDetails();
            log.error("UPLOAD DRIVE FAIL - STATUS={} MESSAGE={} DETAILS={}",
                    e.getStatusCode(),
                    e.getMessage(),
                    details != null ? details.getMessage() : "NULL",
                    e);
            throw new RuntimeException("UPLOAD SU GOOGLE DRIVE FALLITO", e);

        } catch (Exception e) {
            log.error("UPLOAD DRIVE FAIL - GENERIC - {}", e.getMessage(), e);
            throw new RuntimeException("UPLOAD SU GOOGLE DRIVE FALLITO", e);
        }
    }

    @Override
    public void delete(String driveFileId) {
        try {
            log.info("DELETE DRIVE START - FILE_ID={}", driveFileId);

            var req = drive.files().delete(driveFileId);
            if (supportsAllDrives) {
                req.setSupportsAllDrives(true);
            }
            req.execute();

            log.info("DELETE DRIVE OK - FILE_ID={}", driveFileId);

        } catch (GoogleJsonResponseException e) {
            var details = e.getDetails();
            log.error("DELETE DRIVE FAIL - FILE_ID={} STATUS={} MESSAGE={} DETAILS={}",
                    driveFileId,
                    e.getStatusCode(),
                    e.getMessage(),
                    details != null ? details.getMessage() : "NULL",
                    e);
            throw new RuntimeException("DELETE SU GOOGLE DRIVE FALLITO", e);

        } catch (Exception e) {
            log.error("DELETE DRIVE FAIL - FILE_ID={} - {}", driveFileId, e.getMessage(), e);
            throw new RuntimeException("DELETE SU GOOGLE DRIVE FALLITO", e);
        }
    }

    private void grantAnyoneReader(String fileId) throws Exception {
        Permission p = new Permission()
                .setType("anyone")
                .setRole("reader");

        var req = drive.permissions()
                .create(fileId, p)
                .setFields("id");

        if (supportsAllDrives) {
            req.setSupportsAllDrives(true);
        }

        req.execute();
        log.info("DRIVE PERMISSION OK - ANYONE READER - FILE_ID={}", fileId);
    }

    private void grantUserReader(String fileId, String email) throws Exception {
        Permission p = new Permission()
                .setType("user")
                .setRole("reader")
                .setEmailAddress(email);

        var req = drive.permissions()
                .create(fileId, p)
                .setFields("id");

        if (supportsAllDrives) {
            req.setSupportsAllDrives(true);
        }

        req.execute();
        log.info("DRIVE PERMISSION OK - USER READER - FILE_ID={} EMAIL={}", fileId, email);
    }

    @Override
    public int deleteAllByNameInFolder(String filename) {
        try {
            log.info("DELETE DRIVE BY NAME START - FILE_NAME={} FOLDER_ID={}", filename, folderId);

            String safeName = filename.replace("'", "\\'");
            String q = "name = '" + safeName + "' and '" + folderId + "' in parents and trashed = false";

            var listReq = drive.files()
                    .list()
                    .setQ(q)
                    .setFields("files(id,name)");

            if (supportsAllDrives) {
                listReq.setSupportsAllDrives(true);
                listReq.setIncludeItemsFromAllDrives(true);
                listReq.setCorpora("allDrives");
            }

            FileList list = listReq.execute();

            int deleted = 0;
            if (list.getFiles() != null) {
                for (File f : list.getFiles()) {
                    log.info("DELETE DRIVE BY NAME - DELETING - FILE_ID={} FILE_NAME={}", f.getId(), f.getName());

                    var delReq = drive.files().delete(f.getId());
                    if (supportsAllDrives) {
                        delReq.setSupportsAllDrives(true);
                    }
                    delReq.execute();

                    deleted++;
                }
            }

            log.info("DELETE DRIVE BY NAME OK - FILE_NAME={} DELETED_COUNT={}", filename, deleted);
            return deleted;

        } catch (GoogleJsonResponseException e) {
            var details = e.getDetails();
            log.error("DELETE DRIVE BY NAME FAIL - FILE_NAME={} STATUS={} MESSAGE={} DETAILS={}",
                    filename,
                    e.getStatusCode(),
                    e.getMessage(),
                    details != null ? details.getMessage() : "NULL",
                    e);
            throw new RuntimeException("ERRORE ELIMINAZIONE FILE DRIVE PER NOME: " + filename, e);

        } catch (Exception e) {
            log.error("DELETE DRIVE BY NAME FAIL - FILE_NAME={} - {}", filename, e.getMessage(), e);
            throw new RuntimeException("ERRORE ELIMINAZIONE FILE DRIVE PER NOME: " + filename, e);
        }
    }

    private boolean hasShareEmails() {
        return shareEmailsCsv != null && !shareEmailsCsv.isBlank();
    }

    private static List<String> parseEmails(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
