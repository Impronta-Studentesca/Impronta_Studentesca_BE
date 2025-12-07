package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.entity.PersonaDirettivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonaDirettivoResponseDTO {

    private PersonaResponseDTO persona;

    private DirettivoResponseDTO direttivo;

    private String ruoloNelDirettivo;

    public PersonaDirettivoResponseDTO(PersonaDirettivo personaDirettivo) {
        this.persona = new PersonaResponseDTO(personaDirettivo.getPersona());
        this.direttivo = new DirettivoResponseDTO(personaDirettivo.getDirettivo());
        this.ruoloNelDirettivo = personaDirettivo.getRuoloNelDirettivo();
    }


}
