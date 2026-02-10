package it.impronta_studentesca_be.util;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.RuoloDirettivo;
import it.impronta_studentesca_be.dto.CorsoDiStudiRequestDTO;
import it.impronta_studentesca_be.dto.DipartimentoRequestDTO;
import it.impronta_studentesca_be.dto.PersonaRequestDTO;
import it.impronta_studentesca_be.dto.UfficioRequestDTO;
import it.impronta_studentesca_be.entity.*;
import it.impronta_studentesca_be.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Mapper {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private CorsoDiStudiService corsoDiStudiService;

    @Autowired
    private UfficioService ufficioService;

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
            if (dto.getCorsoDiStudiId() != null){
                corsoDiStudi = corsoDiStudiService.getById(dto.getCorsoDiStudiId());
            }

            // Carico l'ufficio se presente
            Ufficio ufficio = null;
            if (dto.getUfficioId() != null) {
                ufficio = ufficioService.getById(dto.getUfficioId());
            }


            Set<Ruolo> ruoliUser = null;
            if(dto.getRuoli() == null || dto.getRuoli().isEmpty()){
                //assegno di default USER
                ruoliUser = Set.of(ruoloService.getByNome(Roles.USER));
            }else {
                ruoliUser =  dto.getRuoli().stream().map(ruolo -> ruoloService.getByNome(ruolo)).collect(Collectors.toSet());

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

    public Dipartimento toDipartimento(DipartimentoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        if (dto == null) {
            return null;
        } else if (dto.getId() != null && (dto.getCodice() == null || dto.getCodice().isEmpty() || dto.getNome() == null || dto.getNome().isEmpty())) {
            return dipartimentoService.getById(dto.getId());
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
//        } else if (dto.getId() != null) {
//            return corsoDiStudiService.getById(dto.getId());
        } else {

            return CorsoDiStudi.builder()
                    .id(dto.getId())                               // null in create
                    .nome(dto.getNome())
                    .tipoCorso(dto.getTipoCorso())
                    .dipartimento(dipartimentoService.getById(dto.getDipartimentoId()))
                    .build();
        }
    }

    public Ufficio toUfficio(UfficioRequestDTO dto) {
        if (dto == null) {
            return null;
        } else if (dto.getId() != null) {
            return ufficioService.getById(dto.getId());
        } else{

        Persona responsabile = null;
        if (dto.getResponsabileId() != null) {
            responsabile = personaService.getById(dto.getResponsabileId());
        }

        return Ufficio.builder()
                .id(dto.getId())          // null in create, valorizzato in update
                .nome(dto.getNome())
                .responsabile(responsabile)
                .build();
    }
    }

    public Set<RuoloDirettivo> getRuoliDirettivo(Set<Ruolo> ruoli) {
        if (ruoli == null || ruoli.isEmpty()) return Collections.emptySet();

        return ruoli.stream()
                .map(Ruolo::getNome)
                .map(Object::toString)
                .map(Mapper::normalize)
                .map(Mapper::toRuoloDirettivoOrNull)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(RuoloDirettivo::getOrdine))
                .collect(Collectors.toCollection(LinkedHashSet::new)); // mantiene ordine
    }

    private static String normalize(String raw) {
        if (raw == null) return "";
        return raw.trim().toUpperCase().replace(' ', '_');
    }

    private static RuoloDirettivo toRuoloDirettivoOrNull(String key) {
        try {
            return RuoloDirettivo.valueOf(key);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
