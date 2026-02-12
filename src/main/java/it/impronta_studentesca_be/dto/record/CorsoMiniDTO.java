package it.impronta_studentesca_be.dto.record;

import it.impronta_studentesca_be.constant.TipoCorso;

public record CorsoMiniDTO(Long id, String nome, TipoCorso tipoCorso, Long dipartimentoId) {}
