package it.impronta_studentesca_be.controller;

import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.dto.ImageUploadResponseDTO;
import it.impronta_studentesca_be.dto.PersonaRequestDTO;
import it.impronta_studentesca_be.dto.PersonaResponseDTO;
import it.impronta_studentesca_be.dto.StaffCardDTO;
import it.impronta_studentesca_be.service.AdminImprontaService;
import it.impronta_studentesca_be.service.SecurityPersonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PATH + "/" + ApiPath.STAFF_PATH)
public class StaffController {

    @Autowired
    AdminImprontaService adminImprontaService;

    @Autowired
    SecurityPersonaService securityPersonaService;

    @GetMapping(ApiPath.ALL_PATH)
    public ResponseEntity<List<StaffCardDTO>> getStaff() {
        return ResponseEntity.ok(adminImprontaService.getStaffCards());
    }

    @PutMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> aggiornaPersona(@RequestBody PersonaRequestDTO persona) throws AccessDeniedException {
        securityPersonaService.checkCanManagePersona(persona.getId());
        adminImprontaService.aggiornaPersona(persona);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/persona/{personaId}")
    public ResponseEntity<Void> eliminaPersona(@PathVariable Long personaId) throws AccessDeniedException {
        securityPersonaService.checkCanManagePersona(personaId);
        adminImprontaService.eliminaPersona(personaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/persona/{personaId}/foto")
    public ResponseEntity<ImageUploadResponseDTO> uploadFotoPersona(@PathVariable Long personaId,
                                                                    @RequestParam("file") MultipartFile file) throws AccessDeniedException {
        securityPersonaService.checkCanManagePersona(personaId);
        ImageUploadResponseDTO response = adminImprontaService.uploadFotoPersona(personaId, file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/persona/{personaId}/foto")
    public ResponseEntity<ImageUploadResponseDTO> deleteFotoPersona(@PathVariable Long personaId) throws AccessDeniedException {
        securityPersonaService.checkCanManagePersona(personaId);
        adminImprontaService.deleteFotoPersona(personaId);
        return ResponseEntity.ok().build();
    }


}
