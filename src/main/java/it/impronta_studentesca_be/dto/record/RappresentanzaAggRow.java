package it.impronta_studentesca_be.dto.record;

import java.time.LocalDate;

public record RappresentanzaAggRow(
        Long prId,
        Long personaId,
        String personaNome,
        String personaCognome,
        Long organoId,
        String organoCodice,
        String organoNome,
        LocalDate dataInizio,
        LocalDate dataFine
) {}
