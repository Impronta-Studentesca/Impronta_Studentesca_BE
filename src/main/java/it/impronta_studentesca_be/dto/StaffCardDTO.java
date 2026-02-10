package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffCardDTO {

    private Long id;                 // id persona
    private String nome;
    private String cognome;
    private String email;


    private Set<String> ruoli;       // come PersonaResponseDTO

    private CorsoDiStudiResponseDTO corsoDiStudi;
    private Integer annoCorso;

    // foto
    private String fotoUrl;
    private String fotoThumbnailUrl;

    // badge staff
    private List<String> direttivoRuoli;       // "Presidente", "Tesoriere"...
    private Set<String> rappresentanze;  // nomi organi/carciche attive
}
