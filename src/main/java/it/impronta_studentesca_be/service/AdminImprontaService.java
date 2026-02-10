package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdminImprontaService {

    // Persone
    PersonaResponseDTO creaPersona(PersonaRequestDTO persona);
    PersonaResponseDTO aggiornaPersona(PersonaRequestDTO persona);
    void eliminaPersona(Long personaId);

    List<StaffCardDTO> getStaffCards();
    ImageUploadResponseDTO uploadFotoPersona(Long personaId, MultipartFile file);
    // Direttivi
    void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo);

    void modificaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo);

    void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId);

    // Organi di rappresentanza
    void assegnaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);
    void modificaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);
    void eliminaPersonaRappresentanza(Long personaRappresentanzaId);

    // Dipartimenti / corsi / uffici (CRUD admin)
    DipartimentoResponseDTO creaDipartimento(DipartimentoRequestDTO dipartimento);
    DipartimentoResponseDTO modificaDipartimento(DipartimentoRequestDTO dipartimento);
    void eliminaDipartimento(DipartimentoRequestDTO dipartimento);
    CorsoDiStudiResponseDTO creaCorso(CorsoDiStudiRequestDTO corso);
    CorsoDiStudiResponseDTO modificaCorso(CorsoDiStudiRequestDTO corso);
    void eliminaCorso(CorsoDiStudiRequestDTO corso);
    UfficioResponseDTO creaUfficio(UfficioRequestDTO ufficio);




    // ecc… puoi aggiungere tutto ciò che ti serve lato pannello admin

}
