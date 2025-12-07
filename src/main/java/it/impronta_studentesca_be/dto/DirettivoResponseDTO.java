package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.Direttivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirettivoResponseDTO {

    private Long id;

    private TipoDirettivo tipo;

    /**
     * Id del dipartimento (nullable per il GENERALE)
     */
    private Long dipartimentoId;

    private String annoAccademico;

    private LocalDate inizioMandato;

    private LocalDate fineMandato;

    private LocalDateTime createdAt;

    /**
     * True se il direttivo è attualmente in carica.
     */
    private boolean attivo;

    public DirettivoResponseDTO(Direttivo direttivo) {
        this.id = direttivo.getId();
        this.tipo = direttivo.getTipo();
        this.dipartimentoId = direttivo.getDipartimento() != null ? direttivo.getDipartimento().getId() : null;
        this.inizioMandato = direttivo.getInizioMandato();
        this.fineMandato = direttivo.getFineMandato();
        this.createdAt = direttivo.getCreatedAt();
        this.attivo = direttivo.isAttivo();
    }
}
