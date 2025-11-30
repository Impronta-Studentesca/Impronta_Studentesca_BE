package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "persona_rappresentanza")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaRappresentanza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // persona_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    // organo_rappresentanza_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organo_rappresentanza_id", nullable = false)
    private OrganoRappresentanza organoRappresentanza;

    @Column(name = "data_inizio")
    private LocalDate dataInizio;

    @Column(name = "data_fine")
    private LocalDate dataFine;

    @Column(length = 100)
    private String carica; // es. "Rappresentante degli studenti"

    @Column(columnDefinition = "TEXT")
    private String note;
}
