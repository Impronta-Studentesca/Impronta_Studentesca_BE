package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganoRappresentanzaDTO {

    private Long id;

    private String codice;

    private String nome;

    public OrganoRappresentanzaDTO(OrganoRappresentanza organoRappresentanza){
        this.id = organoRappresentanza.getId();
        this.codice = organoRappresentanza.getCodice();
        this.nome = organoRappresentanza.getNome();
    }
}
