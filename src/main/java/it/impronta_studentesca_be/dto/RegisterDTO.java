package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private PersonaRequestDTO persona;
    private CorsoDiStudiRequestDTO corso;
}
