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

    private Long personaId;

    private Long direttivoId;

    private String ruoloNelDirettivo;

    public PersonaDirettivoResponseDTO(PersonaDirettivo personaDirettivo) {
        this.personaId = personaDirettivo.getId().getPersonaId();
        this.direttivoId = personaDirettivo.getId().getDirettivoId();
        this.ruoloNelDirettivo = personaDirettivo.getRuoloNelDirettivo();
    }


}
