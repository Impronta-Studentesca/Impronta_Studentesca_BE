package it.impronta_studentesca_be.dto.record;

public record PersonaDirettivoMiniDTO(
        Long direttivoId,
        Long personaId,
        String nome,
        String cognome,
        String ruoloNelDirettivo
) {}