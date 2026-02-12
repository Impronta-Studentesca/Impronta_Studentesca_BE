package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DirettivoRequestDTO {

    private Long id;                 // null in create, valorizzato in update (se lo usi così)

    private TipoDirettivo tipo;      // GENERALE, DIPARTIMENTALE, ...

    /**
     * Id del dipartimento (nullable per il direttivo GENERALE)
     */
    private Long dipartimentoId;


    /**
     * Inizio mandato (puoi farlo opzionale lato FE e valorizzare nel service con LocalDate.now()
     * se è null)
     */
    private LocalDate inizioMandato;

    /**
     * Fine mandato (null = ancora in carica)
     */
    private LocalDate fineMandato;
}
