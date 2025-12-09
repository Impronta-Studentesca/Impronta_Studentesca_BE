package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaConRappresentanzeResponseDTO {

    private PersonaResponseDTO persona;

    private List<RuoloRappresentanzaDTO> cariche; // o "incarichi"
}
