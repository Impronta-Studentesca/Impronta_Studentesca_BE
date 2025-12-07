package it.impronta_studentesca_be.controller;

import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.dto.LoginRequestDTO;
import it.impronta_studentesca_be.dto.LoginResponseDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.security.PersonaUserDetails;
import it.impronta_studentesca_be.service.PublicImprontaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PATH + "/" + ApiPath.AUTH_PATH)
public class AuthController {


    @Autowired
    PublicImprontaService publicImprontaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) throws IllegalAccessException {

        return ResponseEntity.ok(publicImprontaService.login(request));
    }


    /**
     * Primo settaggio password per una persona.
     * Accessibile pubblicamente (es. link univoco inviato via mail).
     */
    @PostMapping("/persona/{personaId}/password")
    public ResponseEntity<Void> creaPassword(@PathVariable Long personaId,
                                             @RequestParam String password) {

        publicImprontaService.creaPassword(personaId, password);
        return ResponseEntity.ok().build(); // oppure noContent() se preferisci 204
    }


}
