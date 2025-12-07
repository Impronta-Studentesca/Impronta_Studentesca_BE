package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.constant.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ruolo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false, unique = true)
    private Roles nome;   // es. ADMIN, STAFF, ...

    @Column(name = "descrizione")
    private String descrizione;

}
