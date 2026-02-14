package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdminImprontaService {

    // Persone
    void creaPersona(PersonaRequestDTO persona);
    void aggiornaPersona(PersonaRequestDTO persona);
    void eliminaPersona(Long personaId);

    @Transactional
    void modificaUfficio(UfficioRequestDTO ufficio);

    @Transactional
    void eliminaUfficio(Long id);

    List<StaffCardDTO> getStaffCards();
    ImageUploadResponseDTO uploadFotoPersona(Long personaId, MultipartFile file);
    // Direttivi

    DirettivoResponseDTO creaDirettivo(DirettivoRequestDTO direttivo);
    DirettivoResponseDTO aggiornaDirettivo(DirettivoRequestDTO direttivo);
    void eliminaDirettivo(Long direttivoId);

    @Transactional
    void deleteFotoPersona(Long personaId);

    void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo);

    void modificaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo);

    void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId);

    List<PersonaMiniDTO> getPersoneByRuoloNonPresentiNelDirettivoId(Roles ruolo, Long direttivoId);

    // Organi di rappresentanza
    void assegnaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);
    void modificaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);

    void eliminaPersonaRappresentanza(Long personaId, String nome);

    void eliminaPersonaRappresentanza(Long personaRappresentanzaId);


    // Dipartimenti / corsi / uffici (CRUD admin)
    void creaDipartimento(DipartimentoRequestDTO dipartimento);
    void modificaDipartimento(DipartimentoRequestDTO dipartimento);
    void eliminaDipartimento(DipartimentoRequestDTO dipartimento);
    void creaCorso(CorsoDiStudiRequestDTO corso);
    void modificaCorso(CorsoDiStudiRequestDTO corso);
    void eliminaCorso(CorsoDiStudiRequestDTO corso);
    void creaUfficio(UfficioRequestDTO ufficio);




    // ecc… puoi aggiungere tutto ciò che ti serve lato pannello admin

}
