package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.constant.TipoCorso;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corso_di_studi_id")
    private CorsoDiStudi corsoDiStudi;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_corso")
    private TipoCorso tipoCorso;

    @Column(name = "anno_corso")
    private Integer annoCorso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ufficio_id")
    private Ufficio ufficio;

    @Column(name = "data_registrazione", nullable = false)
    private LocalDateTime dataRegistrazione;

    @Column(name = "is_staff", nullable = false)
    private boolean staff;

    @Column(name = "foto_url")
    private String fotoUrl;

    @PrePersist
    protected void onCreate() {
        if (dataRegistrazione == null) {
            dataRegistrazione = LocalDateTime.now();
        }
    }
}