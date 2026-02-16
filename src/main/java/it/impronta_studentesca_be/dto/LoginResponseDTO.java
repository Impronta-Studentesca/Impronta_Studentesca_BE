package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private Set<String> ruoli;   // es. ["DIRETTIVO", "STAFF"]

    private String token;
}
