package it.impronta_studentesca_be.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDirettivoId implements Serializable {

    private Long personaId;
    private Long direttivoId;
}
