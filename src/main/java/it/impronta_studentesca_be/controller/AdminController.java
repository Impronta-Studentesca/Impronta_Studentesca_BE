package it.impronta_studentesca_be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.service.AdminImprontaService;
import it.impronta_studentesca_be.service.PublicImprontaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PATH + "/" + ApiPath.ADMIN_PATH)
public class AdminController {

    @Autowired
    AdminImprontaService  adminImprontaService;

    @Autowired
    PublicImprontaService publicImprontaService;

    //PERSONA

    @PostMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> creaPersona(@RequestBody PersonaRequestDTO persona) {
        PersonaResponseDTO response = adminImprontaService.creaPersona(persona);
        return ResponseEntity.ok(response);
    }


    // DIRETTIVO

    @GetMapping("/" + ApiPath.DIRETTIVI_PATH )
    public ResponseEntity<List<DirettivoResponseDTO>> getDirettivi() {
        return ResponseEntity.ok(publicImprontaService.getDirettivi());
    }

    @PostMapping("/" + ApiPath.DIRETTIVO_PATH)
    public ResponseEntity<Void> aggiungiADirettivo(@RequestBody DirettivoRequestDTO direttivoRequestDTO) {
        adminImprontaService.creaDirettivo(direttivoRequestDTO);
    return ResponseEntity.ok().build();
    }

    @PutMapping("/" + ApiPath.DIRETTIVO_PATH)
    public ResponseEntity<Void> modificaADirettivo(@RequestBody DirettivoRequestDTO direttivoRequestDTO) {
        adminImprontaService.aggiornaDirettivo(direttivoRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/" + ApiPath.DIRETTIVO_PATH + "/{direttivoId}")
    public ResponseEntity<Void> eliminaADirettivo(@PathVariable Long direttivoId) {
        adminImprontaService.eliminaDirettivo(direttivoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/" + ApiPath.DIRETTIVO_PATH + '/' + ApiPath.PERSONA_PATH)
    public ResponseEntity<Void> assegnaPersonaADirettivo(@RequestBody PersonaDirettivoRequestDTO personaDirettivoRequestDTO) {
        adminImprontaService.assegnaPersonaADirettivo(personaDirettivoRequestDTO.getPersonaId(), personaDirettivoRequestDTO.getDirettivoId(), personaDirettivoRequestDTO.getRuoloNelDirettivo());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/" + ApiPath.DIRETTIVO_PATH + '/' + ApiPath.PERSONA_PATH)
    public ResponseEntity<Void> modificaPersonaADirettivo(@RequestBody PersonaDirettivoRequestDTO personaDirettivoRequestDTO) {
        adminImprontaService.modificaPersonaADirettivo(personaDirettivoRequestDTO.getPersonaId(), personaDirettivoRequestDTO.getDirettivoId(), personaDirettivoRequestDTO.getRuoloNelDirettivo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/" + ApiPath.DIRETTIVO_PATH + '/' + ApiPath.PERSONA_PATH+ "/{personaId}/{direttivoId}")
    public ResponseEntity<Void> rimuoviPersonaDaDirettivo(@PathVariable Long personaId,
                                                          @PathVariable Long direttivoId) {
        adminImprontaService.rimuoviPersonaDaDirettivo(personaId, direttivoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/" + ApiPath.DIRETTIVO_PATH + "/ruolo/{ruolo}/non-presenti-direttivo/{direttivoId}")
    public ResponseEntity<List<PersonaMiniDTO>> getPersoneByRuoloNonPresentiNelDirettivo(
            @PathVariable Roles ruolo,
            @PathVariable Long direttivoId
    ) {
        return ResponseEntity.ok(
                adminImprontaService.getPersoneByRuoloNonPresentiNelDirettivoId(ruolo, direttivoId)
        );
    }

    // ORGANI DI RAPPRESENTANZA

    @PostMapping("/" + ApiPath.RAPPRESENTANTE_PATH)
    public ResponseEntity<Void> assegnaPersonaAOrgano(@RequestBody PersonaRappresentanzaRequestDTO personaRappresentanzaRequestDTO) {
        adminImprontaService.assegnaPersonaRappresentanza(personaRappresentanzaRequestDTO.getPersonaId(), personaRappresentanzaRequestDTO.getOrganoRappresentanzaId(), personaRappresentanzaRequestDTO.getDataInizio(), personaRappresentanzaRequestDTO.getDataFine());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/" + ApiPath.RAPPRESENTANTE_PATH)
    public ResponseEntity<Void> modificaPersonaAOrgano(@RequestBody PersonaRappresentanzaRequestDTO personaRappresentanzaRequestDTO) {
        adminImprontaService.modificaPersonaRappresentanza(personaRappresentanzaRequestDTO.getPersonaId(), personaRappresentanzaRequestDTO.getOrganoRappresentanzaId(), personaRappresentanzaRequestDTO.getDataInizio(), personaRappresentanzaRequestDTO.getDataFine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/" + ApiPath.RAPPRESENTANTE_PATH + "/{personaRappresentanzaId}")
    public ResponseEntity<Void> eliminaPersonaRappresentanza(@PathVariable Long personaRappresentanzaId) {
        adminImprontaService.eliminaPersonaRappresentanza(personaRappresentanzaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/" + ApiPath.ORGANI_PATH)
    public ResponseEntity<List<OrganoRappresentanzaDTO>> getOrganoAll(
    ) {
        return ResponseEntity.ok(publicImprontaService.getOrganoAll());
    }

    // DIPARTIMENTI

    @PostMapping("/dipartimento")
    public ResponseEntity<DipartimentoResponseDTO> creaDipartimento(@RequestBody DipartimentoRequestDTO dipartimento) {
        DipartimentoResponseDTO response = adminImprontaService.creaDipartimento(dipartimento);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/dipartimento")
    public ResponseEntity<DipartimentoResponseDTO> modificaDipartimento(@RequestBody DipartimentoRequestDTO dipartimento) {
        DipartimentoResponseDTO response = adminImprontaService.modificaDipartimento(dipartimento);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/dipartimento")
    public ResponseEntity eliminaDipartimento(@RequestBody DipartimentoRequestDTO dipartimento) {
         adminImprontaService.eliminaDipartimento(dipartimento);
        return ResponseEntity.ok().build();
    }

    // CORSI DI STUDIO

    @PostMapping("/corso")
    public ResponseEntity<CorsoDiStudiResponseDTO> creaCorso(@RequestBody CorsoDiStudiRequestDTO corso) {
        CorsoDiStudiResponseDTO response = adminImprontaService.creaCorso(corso);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/corso")
    public ResponseEntity<CorsoDiStudiResponseDTO> modificaCorso(@RequestBody CorsoDiStudiRequestDTO corso) {
        CorsoDiStudiResponseDTO response = adminImprontaService.modificaCorso(corso);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/corso")
    public ResponseEntity eliminaCorso(@RequestBody CorsoDiStudiRequestDTO corso) {
        adminImprontaService.eliminaCorso(corso);
        return ResponseEntity.ok().build();
    }

    // UFFICI

    @PostMapping("/ufficio")
    public ResponseEntity<UfficioResponseDTO> creaUfficio(@RequestBody UfficioRequestDTO ufficio) {
        UfficioResponseDTO response = adminImprontaService.creaUfficio(ufficio);
        return ResponseEntity.ok(response);
    }


}
