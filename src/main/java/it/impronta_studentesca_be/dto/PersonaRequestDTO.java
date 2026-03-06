package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaRequestDTO {

    private Long id;

    private String nome;

    private String cognome;

    private String matricola;

    private String email;

    private String mailUnipa;

    private String numeroTelefono;

    private Long corsoDiStudiId;

    private Integer annoCorso;

    private Long ufficioId;

    private boolean staff;

    private Set<Roles> ruoli;

}
