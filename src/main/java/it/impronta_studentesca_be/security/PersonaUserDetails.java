package it.impronta_studentesca_be.security;

import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;


public class PersonaUserDetails implements UserDetails {

    private final Persona persona;

    public PersonaUserDetails(Persona persona) {
        this.persona = persona;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return persona.getRuoli().stream()
                .map(Ruolo::getNome)                // enum Roles
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return persona.getPassword();
    }

    @Override
    public String getUsername() {
        // qui uso l'email come username
        return persona.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // per ora true, puoi aggiungere flag in Persona se vuoi
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // idem
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // idem
    }

    @Override
    public boolean isEnabled() {
        return true; // idem (o un boolean in Persona tipo "attivo")
    }

    public Persona getPersona() {
        return persona;
    }
}
