package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "direttivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direttivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDirettivo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dipartimento_id")
    private Dipartimento dipartimento; // nullable per il GENERALE

    /**
     * Inizio del mandato del direttivo
     */
    @Column(name = "inizio_mandato", nullable = false)
    private LocalDate inizioMandato;

    /**
     * Fine del mandato:
     * - NULL = direttivo ancora in carica
     * - valorizzato = direttivo storico
     */
    @Column(name = "fine_mandato")
    private LocalDate fineMandato;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Campo derivato: true se il direttivo è attualmente in carica.
     * Non viene mappato su DB.
     */
    @Transient
    public boolean isAttivo() {
        return fineMandato == null || fineMandato.isAfter(LocalDate.now());
    }
}
