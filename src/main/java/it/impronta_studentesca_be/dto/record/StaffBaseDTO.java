package it.impronta_studentesca_be.dto.record;

import it.impronta_studentesca_be.constant.TipoCorso;

public record StaffBaseDTO(
        Long id,
        String nome,
        String cognome,
        String email,
        Long corsoId,
        String corsoNome,
        TipoCorso tipoCorso,
        Integer annoCorso,
        String fotoUrl,
        String fotoThumbnailUrl
) {}
