package it.impronta_studentesca_be.constant;

import lombok.Getter;

@Getter
public enum RuoloDirettivo {

    PRESIDENTE("Presidente", 1),
    VICE_PRESIDENTE("Vicepresidente", 2),
    SEGRETARIO("Segretario", 3),
    VICE_SEGRETARIO("Vicesegretario", 4),
    TESORIERE("Tesoriere", 5),
    VICE_TESORIERE("Vice tesoriere", 6),

    RESPONSABILE_COMUNICAZIONE("Responsabile comunicazione",7),
    VICE_RESPONSABILE_COMUNICAZIONE("Vice responsabile comunicazione", 8),
    RESPONSABILE_ORGANIZZAZIONE("Responsabile organizzazione", 9),
    VICE_RESPONSABILE_ORGANIZZAZIONE("Vice responsabile organizzazione", 10),

    SOCIO_CONSIGLIERE("Socio Consigliere", 11),

    PRESIDENTE_Dipartimentale("Presidente dipartimentale", 12);


    private final String label;

    private int ordine;


    RuoloDirettivo(String label,  int ordine) {
        this.label = label;
        this.ordine = ordine;
    }
}
