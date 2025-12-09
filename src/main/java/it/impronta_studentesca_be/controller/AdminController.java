package it.impronta_studentesca_be.controller;

import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.service.AdminImprontaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PATH + "/" + ApiPath.ADMIN_PATH)
public class AdminController {

    @Autowired
    AdminImprontaService  adminImprontaService;

    //PERSONA

    @PostMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> creaPersona(@RequestBody PersonaRequestDTO persona) {
        PersonaResponseDTO response = adminImprontaService.creaPersona(persona);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> aggiornaPersona(@RequestBody PersonaRequestDTO persona) {
        PersonaResponseDTO response = adminImprontaService.aggiornaPersona(persona);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/persona/{personaId}")
    public ResponseEntity<Void> eliminaPersona(@PathVariable Long personaId) {
        adminImprontaService.eliminaPersona(personaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/persona/{personaId}/foto")
    public ResponseEntity<ImageUploadResponseDTO> uploadFotoPersona(@PathVariable Long personaId,
                                                                    @RequestParam("file") MultipartFile file) {
        ImageUploadResponseDTO response = adminImprontaService.uploadFotoPersona(personaId, file);
        return ResponseEntity.ok(response);
    }

    // DIRETTIVO

    @PostMapping("/direttivo/{direttivoId}/persona/{personaId}")
    public ResponseEntity<Void> assegnaPersonaADirettivo(@PathVariable Long personaId,
                                                         @PathVariable Long direttivoId,
                                                         @RequestParam String ruolo) {
        adminImprontaService.assegnaPersonaADirettivo(personaId, direttivoId, ruolo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/direttivo/{direttivoId}/persona/{personaId}")
    public ResponseEntity<Void> rimuoviPersonaDaDirettivo(@PathVariable Long personaId,
                                                          @PathVariable Long direttivoId) {
        adminImprontaService.rimuoviPersonaDaDirettivo(personaId, direttivoId);
        return ResponseEntity.noContent().build();
    }

    // ORGANI DI RAPPRESENTANZA

//    @PostMapping("/organo/{organoId}/persona/{personaId}")
//    public ResponseEntity<Void> assegnaPersonaAOrgano(@PathVariable Long personaId,
//                                                      @PathVariable Long organoId,
//                                                      @RequestParam String ruolo) {
//        adminImprontaService.assegnaPersonaAOrgano(personaId, organoId, ruolo);
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("/persona-rappresentanza/{personaRappresentanzaId}")
    public ResponseEntity<Void> eliminaPersonaRappresentanza(@PathVariable Long personaRappresentanzaId) {
        adminImprontaService.eliminaPersonaRappresentanza(personaRappresentanzaId);
        return ResponseEntity.noContent().build();
    }

    // DIPARTIMENTI

    @PostMapping("/dipartimento")
    public ResponseEntity<DipartimentoResponseDTO> creaDipartimento(@RequestBody DipartimentoRequestDTO dipartimento) {
        DipartimentoResponseDTO response = adminImprontaService.creaDipartimento(dipartimento);
        return ResponseEntity.ok(response);
    }

    // CORSI DI STUDIO

    @PostMapping("/corso")
    public ResponseEntity<CorsoDiStudiResponseDTO> creaCorso(@RequestBody CorsoDiStudiRequestDTO corso) {
        CorsoDiStudiResponseDTO response = adminImprontaService.creaCorso(corso);
        return ResponseEntity.ok(response);
    }

    // UFFICI

    @PostMapping("/ufficio")
    public ResponseEntity<UfficioResponseDTO> creaUfficio(@RequestBody UfficioRequestDTO ufficio) {
        UfficioResponseDTO response = adminImprontaService.creaUfficio(ufficio);
        return ResponseEntity.ok(response);
    }


}
