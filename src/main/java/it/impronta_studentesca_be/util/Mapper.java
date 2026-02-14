package it.impronta_studentesca_be.util;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.*;
import it.impronta_studentesca_be.repository.*;
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

    @Autowired
    private RuoloRepository ruoloRepository;


    public Persona toPersona(PersonaRequestDTO dto) {

        if (dto == null) return null;

        CorsoDiStudi corsoDiStudi = null;
        if (dto.getCorsoDiStudiId() != null) {
            corsoDiStudi = corsoDiStudiRepository.getReferenceById(dto.getCorsoDiStudiId());
        }

        Ufficio ufficio = null;
        if (dto.getUfficioId() != null) {
            ufficio = ufficioRepository.getReferenceById(dto.getUfficioId());
        }

        Set<Roles> nomiRuoli;
        if (dto.getRuoli() == null || dto.getRuoli().isEmpty()) {
            nomiRuoli = Set.of(Roles.USER);
        } else {
            nomiRuoli = new HashSet<>(dto.getRuoli());
        }

        Set<Ruolo> ruoliUser = new HashSet<>(ruoloRepository.findByNomeIn(nomiRuoli));

        // opzionale: se vuoi validare che non manchi nessun ruolo richiesto
        // if (ruoliUser.size() != nomiRuoli.size()) ...

        String email = dto.getEmail();
        if (email != null) {
            email = email.trim().toLowerCase(Locale.ROOT);
        }

        return Persona.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(email)
                .corsoDiStudi(corsoDiStudi)
                .ufficio(ufficio)
                .annoCorso(dto.getAnnoCorso())
                .ruoli(ruoliUser)
                .build();
    }


    public Persona toPersona(PersonaMiniDTO dto) {
        return Persona.builder()
                .id(dto.id())                 // di solito null in create
                .nome(dto.nome())
                .cognome(dto.cognome())
                .build();
    }

    public Dipartimento toDipartimentoUpdate(DipartimentoRequestDTO dto) {
        if (dto == null) return null;

        return Dipartimento.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .codice(dto.getCodice())
                .build();
    }


    public Dipartimento toDipartimento(DipartimentoRequestDTO dto) {
        if (dto == null) return null;

        return Dipartimento.builder()
                .nome(dto.getNome())
                .codice(dto.getCodice())
                .build();
    }


    public CorsoDiStudi toCorsoDiStudiUpdate(CorsoDiStudiRequestDTO dto) {
        if (dto == null) return null;

        return CorsoDiStudi.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .tipoCorso(dto.getTipoCorso())
                .build();
    }

    public CorsoDiStudi toCorsoDiStudi(CorsoDiStudiRequestDTO dto) {
        if (dto == null) return null;

        return CorsoDiStudi.builder()
                .nome(dto.getNome())
                .tipoCorso(dto.getTipoCorso())
                // DIPARTIMENTO LO SETTI NEL SERVICE
                .build();
    }


    public Ufficio toUfficio(UfficioRequestDTO dto) {
        if (dto == null) return null;

        return Ufficio.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                // RESPONSABILE LO SETTI NEL SERVICE
                .build();
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



