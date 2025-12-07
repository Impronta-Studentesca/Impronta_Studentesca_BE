package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.entity.Ufficio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UfficioResponseDTO {

    private Long id;

    private String nome;

    private ResponsabileUfficioResponseDTO responsabile;

    public UfficioResponseDTO(Ufficio ufficio) {
        this.id = ufficio.getId();
        this.nome = ufficio.getNome();
        this.responsabile = new ResponsabileUfficioResponseDTO(ufficio.getResponsabile());
    }
}
