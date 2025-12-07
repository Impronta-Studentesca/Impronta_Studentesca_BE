package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PersonaDirettivoRequestDTO {

    private Long personaId;

    private Long direttivoId;

    /**
     * Ruolo ricoperto da questa persona all'interno del direttivo,
     * es. "Presidente", "Vicepresidente", "Tesoriere"...
     */
    private String ruoloNelDirettivo;
}
