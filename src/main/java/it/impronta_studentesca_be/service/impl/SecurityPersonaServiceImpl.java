package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.RuoloDirettivo;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.service.PersonaService;
import it.impronta_studentesca_be.service.SecurityPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class SecurityPersonaServiceImpl implements SecurityPersonaService {

    @Autowired
    PersonaService  personaService;


    @Override
    public Persona getCurrentPersona() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personaService.getByEmail(email);
    }

    public boolean isDirettivo(Persona p) {
        if (p == null || p.getRuoli() == null) return false;

        return p.getRuoli().stream()
                .map(Ruolo::getNome)
                .map(Object::toString)
                .anyMatch(name -> {
                    try {
                        return Roles.valueOf(name).equals(Roles.DIRETTIVO) ;

                    } catch (IllegalArgumentException ex) {
                        return false;
                    }
                });
    }

    @Override
    public void checkCanManagePersona(Long targetPersonaId) throws AccessDeniedException {
        Persona me = getCurrentPersona();
        if (isDirettivo(me)) return;
        if (!me.getId().equals(targetPersonaId)) {
            throw new AccessDeniedException("Non autorizzato");
        }
    }
}
