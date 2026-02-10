package it.impronta_studentesca_be.constant;

import lombok.Data;

@Data
public class ApiPath {
        /*
        CONTROLLER
         */
    // Path base per tutte le API REST
    public static final String BASE_PATH = "impronta/studentesca/official/api";

    // Path per il controller pubblico
    public static final String PUBLIC_PATH = "public";

    // Path per il controller amministrativo
    public static final String ADMIN_PATH = "admin";

    // Path per il controller auth
    public static final String AUTH_PATH = "auth";


    /*
    METODI
     */

    public static final String PERSONE_PATH = "persone";

    public static final String PERSONA_PATH = "persona";

    public static final String CORSI_PATH = "corsi";

    public static final String CORSO_PATH = "corso";

    public static final String DIPARTIMENTI_PATH = "dipartimenti";

    public static final String DIPARTIMENTO_PATH = "dipartimento";

    public static final String ALL_PATH = "all";

    public static final String STAFF_PATH = "staff";

    public static final String DIRETTIVO_PATH = "direttivo";

    public static final String DIRETTIVI_PATH = "direttivi";

    public static final String TIPO_PATH = "tipo";

    public static final String IN_CARICA_PATH = "in_carica";

    public static final String ORGANO_PATH = "organo";

    public static final String ORGANI_PATH = "organi";

    public static final String RAPPRESENTANTE_PATH = "rappresentante";

    public static final String RAPPRESENTANTI_PATH = "rappresentanti";



}
