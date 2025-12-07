package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;

import java.util.List;

public interface PublicImprontaService {

    // Dipartimenti
    List<DipartimentoResponseDTO> getDipartimenti();
    DipartimentoResponseDTO getDipartimentoById(Long dipartimentoId);
    DipartimentoResponseDTO getDipartimentoByCorsoId(Long corsoId);
    DipartimentoResponseDTO getDipartimentoByPersonaId(Long personaId);

    //Corsi
    List<CorsoDiStudiResponseDTO> getCorsiByDipartimento(Long dipartimentoId);
    CorsoDiStudiResponseDTO getCorsoById(Long corsoId);
    CorsoDiStudiResponseDTO getCorsoByPersonaId(Long personaId);

    //Uffici
    List<UfficioResponseDTO> getUffici();


    // Persone
    PersonaResponseDTO getPersonaById(Long personaId);
    List<PersonaResponseDTO> getStaff();
    List<PersonaResponseDTO> getPersoneByDipartimento(Long dipartimentoId);
    List<PersonaResponseDTO> getPersoneByCorso(Long corsoId);

    OrganoRappresentanzaDTO getOrganoById(Long organoId);

    List<OrganoRappresentanzaDTO> getOrganoAll();

    PersonaConRappresentanzeResponseDTO getRappresentanteByPersona(Long personaId);

    List<PersonaRappresentanzaResponseDTO>  getRappresentanteByOrgano(Long personaId);

    List<PersonaConRappresentanzeResponseDTO> getRappresentanteAll();

    // Direttivi / rappresentanza
    List<PersonaDirettivoResponseDTO> getMembriDirettivo(Long direttivoId);
    PersonaRappresentanzaResponseDTO getRappresentanteById(Long id);

    // Se vuoi, metodi “aggregati” già pronti per il FE
 // es. tipo=GENERALE

    DirettivoResponseDTO getDirettivoById(Long personaId);

    List<DirettivoResponseDTO> getDirettivi();

    List<DirettivoResponseDTO> getDirettiviByTipo(TipoDirettivo tipo);

    List<DirettivoResponseDTO> getDirettiviByDipartimento(Long dipartimentoId);

    List<DirettivoResponseDTO> getDirettiviInCarica();

    List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo);

    void creaPassword(Long personaId, String password);

    LoginResponseDTO login( LoginRequestDTO request) throws IllegalAccessException;
}