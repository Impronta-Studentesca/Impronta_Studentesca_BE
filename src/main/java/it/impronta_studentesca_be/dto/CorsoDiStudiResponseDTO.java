package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.TipoCorso;
import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
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

    public CorsoDiStudiResponseDTO(Long corsoId, String corsoNome, TipoCorso tipoCorso) {
        this.id = corsoId;
        this.nome = corsoNome;
        this.tipoCorso = tipoCorso;
    }

    public CorsoDiStudiResponseDTO(Long corsoId, String corsoNome, TipoCorso tipoCorso, Long dipartimentoId) {
        this.id = corsoId;
        this.nome = corsoNome;
        this.tipoCorso = tipoCorso;
        this.dipartimento = new DipartimentoResponseDTO();
        this.dipartimento.setId(dipartimentoId);
    }

    public CorsoDiStudiResponseDTO(Long corsoId, String corsoNome, TipoCorso tipoCorso, Long dipartimentoId, String dipartimentoNome, String dipartimentoCodice) {
        this.id = corsoId;
        this.nome = corsoNome;
        this.tipoCorso = tipoCorso;
        this.dipartimento = new DipartimentoResponseDTO(dipartimentoId, dipartimentoNome, dipartimentoCodice);
    }
}

