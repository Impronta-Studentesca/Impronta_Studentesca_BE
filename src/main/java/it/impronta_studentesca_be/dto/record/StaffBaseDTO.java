package it.impronta_studentesca_be.dto.record;

import it.impronta_studentesca_be.constant.TipoCorso;

public record StaffBaseDTO(
        Long id,
        String nome,
        String cognome,
        String matricola,
        String numeroTelefono,
        String email,
        String mailUnipa,
        Long corsoId,
        String corsoNome,
        TipoCorso tipoCorso,
        Long dipartimentoId,
        String dipartimentoNome,
        String dipartimentoCodice,
        Integer annoCorso,
        String fotoUrl,
        String fotoThumbnailUrl
) {}
