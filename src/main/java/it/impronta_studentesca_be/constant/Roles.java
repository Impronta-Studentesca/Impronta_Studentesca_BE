package it.impronta_studentesca_be.constant;

public enum Roles {

    DIRETTIVO("DIRETTIVO"),
    DIRETTIVO_DIPARTIMENTALE("DIRETTIVO_DIPARTIMENTALE"),
    STAFF("STAFF"),
    RAPPRESENTANTE("RAPPRESENTANTE"),
    RESPONSABILE_UFFICIO("RESPONSABILE_UFFICIO"),
    USER("USER"); // ruolo base / pubblico

    private final String authority;

    Roles(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
