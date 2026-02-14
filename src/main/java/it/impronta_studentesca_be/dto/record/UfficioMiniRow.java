package it.impronta_studentesca_be.dto.record;

public record UfficioMiniRow(
        Long ufficioId,
        String ufficioNome,
        Long responsabileId,
        String responsabileNome,
        String responsabileCognome
) {}
