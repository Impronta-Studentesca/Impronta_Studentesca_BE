package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDirettivo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dipartimento_id")
    private Dipartimento dipartimento; // nullable per il GENERALE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
