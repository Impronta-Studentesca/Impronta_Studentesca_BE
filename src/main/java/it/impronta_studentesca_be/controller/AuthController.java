package it.impronta_studentesca_be.controller;

import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.service.PublicImprontaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PATH + "/" + ApiPath.AUTH_PATH)
public class AuthController {


    @Autowired
    PublicImprontaService publicImprontaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(HttpServletRequest httpRequest,
                                                  HttpServletResponse httpResponse,
                                                  @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(publicImprontaService.login(httpRequest, httpResponse, request));
    }



    /**
     * Primo settaggio password (PUBBLICO via link email).
     */
    @PostMapping("/persona/{personaId}/crea/password")
    public ResponseEntity<Void> creaPassword(@PathVariable Long personaId,
                                             @RequestBody PasswordSetRequest req) {

        publicImprontaService.creaPassword(personaId, req.getPassword(), req.getToken());
        return ResponseEntity.ok().build();
    }

    /**
     * Modifica password (PUBBLICO via link email).
     */
    @PostMapping("/persona/{personaId}/modifica/password")
    public ResponseEntity<Void> modificaPassword(@PathVariable Long personaId,
                                                 @RequestBody PasswordSetRequest req) {

        publicImprontaService.modificaPassword(personaId, req.getPassword(), req.getToken());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession(false); // se esiste
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/richiesta/modifica/password/{email}" )
    public ResponseEntity<List<PersonaConRappresentanzeResponseDTO>> richiestaModificaPassword(
            @PathVariable String email
    ) {
        publicImprontaService.richiestaModificaPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/richiesta/crea/password")
    public ResponseEntity<Void> richiestaCreaPassword(@RequestBody PersonaMiniRequestDTO req) {
        publicImprontaService.richiestaCreaPassword(req.getId(), req.getNome(), req.getEmail());
        return ResponseEntity.ok().build(); // oppure noContent().build()
    }




}
