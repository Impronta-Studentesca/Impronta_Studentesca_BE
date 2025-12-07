package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.TipoCorso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaRequestDTO {

    private Long id;

    private String nome;

    private String cognome;

    private String email;

    private Long corsoDiStudiId;

    private TipoCorso tipoCorso;

    private Integer annoCorso;

    private Long ufficioId;

    private boolean staff;

}
