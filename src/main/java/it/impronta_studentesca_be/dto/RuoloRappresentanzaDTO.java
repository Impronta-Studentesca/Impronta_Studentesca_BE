package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuoloRappresentanzaDTO {

    private Long id; // id della persona_rappresentanza

    private OrganoRappresentanzaDTO organo;

    private LocalDate dataInizio;

    private LocalDate dataFine;
}
