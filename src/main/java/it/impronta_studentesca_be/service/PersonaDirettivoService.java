package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PersonaDirettivoService {

    void addPersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo);

    @Transactional
    void updatePersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo);

    void removePersonaFromDirettivo(Long personaId, Long direttivoId);

    List<PersonaDirettivoMiniDTO>  getMiniByDirettivo(Long direttivoId);

    List<PersonaDirettivo> getDirettivoGeneraleAttivoByPersona(Long personaId);

    List<String> getRuoliDirettivoGeneraleAttivi(Long personaId);

    List<PersonaMiniDTO> getPersonaByRuoloNotInDirettivo(Roles ruolo, Long direttivoId);
}