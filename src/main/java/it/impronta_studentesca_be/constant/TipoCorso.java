package it.impronta_studentesca_be.constant;

public enum TipoCorso {

    TRIENNALE("Triennale"),
    MAGISTRALE("Magistrale"),
    CICLO_UNICO("Ciclo unico"),
    ALTRA("Altra");

    private final String value;

    TipoCorso(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

