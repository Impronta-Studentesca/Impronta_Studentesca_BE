package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.dto.UfficioRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ufficio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ufficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // FK ufficio.responsabile_id → persona.id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsabile_id")
    private Persona responsabile;



}
