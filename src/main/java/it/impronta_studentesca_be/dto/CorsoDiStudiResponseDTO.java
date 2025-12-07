package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.TipoCorso;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorsoDiStudiResponseDTO {

    private Long id;

    private String nome;

    private TipoCorso tipoCorso;

    private DipartimentoResponseDTO dipartimento;

    public CorsoDiStudiResponseDTO(CorsoDiStudi corsoDiStudi) {
        this.id = corsoDiStudi.getId();
        this.nome = corsoDiStudi.getNome();
        this.tipoCorso = corsoDiStudi.getTipoCorso();
        if (corsoDiStudi.getDipartimento() != null) {
            this.dipartimento = new DipartimentoResponseDTO(corsoDiStudi.getDipartimento());

        }
    }
}

