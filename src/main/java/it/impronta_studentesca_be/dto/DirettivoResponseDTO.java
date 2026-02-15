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

    private String dipartimentoCodice;

    private LocalDate inizioMandato;

    private LocalDate fineMandato;


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
        this.attivo = direttivo.isAttivo();
    }

    public DirettivoResponseDTO(Long id,
                                TipoDirettivo tipo,
                                Long dipartimentoId,
                                LocalDate inizioMandato,
                                LocalDate fineMandato) {
        this.id = id;
        this.tipo = tipo;
        this.dipartimentoId = dipartimentoId;
        this.inizioMandato = inizioMandato;
        this.fineMandato = fineMandato;
        this.attivo = calcolaAttivo(inizioMandato, fineMandato);
    }

    public DirettivoResponseDTO(Long id,
                                TipoDirettivo tipo,
                                Long dipartimentoId,
                                String dipartimentoCodice,
                                LocalDate inizioMandato,
                                LocalDate fineMandato) {
        this.id = id;
        this.tipo = tipo;
        this.dipartimentoId = dipartimentoId;
        this.dipartimentoCodice = dipartimentoCodice;
        this.inizioMandato = inizioMandato;
        this.fineMandato = fineMandato;
        this.attivo = calcolaAttivo(inizioMandato, fineMandato);
    }

    private boolean calcolaAttivo(LocalDate inizio, LocalDate fine) {
        LocalDate today = LocalDate.now();

        if (inizio == null) return false;

        // CASO IN CUI FINE E' NULL: ATTIVO SE OGGI >= INIZIO
        if (fine == null) {
            return !today.isBefore(inizio);
        }

        // CASO FINE NOT NULL: ATTIVO SE OGGI IN [INIZIO, FINE]
        return !today.isBefore(inizio) && !today.isAfter(fine);
    }

}
