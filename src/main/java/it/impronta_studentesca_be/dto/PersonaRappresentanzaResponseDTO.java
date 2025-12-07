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

    private PersonaResponseDTO persona;

    private OrganoRappresentanzaDTO organo;

    private LocalDate dataInizio;

    private LocalDate dataFine;


    // Costruttore che prende l'entità
    public PersonaRappresentanzaResponseDTO(PersonaRappresentanza entity) {
        this.id = entity.getId();
        this.persona = entity.getPersona() != null
                ? new PersonaResponseDTO(entity.getPersona())
                : null;
        this.organo = entity.getOrganoRappresentanza() != null
                ? new OrganoRappresentanzaDTO(entity.getOrganoRappresentanza())
                : null;
        this.dataInizio = entity.getDataInizio();
        this.dataFine = entity.getDataFine();

    }

}
