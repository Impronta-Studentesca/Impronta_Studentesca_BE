package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface AdminImprontaService {

    // Persone
    PersonaResponseDTO creaPersona(PersonaRequestDTO persona);
    PersonaResponseDTO aggiornaPersona(PersonaRequestDTO persona);
    void eliminaPersona(Long personaId);
    ImageUploadResponseDTO uploadFotoPersona(Long personaId, MultipartFile file);
    // Direttivi
    void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo);
    void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId);

    // Organi di rappresentanza
    void eliminaPersonaRappresentanza(Long personaRappresentanzaId);

    // Dipartimenti / corsi / uffici (CRUD admin)
    DipartimentoResponseDTO creaDipartimento(DipartimentoRequestDTO dipartimento);
    CorsoDiStudiResponseDTO creaCorso(CorsoDiStudiRequestDTO corso);
    UfficioResponseDTO creaUfficio(UfficioRequestDTO ufficio);

    // ecc… puoi aggiungere tutto ciò che ti serve lato pannello admin

}
