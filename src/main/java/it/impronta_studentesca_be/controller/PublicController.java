package it.impronta_studentesca_be.controller;


import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.service.PublicImprontaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/" +ApiPath.BASE_PATH + "/" + ApiPath.PUBLIC_PATH)
public class PublicController {

    @Autowired
    private PublicImprontaService publicImprontaService;

    // DIPARTIMENTI

    @GetMapping("/" + ApiPath.DIPARTIMENTI_PATH + "/" + ApiPath.ALL_PATH)
    public ResponseEntity<List<DipartimentoResponseDTO>> getDipartimenti() {
        return ResponseEntity.ok(publicImprontaService.getDipartimenti());
    }

    @GetMapping("/" + ApiPath.DIPARTIMENTO_PATH + "/{dipartimentoId}")
    public ResponseEntity<DipartimentoResponseDTO> getDipartimentoById(
            @PathVariable Long dipartimentoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getDipartimentoById(dipartimentoId));
    }

    @GetMapping( "/" + ApiPath.CORSO_PATH + "/{corsoId}/" + ApiPath.DIPARTIMENTO_PATH)
    public ResponseEntity<DipartimentoResponseDTO> getDipartimentoByCorsoId(
            @PathVariable Long corsoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getDipartimentoByCorsoId(corsoId));
    }

    @GetMapping( "/" + ApiPath.PERSONA_PATH + "/{personaId}/" + ApiPath.DIPARTIMENTO_PATH)
    public ResponseEntity<DipartimentoResponseDTO> getDipartimentoByPersonaId(
            @PathVariable Long personaId
    ) {
        return ResponseEntity.ok(publicImprontaService.getDipartimentoByPersonaId(personaId));
    }


    // CORSI DI STUDIO

    @GetMapping("/" + ApiPath.DIPARTIMENTO_PATH + "/{dipartimentoId}/" + ApiPath.CORSI_PATH)
    public ResponseEntity<List<CorsoDiStudiResponseDTO>> getCorsiByDipartimento(
            @PathVariable Long dipartimentoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getCorsiByDipartimento(dipartimentoId));
    }

    @GetMapping("/" + ApiPath.PERSONA_PATH + "/{personaId}/" + ApiPath.CORSO_PATH)
    public ResponseEntity<CorsoDiStudiResponseDTO> getCorsoByPersona(
            @PathVariable Long personaId
    ) {
        return ResponseEntity.ok(publicImprontaService.getCorsoByPersonaId(personaId));
    }


    @GetMapping("/" + ApiPath.CORSO_PATH + "/{corsoId}")
    public ResponseEntity<CorsoDiStudiResponseDTO> getCorsoById(
            @PathVariable Long corsoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getCorsoById(corsoId));
    }


    // UFFICI

    @GetMapping("/uffici/all")
    public ResponseEntity<List<UfficioResponseDTO>> getUffici() {
        return ResponseEntity.ok(publicImprontaService.getUffici());
    }

    // PERSONE / STAFF


    @GetMapping("/persona/{personaId}/foto")
    public ResponseEntity<PersonaPhotoResponseDTO> getFotoPersona(@PathVariable Long personaId) {
        return ResponseEntity.ok(publicImprontaService.getFotoPersona(personaId));
    }

    @GetMapping("/" + ApiPath.PERSONA_PATH + "/{personaId}")
    public ResponseEntity<PersonaResponseDTO> getPersonaById(
            @PathVariable Long personaId
    ) {
        return ResponseEntity.ok(publicImprontaService.getPersonaById(personaId));
    }

    @GetMapping("/" + ApiPath.STAFF_PATH + "/" + ApiPath.ALL_PATH)
    public ResponseEntity<List<PersonaResponseDTO>> getStaff() {
        return ResponseEntity.ok(publicImprontaService.getStaff());
    }

    @GetMapping("/" + ApiPath.DIPARTIMENTO_PATH + "/{dipartimentoId}/" +  ApiPath.PERSONE_PATH)
    public ResponseEntity<List<PersonaResponseDTO>> getPersoneByDipartimento(
            @PathVariable Long dipartimentoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getPersoneByDipartimento(dipartimentoId));
    }

    @GetMapping("/" + ApiPath.CORSO_PATH + "/{corsoId}/" + ApiPath.PERSONE_PATH)
    public ResponseEntity<List<PersonaResponseDTO>> getPersoneByCorso(
            @PathVariable Long corsoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getPersoneByCorso(corsoId));
    }


    //DIRETTIVO

    @GetMapping("/" + ApiPath.DIRETTIVO_PATH + "/{direttivoId}")
    public ResponseEntity<DirettivoResponseDTO> getDirettivoById(
            @PathVariable Long direttivoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getDirettivoById(direttivoId));
    }


    @GetMapping("/" + ApiPath.TIPO_PATH + "/{tipo}/" + ApiPath.DIRETTIVI_PATH)
    public ResponseEntity<List<DirettivoResponseDTO>> getDirettivoById(
            @PathVariable String tipo
    ) {
        return ResponseEntity.ok(publicImprontaService.getDirettiviByTipo(TipoDirettivo.valueOf(tipo)));
    }

    @GetMapping("/" + ApiPath.DIPARTIMENTO_PATH + "/{dipartimentoId}/" +  ApiPath.DIRETTIVI_PATH)
    public ResponseEntity<List<DirettivoResponseDTO>> getDirettiviByDipartimento(
            @PathVariable Long dipartimentoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getDirettiviByDipartimento(dipartimentoId));
    }

    @GetMapping("/" + ApiPath.DIRETTIVI_PATH + "/" + ApiPath.IN_CARICA_PATH)
    public ResponseEntity<List<DirettivoResponseDTO>> getDirettiviInCarica() {
        return ResponseEntity.ok(publicImprontaService.getDirettiviInCarica());
    }

    @GetMapping("/" + ApiPath.TIPO_PATH + "/{tipo}/" + ApiPath.DIRETTIVI_PATH + "/" + ApiPath.IN_CARICA_PATH)
    public ResponseEntity<List<DirettivoResponseDTO>> getDirettiviByTipoInCarica(
            @PathVariable TipoDirettivo tipo
    ) {
        return ResponseEntity.ok(publicImprontaService.getDirettiviByTipoInCarica(tipo));
    }


    // PERSONA_DIRETTIVO

    @GetMapping("/" + ApiPath.DIRETTIVO_PATH + "/{direttivoId}/" + ApiPath.PERSONE_PATH)
    public ResponseEntity<List<PersonaDirettivoResponseDTO>> getMembriDirettivo(
            @PathVariable Long direttivoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getMembriDirettivo(direttivoId));
    }


    //ORGANO DI RAPPRESENTANZA

    @GetMapping("/" + ApiPath.ORGANO_PATH + "/{organoId}/")
    public ResponseEntity<OrganoRappresentanzaDTO> getOrganoById(
            @PathVariable Long organoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getOrganoById(organoId));
    }


    // RAPPRESENTANTI

    @GetMapping("/" + ApiPath.RAPPRESENTANTE_PATH + "/{rappresentanteId}")
    public ResponseEntity<PersonaRappresentanzaResponseDTO> getPersonaRappresentanzaById(
            @PathVariable Long rappresentanteId
    ) {
        return ResponseEntity.ok(publicImprontaService.getPersonaRappresentanzaById(rappresentanteId));
    }


    @GetMapping("/" + ApiPath.PERSONA_PATH + "/{personaId}/" + ApiPath.RAPPRESENTANTE_PATH)
    public ResponseEntity<PersonaConRappresentanzeResponseDTO> getRappresentanteByPersona(
            @PathVariable Long personaId
    ) {
        return ResponseEntity.ok(publicImprontaService.getRappresentanteByPersona(personaId));
    }

    @GetMapping("/" + ApiPath.ORGANO_PATH + "/{organoId}/" + ApiPath.RAPPRESENTANTE_PATH)
    public ResponseEntity<List<PersonaRappresentanzaResponseDTO>> getRappresentanteByOrgano(
            @PathVariable Long organoId
    ) {
        return ResponseEntity.ok(publicImprontaService.getRappresentanteByOrgano(organoId));
    }


    @GetMapping("/" + ApiPath.RAPPRESENTANTI_PATH)
    public ResponseEntity<List<PersonaConRappresentanzeResponseDTO>> getRappresentanteAll(
    ) {
        return ResponseEntity.ok(publicImprontaService.getRappresentanteAll());
    }

}
