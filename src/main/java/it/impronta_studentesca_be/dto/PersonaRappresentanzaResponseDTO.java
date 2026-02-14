package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaRappresentanzaResponseDTO {

    private Long id;

    private Long personaId;

    private Long organoId;

    private LocalDate dataInizio;

    private LocalDate dataFine;


    // Costruttore che prende l'entità
    public PersonaRappresentanzaResponseDTO(PersonaRappresentanza entity) {
        this.id = entity.getId();
        this.personaId = entity.getPersona() != null
                ? entity.getPersona().getId()
                : null;
        this.organoId = entity.getOrganoRappresentanza() != null
                ? entity.getOrganoRappresentanza().getId()
                : null;
        this.dataInizio = entity.getDataInizio();
        this.dataFine = entity.getDataFine();

    }



}
