package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organo_rappresentanza")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganoRappresentanza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codice;  // es. 'CD_DIP', 'CCS', 'SENATO', ...

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;
}