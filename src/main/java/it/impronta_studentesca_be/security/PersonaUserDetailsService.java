package it.impronta_studentesca_be.security;

import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonaUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonaService personaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername username={}", username);

        // username = email
        Persona persona = personaService.getByEmail(username);

        return new PersonaUserDetails(persona);
    }
}
