package it.impronta_studentesca_be.dto;

import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
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

    private PersonaResponseDTO personaResponseDTO;

    private Long direttivoId;

    private String ruoloNelDirettivo;

    public PersonaDirettivoResponseDTO(PersonaDirettivo personaDirettivo) {
        this.personaResponseDTO = new PersonaResponseDTO(personaDirettivo.getPersona());
        this.direttivoId = personaDirettivo.getId().getDirettivoId();
        this.ruoloNelDirettivo = personaDirettivo.getRuoloNelDirettivo();
    }

    public PersonaDirettivoResponseDTO(PersonaDirettivoMiniDTO personaDirettivo) {
        this.personaResponseDTO = new PersonaResponseDTO(personaDirettivo.personaId(), personaDirettivo.nome(), personaDirettivo.cognome());

        this.direttivoId = personaDirettivo.direttivoId();

        this.ruoloNelDirettivo = personaDirettivo.ruoloNelDirettivo();
    }



}
