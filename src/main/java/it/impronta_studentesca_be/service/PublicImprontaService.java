package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface PublicImprontaService {

    // Dipartimenti
    List<DipartimentoResponseDTO> getDipartimenti();
    DipartimentoResponseDTO getDipartimentoById(java.lang.Long dipartimentoId);
    DipartimentoResponseDTO getDipartimentoByCorsoId(java.lang.Long corsoId);
    DipartimentoResponseDTO getDipartimentoByPersonaId(java.lang.Long personaId);

    //Corsi
    List<CorsoMiniDTO> getCorsiByDipartimento(java.lang.Long dipartimentoId);
    CorsoDiStudiResponseDTO getCorsoById(java.lang.Long corsoId);
    CorsoDiStudiResponseDTO getCorsoByPersonaId(java.lang.Long personaId);

    //Uffici
    List<UfficioResponseDTO> getUffici();


    PersonaPhotoResponseDTO getFotoPersona(java.lang.Long personaId);

    // Persone
    PersonaResponseDTO getPersonaById(java.lang.Long personaId);
    List<PersonaMiniDTO> getPersoneByDipartimento(Long dipartimentoId);
    List<PersonaMiniDTO> getPersoneByCorso(Long corsoId);

    OrganoRappresentanzaDTO getOrganoById(java.lang.Long organoId);

    List<OrganoRappresentanzaDTO> getOrganoAll();

    PersonaConRappresentanzeResponseDTO getRappresentanteByPersona(java.lang.Long personaId);

    List<PersonaRappresentanzaResponseDTO>  getRappresentanteByOrgano(java.lang.Long personaId);

    List<PersonaConRappresentanzeResponseDTO> getRappresentanteAll();

    // Direttivi / rappresentanza
    List<PersonaDirettivoResponseDTO> getMembriDirettivo(java.lang.Long direttivoId);
    PersonaRappresentanzaResponseDTO getPersonaRappresentanzaById(java.lang.Long id);

    // Se vuoi, metodi “aggregati” già pronti per il FE
 // es. tipo=GENERALE

    DirettivoResponseDTO getDirettivoById(java.lang.Long personaId);

    List<DirettivoResponseDTO> getDirettivi();

    List<DirettivoResponseDTO> getDirettiviByTipo(TipoDirettivo tipo);

    List<DirettivoResponseDTO> getDirettiviByDipartimento(java.lang.Long dipartimentoId);

    List<DirettivoResponseDTO> getDirettiviInCarica();

    List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo);

    void creaPassword(Long personaId, String password, String token);

    void modificaPassword(Long personaId, String password, String token);

    LoginResponseDTO login(HttpServletRequest request, HttpServletResponse response, LoginRequestDTO dto);

    void richiestaModificaPassword(String email);

    void richiestaCreaPassword(Long id, String nome, String email);
}