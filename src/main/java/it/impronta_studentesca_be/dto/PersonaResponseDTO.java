package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaResponseDTO {

    private Long id;

    private Set<String> ruoli;

    private String nome;

    private String cognome;

    private CorsoDiStudiResponseDTO corsoDiStudi;

    private Integer annoCorso;

    private UfficioResponseDTO ufficio;

    public PersonaResponseDTO(Persona persona) {
        this.id = persona.getId();
        this.nome = persona.getNome();
        this.cognome = persona.getCognome();
        this.ruoli = persona.getRuoli().stream()
                .map(Ruolo::getNome)
                .map(Roles::name)
                .collect(Collectors.toSet());
        this.corsoDiStudi = persona.getCorsoDiStudi() != null ?  new CorsoDiStudiResponseDTO(persona.getCorsoDiStudi()) : null;
        this.annoCorso = persona.getAnnoCorso();
        this.ufficio = persona.getUfficio() != null ? new UfficioResponseDTO(persona.getUfficio()) : new UfficioResponseDTO();

    }

}
