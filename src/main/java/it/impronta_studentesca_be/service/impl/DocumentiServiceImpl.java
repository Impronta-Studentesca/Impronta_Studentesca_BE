package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.TipoCorso;
import it.impronta_studentesca_be.dto.DocumentoLinkResponseDTO;
import it.impronta_studentesca_be.dto.DriveFileInfo;
import it.impronta_studentesca_be.dto.record.StaffExportRow;
import it.impronta_studentesca_be.entity.FileDocumentale;
import it.impronta_studentesca_be.repository.FileDocumentaleRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.DocumentiService;
import it.impronta_studentesca_be.service.GoogleDriveStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentiServiceImpl implements DocumentiService {

    @Autowired
    private GoogleDriveStorageService googleDriveStorageService;

    @Autowired
    private FileDocumentaleRepository fileRepository;

    @Autowired
    PersonaRepository personaRepository;

    private static final String STAFF_XLSX_NAME = "ASSOCIATI IMPRONTA.xlsx";
    private static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    public DocumentoLinkResponseDTO creaPdf(byte[] pdfBytes, String fileName) {
        log.info("INIZIO CREA PDF - FILE_NAME={} - BYTES={}", fileName, pdfBytes != null ? pdfBytes.length : null);

        DriveFileInfo info = googleDriveStorageService.uploadToFolder(pdfBytes, fileName, "application/pdf");
        log.info("UPLOAD PDF SU DRIVE OK - FILE_ID={} - NAME={}", info.getFileId(), info.getName());

        FileDocumentale entity = FileDocumentale.builder()
                .nome(info.getName())
                .mimeType(info.getMimeType())
                .driveFileId(info.getFileId())
                .webViewLink(info.getWebViewLink())
                .webContentLink(info.getWebContentLink())
                .size(info.getSize())
                .md5Checksum(info.getMd5Checksum())
                .daModificare(false)
                .build();

        FileDocumentale saved = fileRepository.save(entity);
        log.info("SALVATAGGIO METADATI PDF SU DB OK - ID={} - DRIVE_FILE_ID={}", saved.getId(), saved.getDriveFileId());

        log.info("FINE CREA PDF - OK");
        return toDto(saved);
    }

    @Override
    public DocumentoLinkResponseDTO exportStaffExcel() {
        log.info("INIZIO EXPORT STAFF EXCEL - FILE_NAME={}", STAFF_XLSX_NAME);

        // ✅ Se esiste e NON è da modificare: ritorna link senza rigenerare
        var existingOpt = fileRepository.findFirstByNomeAndMimeTypeOrderByUpdatedAtDescCreatedAtDesc(STAFF_XLSX_NAME, XLSX_MIME);
        if (existingOpt.isPresent()) {
            log.info("EXCEL ESISTENTE TROVATO - ID={} - DA_MODIFICARE={}",
                    existingOpt.get().getId(),
                    existingOpt.get().isDaModificare());

            if (!existingOpt.get().isDaModificare()) {
                log.info("CACHE HIT EXCEL - NON RIGENERO - RITORNO LINK");
                return toDto(existingOpt.get());
            }

            log.info("EXCEL DA RIGENERARE - DA_MODIFICARE=TRUE");
        } else {
            log.info("NESSUN EXCEL ESISTENTE - CREAZIONE NUOVO");
        }

        // ✅ Altrimenti rigenera: elimina precedente (DB + Drive) e carica nuovo
        log.info("INIZIO CLEANUP EXCEL PRECEDENTE - FILE_NAME={} - MIME={}", STAFF_XLSX_NAME, XLSX_MIME);
        cleanupByNameAndMime(STAFF_XLSX_NAME, XLSX_MIME);
        log.info("FINE CLEANUP EXCEL PRECEDENTE - OK");

        log.info("RECUPERO RIGHE STAFF DAL DB");
        List<StaffExportRow> rows = personaRepository.exportStaffRows();
        log.info("RIGHE STAFF RECUPERATE - COUNT={}", rows != null ? rows.size() : null);

        String[] headers = {
                "DIPARTIMENTO",
                "CORSO DI STUDI",
                "TIPO CORSO",
                "NOME",
                "COGNOME",
                "ANNO DI CORSO"
        };

        log.info("INIZIO CREAZIONE BYTES EXCEL");
        byte[] xlsxBytes = buildStaffExcelBytes(rows, headers);
        log.info("FINE CREAZIONE BYTES EXCEL - BYTES={}", xlsxBytes != null ? xlsxBytes.length : null);

        log.info("UPLOAD EXCEL SU DRIVE - FILE_NAME={}", STAFF_XLSX_NAME);
        DriveFileInfo info = googleDriveStorageService.uploadToFolder(
                xlsxBytes,
                STAFF_XLSX_NAME,
                XLSX_MIME
        );
        log.info("UPLOAD EXCEL SU DRIVE OK - FILE_ID={} - NAME={}", info.getFileId(), info.getName());

        FileDocumentale entity = FileDocumentale.builder()
                .nome(info.getName())
                .mimeType(info.getMimeType())
                .driveFileId(info.getFileId())
                .webViewLink(info.getWebViewLink())
                .webContentLink(info.getWebContentLink())
                .size(info.getSize())
                .md5Checksum(info.getMd5Checksum())
                .daModificare(false)
                .build();

        FileDocumentale saved = fileRepository.save(entity);
        log.info("SALVATAGGIO METADATI EXCEL SU DB OK - ID={} - DRIVE_FILE_ID={}", saved.getId(), saved.getDriveFileId());

        log.info("FINE EXPORT STAFF EXCEL - OK");
        return toDto(saved);
    }

    private void cleanupByNameAndMime(String fileName, String mimeType) {

        // 1) DB: cancella tutti i record che matchano (così resti pulito)
        List<FileDocumentale> old = fileRepository.findAllByNomeAndMimeType(fileName, mimeType);
        log.info("CLEANUP DB - RECORD TROVATI - COUNT={} - FILE_NAME={} - MIME={}",
                old != null ? old.size() : null, fileName, mimeType);

        if (old != null) {
            for (FileDocumentale d : old) {
                log.info("CLEANUP RECORD - DB_ID={} - DRIVE_FILE_ID={}", d.getId(), d.getDriveFileId());

                try {
                    googleDriveStorageService.delete(d.getDriveFileId());
                    log.info("CLEANUP DRIVE OK - DRIVE_FILE_ID={}", d.getDriveFileId());
                } catch (Exception ignore) {
                    log.warn("CLEANUP DRIVE FALLITO (BEST EFFORT) - DRIVE_FILE_ID={}", d.getDriveFileId());
                }

                fileRepository.delete(d);
                log.info("CLEANUP DB OK - DB_ID={}", d.getId());
            }
        }

        // 2) Drive: cancella eventuali duplicati rimasti per nome nella cartella
        try {
            int deleted = googleDriveStorageService.deleteAllByNameInFolder(fileName);
            log.info("CLEANUP DRIVE PER NOME OK - FILE_NAME={} - DELETED={}", fileName, deleted);
        } catch (Exception ignore) {
            log.warn("CLEANUP DRIVE PER NOME FALLITO (BEST EFFORT) - FILE_NAME={}", fileName);
        }
    }

    private byte[] buildStaffExcelBytes(List<StaffExportRow> rows, String[] headers) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            log.info("INIZIO BUILD EXCEL - SHEET=Staff - ROWS={}", rows != null ? rows.size() : null);

            XSSFSheet sheet = wb.createSheet("Staff");

            // Header style
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int r = 1;
            if (rows != null) {
                for (StaffExportRow x : rows) {
                    Row row = sheet.createRow(r++);

                    row.createCell(0).setCellValue(x.dipartimentoCodice());
                    row.createCell(1).setCellValue(x.corsoDiStudiNome());
                    row.createCell(2).setCellValue(
                            x.tipoCorso() != null ? TipoCorso.valueOf(x.tipoCorso()).getValue() : ""
                    );
                    row.createCell(3).setCellValue(x.nome());
                    row.createCell(4).setCellValue(x.cognome());

                    Cell annoCell = row.createCell(5);
                    if (x.annoDiCorso() != null) annoCell.setCellValue(x.annoDiCorso());
                    else annoCell.setBlank();
                }
            }

            // Freeze header
            sheet.createFreezePane(0, 1);

            // Auto-size colonne
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Filtri + Tabella
            int lastRow = Math.max(0, rows != null ? rows.size() : 0);
            sheet.setAutoFilter(new CellRangeAddress(0, lastRow, 0, headers.length - 1));

            if (rows != null && !rows.isEmpty()) {
                AreaReference area = new AreaReference(
                        new CellReference(0, 0),
                        new CellReference(lastRow, headers.length - 1),
                        SpreadsheetVersion.EXCEL2007
                );

                XSSFTable table = sheet.createTable(area);
                table.setName("StaffTable");
                table.setDisplayName("StaffTable");

                CTTable ct = table.getCTTable();
                ct.setRef(area.formatAsString());

// ✅ AutoFilter: crea o riusa
                CTAutoFilter af = ct.getAutoFilter();
                if (af == null) af = ct.addNewAutoFilter();
                af.setRef(area.formatAsString());

// ✅ Colonne: crea o riusa
                CTTableColumns cols = ct.getTableColumns();
                if (cols == null) cols = ct.addNewTableColumns();

                cols.setCount(headers.length);

// reset colonne (evita xml incoerente)
                int existing = cols.sizeOfTableColumnArray();
                for (int i = existing - 1; i >= 0; i--) {
                    cols.removeTableColumn(i); // se non compila, vedi nota sotto
                }

                for (int i = 0; i < headers.length; i++) {
                    CTTableColumn col = cols.addNewTableColumn();
                    col.setId(i + 1);
                    col.setName(headers[i]);
                }

// ✅ Stile: crea o riusa
                CTTableStyleInfo style = ct.getTableStyleInfo();
                if (style == null) style = ct.addNewTableStyleInfo();

                style.setName("TableStyleMedium9");
                style.setShowRowStripes(true);


                log.info("TABELLA EXCEL CREATA - AREA={}", area.formatAsString());
            } else {
                log.info("NESSUNA RIGA - TABELLA EXCEL NON CREATA");
            }

            wb.write(out);
            log.info("FINE BUILD EXCEL - OK");

            return out.toByteArray();

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE EXCEL STAFF", e);
            throw new RuntimeException("Errore creazione Excel staff", e);
        }
    }

    @Override
    public DocumentoLinkResponseDTO getLinkById(Long id) {
        log.info("INIZIO GET LINK DOCUMENTO - ID={}", id);

        FileDocumentale doc = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento non trovato: " + id));

        log.info("FINE GET LINK DOCUMENTO - OK - ID={} - DRIVE_FILE_ID={}", doc.getId(), doc.getDriveFileId());
        return toDto(doc);
    }

    @Override
    public List<DocumentoLinkResponseDTO> listAll() {
        log.info("INIZIO LIST ALL DOCUMENTI");

        List<DocumentoLinkResponseDTO> list = fileRepository.findAll().stream().map(this::toDto).toList();
        log.info("FINE LIST ALL DOCUMENTI - COUNT={}", list.size());

        return list;
    }

    private DocumentoLinkResponseDTO toDto(FileDocumentale d) {
        String fileId = d.getDriveFileId();
        String downloadDirect =
                "https://drive.google.com/uc?export=download&id=" +
                        URLEncoder.encode(fileId, StandardCharsets.UTF_8);

        return DocumentoLinkResponseDTO.builder()
                .id(d.getId())
                .nome(d.getNome())
                .mimeType(d.getMimeType())
                .webViewLink(d.getWebViewLink())
                .webContentLink(d.getWebContentLink())
                .downloadDirectUrl(downloadDirect)
                .daModificare(d.isDaModificare())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    @Transactional
    public void marcaExcelAssociatiDaModificare() {
        fileRepository.markDaModificareByNomeAndMimeType(
                "ASSOCIATI IMPRONTA.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }
}
