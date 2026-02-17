package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoRow;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PersonaDirettivoService {

    void addPersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo);

    @Transactional
    void updatePersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo);

    void removePersonaFromDirettivo(Long personaId, Long direttivoId);

    List<PersonaDirettivoMiniDTO>  getMiniByDirettivo(Long direttivoId);

    List<PersonaMiniDTO> getPersonaByRuoloNotInDirettivo(Roles ruolo, Long direttivoId);

    @Transactional
    boolean  existsByPersona_IdAndDirettivo_Tipo(Long personaId, TipoDirettivo tipoDirettivo);

    @Transactional
    List<PersonaDirettivoRow> findRuoliDirettivoGeneraleAttiviByPersonaIds(List<Long> ids, TipoDirettivo tipoDirettivo, LocalDate today);
}