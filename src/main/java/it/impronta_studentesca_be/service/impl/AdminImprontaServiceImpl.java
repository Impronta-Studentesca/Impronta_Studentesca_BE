package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.service.*;
import it.impronta_studentesca_be.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminImprontaServiceImpl implements AdminImprontaService {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private CorsoDiStudiService corsoDiStudiService;

    @Autowired
    private UfficioService ufficioService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private DirettivoService direttivoService;


    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private Mapper mapper;


    @Override
    public PersonaResponseDTO creaPersona(PersonaRequestDTO persona) {

        persona.setId(null);
        return new PersonaResponseDTO(personaService.create(mapper.toPersona(persona)));

    }

    @Override
    public PersonaResponseDTO aggiornaPersona(PersonaRequestDTO persona) {

        return new PersonaResponseDTO(personaService.update(mapper.toPersona(persona)));

    }

    @Override
    public void eliminaPersona(Long personaId) {

        personaService.delete(personaId);

    }

    @Override
    public ImageUploadResponseDTO uploadFotoPersona(Long personaId, MultipartFile file) {

        // 1. Recupero persona
        Persona persona = personaService.getById(personaId);

        // 2. Carico l’immagine su ImageKit usando il service che hai già
        ImageUploadResponseDTO uploadResult = imageStorageService.uploadPersonaPhoto(personaId, file);

        // 3. Aggiorno la persona con le info dell’immagine
        persona.setFotoUrl(uploadResult.getUrl());
        persona.setFotoThumbnailUrl(uploadResult.getThumbnail());
        persona.setFotoFileId(uploadResult.getFileId());

        personaService.update(persona);

        // 4. Ritorno al FE i dati dell’immagine (url, thumbnail, ecc.)
        return uploadResult;
    }

    @Override
    public void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo) {

        personaService.checkExistById(personaId);
        direttivoService.checkExistById(direttivoId);

        personaDirettivoService.addPersonaToDirettivo(personaService.getById(personaId), direttivoService.getById(direttivoId), ruolo);

    }

    @Override
    public void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId) {
        personaService.checkExistById(personaId);
        direttivoService.checkExistById(direttivoId);
        personaDirettivoService.removePersonaFromDirettivo(personaService.getById(personaId), direttivoService.getById(direttivoId));

    }


    @Override
    public void eliminaPersonaRappresentanza(Long personaRappresentanzaId) {

        personaRappresentanzaService.delete(personaRappresentanzaId);

    }

    @Override
    public DipartimentoResponseDTO creaDipartimento(DipartimentoRequestDTO dipartimento) {

        return new DipartimentoResponseDTO(dipartimentoService.create(mapper.toDipartimento(dipartimento)));

    }

    @Override
    public CorsoDiStudiResponseDTO creaCorso(CorsoDiStudiRequestDTO corso) {

        return new CorsoDiStudiResponseDTO(corsoDiStudiService.create(mapper.toCorsoDiSudi(corso)));

    }

    @Override
    public UfficioResponseDTO creaUfficio(UfficioRequestDTO ufficio) {
        return new UfficioResponseDTO(ufficioService.create(mapper.toUfficio(ufficio)));
    }


}
