package it.impronta_studentesca_be.util;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.RuoloDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.*;
import it.impronta_studentesca_be.repository.CorsoDiStudiRepository;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.repository.UfficioRepository;
import it.impronta_studentesca_be.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Mapper {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Autowired
    private CorsoDiStudiRepository corsoDiStudiRepository;

    @Autowired
    private UfficioRepository ufficioRepository;

    @Autowired
    private RuoloService ruoloService;


    public Persona toPersona(PersonaRequestDTO dto) {

        if (dto == null) {
            return null;
//        } else if (dto.getId() != null) {
//            return personaService.getById(dto.getId());
        } else {

            // Carico il corso di studi (obbligatorio)

            CorsoDiStudi corsoDiStudi = null;
            if (dto.getCorsoDiStudiId() != null) {
                corsoDiStudi = corsoDiStudiRepository.getReferenceById(dto.getCorsoDiStudiId());
            }

            // Carico l'ufficio se presente
            Ufficio ufficio = null;
            if (dto.getUfficioId() != null) {
                ufficio = ufficioRepository.getReferenceById(dto.getUfficioId());
            }


            Set<Ruolo> ruoliUser = null;
            if (dto.getRuoli() == null || dto.getRuoli().isEmpty()) {
                //assegno di default USER
                ruoliUser = Set.of(ruoloService.getByNome(Roles.USER));
            } else {
                ruoliUser = dto.getRuoli().stream().map(ruolo -> ruoloService.getByNome(ruolo)).collect(Collectors.toSet());

            }
            return Persona.builder()
                    .id(dto.getId())                 // di solito null in create
                    .nome(dto.getNome())
                    .cognome(dto.getCognome())
                    .email(dto.getEmail().trim().toLowerCase(Locale.ROOT))
                    .corsoDiStudi(corsoDiStudi)
                    .ufficio(ufficio)
                    .annoCorso(dto.getAnnoCorso())
                    .ruoli(ruoliUser)        // o un Set vuoto se non vuoi default
                    .build();
        }
    }

    public Persona toPersona(PersonaMiniDTO dto) {
        return Persona.builder()
                .id(dto.id())                 // di solito null in create
                .nome(dto.nome())
                .cognome(dto.cognome())
                .build();
    }

    public Dipartimento toDipartimento(DipartimentoRequestDTO dto) {

        if (dto == null) {
            return null;
        } else {

            return Dipartimento.builder()
                    .id(dto.getId())        // null in create, valorizzato in update
                    .nome(dto.getNome())
                    .codice(dto.getCodice())
                    .build();
        }
    }

    public CorsoDiStudi toCorsoDiSudi(CorsoDiStudiRequestDTO dto) {
        if (dto == null) {
            return null;
        } else {

            return CorsoDiStudi.builder()
                    .id(dto.getId())                               // null in create
                    .nome(dto.getNome())
                    .tipoCorso(dto.getTipoCorso())
                    .dipartimento(dipartimentoRepository.getReferenceById(dto.getDipartimentoId()))
                    .build();
        }
    }

    public Ufficio toUfficio(UfficioRequestDTO dto) {
        if (dto == null) {
            return null;
        } else {

            Persona responsabile = null;
            if (dto.getResponsabileId() != null) {
                responsabile = personaRepository.getReferenceById(dto.getResponsabileId());
            }

            return Ufficio.builder()
                    .id(dto.getId())          // null in create, valorizzato in update
                    .nome(dto.getNome())
                    .responsabile(responsabile)
                    .build();
        }
    }


    public Direttivo toDirettivo(DirettivoRequestDTO dto) {
        if (dto == null) {
            return null;

        } else {

            Dipartimento dipartimento = null;
            if (dto.getDipartimentoId() != null) {
                dipartimento = dipartimentoRepository.getReferenceById(dto.getDipartimentoId());
            }


            return Direttivo.builder()
                    .id(dto.getId())
                    .tipo(dto.getTipo())
                    .dipartimento(dipartimento)
                    .inizioMandato(dto.getInizioMandato())
                    .fineMandato(dto.getFineMandato() != null ? dto.getFineMandato() : null)
                    .build();
        }
    }


}



