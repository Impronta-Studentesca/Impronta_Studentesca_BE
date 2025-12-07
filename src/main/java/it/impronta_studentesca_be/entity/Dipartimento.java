package it.impronta_studentesca_be.entity;


import it.impronta_studentesca_be.dto.DipartimentoRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dipartimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dipartimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String codice;


}