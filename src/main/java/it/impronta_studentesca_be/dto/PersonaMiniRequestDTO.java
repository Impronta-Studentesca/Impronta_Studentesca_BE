package it.impronta_studentesca_be.dto;

import lombok.Data;

@Data
public class PersonaMiniRequestDTO {
    private Long id;
    private String nome;
    private String email;
}
