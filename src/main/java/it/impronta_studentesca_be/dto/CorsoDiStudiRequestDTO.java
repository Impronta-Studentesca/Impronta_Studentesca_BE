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
public class CorsoDiStudiRequestDTO {

    private Long id;

    private String nome;

    private Long dipartimentoId;

    private TipoCorso tipoCorso;

}

