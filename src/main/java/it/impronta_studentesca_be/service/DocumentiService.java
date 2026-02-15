package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.DocumentoLinkResponseDTO;

import java.util.List;

public interface DocumentiService {

    DocumentoLinkResponseDTO creaPdf(byte[] pdfBytes, String fileName);

    DocumentoLinkResponseDTO getLinkById(Long id);

    List<DocumentoLinkResponseDTO> listAll();

    DocumentoLinkResponseDTO exportStaffExcel();

    void marcaExcelAssociatiDaModificare();
}
