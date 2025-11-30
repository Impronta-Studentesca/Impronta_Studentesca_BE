package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persona_direttivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaDirettivo {

    @EmbeddedId
    private PersonaDirettivoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("personaId")
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("direttivoId")
    @JoinColumn(name = "direttivo_id")
    private Direttivo direttivo;

    @Column(name = "ruolo_nel_direttivo", length = 100)
    private String ruoloNelDirettivo;
}