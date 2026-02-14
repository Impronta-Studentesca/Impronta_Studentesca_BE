package it.impronta_studentesca_be.dto.record;

import it.impronta_studentesca_be.entity.Dipartimento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DipartimentoResponseDTO {

    private Long id;

    private String nome;

    private String codice;

    public DipartimentoResponseDTO(Dipartimento dipartimento) {
        this.id = dipartimento.getId();
        this.nome = dipartimento.getNome();
        this.codice = dipartimento.getCodice();
    }




}
