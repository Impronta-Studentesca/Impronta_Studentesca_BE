package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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



    public PersonaRappresentanza(Persona persona, OrganoRappresentanza organoRappresentanza) {

        this.persona = persona;
        this.organoRappresentanza = organoRappresentanza;

    }

    public PersonaRappresentanza(Persona persona, OrganoRappresentanza organoRappresentanza, LocalDate dataInizio) {

        this.persona = persona;
        this.organoRappresentanza = organoRappresentanza;
        this.dataInizio = dataInizio;

    }

    public PersonaRappresentanza(Persona persona, OrganoRappresentanza organoRappresentanza, LocalDate dataInizio, LocalDate dataFine) {

        this.persona = persona;
        this.organoRappresentanza = organoRappresentanza;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;

    }

    @PrePersist
    protected void onCreate() {
        generaDataInizio();
        generaDataFine();
    }

    private void generaDataInizio() {
        if (dataInizio == null) {
            dataInizio = LocalDate.now();
        }
    }

    private void generaDataFine() {
        // Se è già stata impostata a mano, non tocco nulla
        if (dataFine != null) {
            return;
        }

        // Se manca la dataInizio, la genero prima
        if (dataInizio == null) {
            generaDataInizio();
        }

        // Durata in anni: 4 per CdA ERSU, altrimenti 2
        int durataAnni = 2;
        if (organoRappresentanza != null && organoRappresentanza.getCodice() != null) {
            if ("CdA ERSU".equalsIgnoreCase(organoRappresentanza.getCodice())) {
                durataAnni = 4;
            }
        }

        this.dataFine = dataInizio.plusYears(durataAnni);
    }


    public boolean isAttiva(){

        LocalDate today = LocalDate.now();
        return (this.getDataInizio() != null && this.getDataInizio().isAfter(today)) && (this.getDataFine() == null || this.getDataFine().isBefore(today));
    }


}
