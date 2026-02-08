package it.impronta_studentesca_be.controller;

import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.dto.LoginRequestDTO;
import it.impronta_studentesca_be.dto.LoginResponseDTO;
import it.impronta_studentesca_be.service.PublicImprontaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
     * Primo settaggio password per una persona.
     * Accessibile pubblicamente (es. link univoco inviato via mail).
     */
    @PostMapping("/persona/{personaId}/crea/password")
    public ResponseEntity<Void> creaPassword(@PathVariable Long personaId,
                                             @RequestParam String password) {

        publicImprontaService.creaPassword(personaId, password);
        return ResponseEntity.ok().build(); // oppure noContent() se preferisci 204
    }


    /**
     * Modifica password per una persona.
     * Accessibile pubblicamente (es. link univoco inviato via mail).
     */
    @PostMapping("/persona/{personaId}/modifica/password")
    public ResponseEntity<Void> modificaPassword(@PathVariable Long personaId,
                                             @RequestParam String password) {

        publicImprontaService.modificaPassword(personaId, password);
        return ResponseEntity.ok().build(); // oppure noContent() se preferisci 204
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession(false); // se esiste
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        return ResponseEntity.ok().build();
    }




}
