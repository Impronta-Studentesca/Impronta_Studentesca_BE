package it.impronta_studentesca_be.dto.record;


public record StaffExportRow(
        String dipartimentoCodice,
        String corsoDiStudiNome,
        String tipoCorso,
        Integer annoDiCorso,
        String nome,
        String cognome
) {}
