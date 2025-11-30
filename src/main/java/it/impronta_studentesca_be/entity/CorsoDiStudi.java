package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "corso_di_studi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorsoDiStudi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dipartimento_id", nullable = false)
    private Dipartimento dipartimento;
}
