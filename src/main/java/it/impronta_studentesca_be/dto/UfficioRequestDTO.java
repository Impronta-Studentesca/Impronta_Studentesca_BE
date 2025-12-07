package it.impronta_studentesca_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UfficioRequestDTO {

    private Long id;

    private String nome;

    private Long responsabileId;
}
