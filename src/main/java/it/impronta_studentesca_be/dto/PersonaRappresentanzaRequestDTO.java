package it.impronta_studentesca_be.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonaRappresentanzaRequestDTO {

    private Long id;                        // null in create, valorizzato in update

    private Long personaId;                 // FK verso Persona

    private Long organoRappresentanzaId;    // FK verso OrganoRappresentanza

    private LocalDate dataInizio;

    private LocalDate dataFine;

}
