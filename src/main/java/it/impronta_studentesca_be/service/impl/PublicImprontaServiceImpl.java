package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.security.PersonaUserDetails;
import it.impronta_studentesca_be.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublicImprontaServiceImpl implements PublicImprontaService {

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private CorsoDiStudiService corsoDiStudiService;

    @Autowired
    private UfficioService ufficioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

    @Autowired
    private DirettivoService direttivoService;

    @Autowired
    private OrganoRappresentanzaService organoRappresentanzaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO DIPARTIMENTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public List<DipartimentoResponseDTO> getDipartimenti() {

        return dipartimentoService.getAll().stream().map(DipartimentoResponseDTO::new).collect(Collectors.toList());

    }

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public DipartimentoResponseDTO getDipartimentoById(Long dipartimentoId) {
        return new DipartimentoResponseDTO(dipartimentoService.getById(dipartimentoId));
    }

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public DipartimentoResponseDTO getDipartimentoByCorsoId(Long corsoId) {
        corsoDiStudiService.checkExistById(corsoId);
        return getCorsoById(corsoId).getDipartimento();

    }

    /*
    TESTATO 05/12/2025 FUNZIONA
     */
    @Override
    public DipartimentoResponseDTO getDipartimentoByPersonaId(Long personaId) {
        personaService.checkExistById(personaId);
        return getCorsoByPersonaId(personaId).getDipartimento();
    }


    ////////////////////////////////////////////////////////////////////////////
    /// FINE DIPARTIMENTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO CORSI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public List<CorsoDiStudiResponseDTO> getCorsiByDipartimento(Long dipartimentoId) {
        dipartimentoService.checkExistById(dipartimentoId);
        return corsoDiStudiService.getByDipartimento(dipartimentoId).stream().map(CorsoDiStudiResponseDTO::new).collect(Collectors.toList());
    }

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public CorsoDiStudiResponseDTO getCorsoById(Long corsoId) {
        return new CorsoDiStudiResponseDTO(corsoDiStudiService.getById(corsoId));
    }

    /*
    TESTATO 04/12/2025 FUNZIONA
     */
    @Override
    public CorsoDiStudiResponseDTO getCorsoByPersonaId(Long personaId) {
        personaService.checkExistById(personaId);
        return new CorsoDiStudiResponseDTO(personaService.getById(personaId).getCorsoDiStudi());
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FINE CORSI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO UFFICI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////
    @Override
    public List<UfficioResponseDTO> getUffici() {

        return ufficioService.getAll().stream().map(UfficioResponseDTO::new).collect(Collectors.toList());

    }


    ////////////////////////////////////////////////////////////////////////////
    /// FINE UFFICI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO PERSONE//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////



    /*
    TESTATO 04/12/2025 FUNZIONA
     */
    @Override
    public PersonaResponseDTO getPersonaById(Long personaId) {
        return new PersonaResponseDTO(personaService.getById(personaId));
    }


    @Override
    public List<PersonaResponseDTO> getStaff() {

        return personaService.getStaff().stream().map(PersonaResponseDTO::new).collect(Collectors.toList());

    }

    /*
    TESTATO 05/12/2025 FUNZIONA
     */
    @Override
    public List<PersonaResponseDTO> getPersoneByDipartimento(Long dipartimentoId) {
        return personaService.getByDipartimento(dipartimentoId).stream().map(PersonaResponseDTO::new).collect(Collectors.toList());
    }

    /*
    TESTATO 05/12/2025 FUNZIONA
     */
    @Override
    public List<PersonaResponseDTO> getPersoneByCorso(Long corsoId) {
        return personaService.getByCorsoDiStudi(corsoId).stream().map(PersonaResponseDTO::new).collect(Collectors.toList());
    }


    ////////////////////////////////////////////////////////////////////////////
    /// FINE PERSONA//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    @Override
    public OrganoRappresentanzaDTO getOrganoById(Long organoId) {
        return new OrganoRappresentanzaDTO(organoRappresentanzaService.getById(organoId));
    }

    @Override
    public List<OrganoRappresentanzaDTO> getOrganoAll() {
        return organoRappresentanzaService.getAll().stream().map(OrganoRappresentanzaDTO::new).collect(Collectors.toList());
    }
    ////////////////////////////////////////////////////////////////////////////
    /// FINE ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO RAPPRESENTANTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    @Override
    public PersonaRappresentanzaResponseDTO getRappresentanteById(Long id) {
        return new PersonaRappresentanzaResponseDTO(personaRappresentanzaService.getById(id));
    }

    @Override
    public PersonaConRappresentanzeResponseDTO getRappresentanteByPersona(Long personaId) {
        // Verifico che la persona esista (se non esiste, eccezione)
        personaService.checkExistById(personaId);

        // Tutte le righe persona_rappresentanza per quella persona
        List<PersonaRappresentanza> rappresentanze =
                personaRappresentanzaService.getByPersona(personaId);

        // Persona ricavata direttamente dalla prima rappresentanza (se c’è)
        PersonaResponseDTO personaDTO = rappresentanze.stream()
                .findFirst()
                .map(PersonaRappresentanza::getPersona)      // Persona
                .map(PersonaResponseDTO::new)                // -> PersonaResponseDTO
                // se non ha nessuna rappresentanza ma esiste comunque nel DB:
                .orElseGet(() -> new PersonaResponseDTO(personaService.getById(personaId)));

        // Mappo ogni PersonaRappresentanza in un DTO “ruolo”
        List<RuoloRappresentanzaDTO> ruoli = rappresentanze.stream()
                .map(pr -> RuoloRappresentanzaDTO.builder()
                        .id(pr.getId())
                        .organo(new OrganoRappresentanzaDTO(pr.getOrganoRappresentanza()))
                        .dataInizio(pr.getDataInizio())
                        .dataFine(pr.getDataFine())
                        .build()
                )
                .toList();

        // Costruisco il DTO aggregato
        return PersonaConRappresentanzeResponseDTO.builder()
                .persona(personaDTO)
                .ruoli(ruoli)
                .build();
    }

    @Override
    public List<PersonaRappresentanzaResponseDTO> getRappresentanteByOrgano(Long organoId) {

        organoRappresentanzaService.checkExistById(organoId);
        return personaRappresentanzaService.getByOrganoId(organoId).stream().map(PersonaRappresentanzaResponseDTO::new).toList();
    }

    @Override
    public List<PersonaConRappresentanzeResponseDTO> getRappresentanteAll() {

        // Recupero tutte le righe persona_rappresentanza
        List<PersonaRappresentanza> rappresentanze = personaRappresentanzaService.getAll();

        // Raggruppo per persona (uso l'id per sicurezza)
        Map<Long, List<PersonaRappresentanza>> byPersona =
                rappresentanze.stream()
                        .collect(Collectors.groupingBy(pr -> pr.getPersona().getId()));

        // Per ogni persona costruisco il DTO aggregato
        return byPersona.values().stream()
                .map(listaPerPersona -> {

                    // Persona (è la stessa per tutta la lista)
                    Persona persona = listaPerPersona.get(0).getPersona();
                    PersonaResponseDTO personaDTO = new PersonaResponseDTO(persona);

                    // Tutti i ruoli di quella persona, ORDINATI per organo
                    List<RuoloRappresentanzaDTO> ruoli = listaPerPersona.stream()
                            .map(pr -> RuoloRappresentanzaDTO.builder()
                                    .id(pr.getId())
                                    .organo(new OrganoRappresentanzaDTO(pr.getOrganoRappresentanza()))
                                    .dataInizio(pr.getDataInizio())
                                    .dataFine(pr.getDataFine())
                                    .build()
                            )
                            // qui scegli tu se ordinare per codice o per nome
                            .sorted(Comparator.comparing(r ->
                                    r.getOrgano().getCodice()   // oppure getNome()
                            ))
                            .toList();

                    return PersonaConRappresentanzeResponseDTO.builder()
                            .persona(personaDTO)
                            .ruoli(ruoli)
                            .build();
                })
                .toList();
    }


    ////////////////////////////////////////////////////////////////////////////
    /// FINE RAPPRESENTANTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    /// FINE ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO PERSONA_DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<PersonaDirettivoResponseDTO> getMembriDirettivo(Long direttivoId) {
        direttivoService.checkExistById(direttivoId);
        return personaDirettivoService.getByDirettivo(direttivoId).stream().map(PersonaDirettivoResponseDTO::new).collect(Collectors.toList());
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FINE PERSONA_DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public DirettivoResponseDTO getDirettivoById(Long personaId) {
        return new DirettivoResponseDTO(direttivoService.getById(personaId));
    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<DirettivoResponseDTO> getDirettivi() {

        return direttivoService.getAll().stream().map(DirettivoResponseDTO::new).collect(Collectors.toList());

    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<DirettivoResponseDTO> getDirettiviByTipo(TipoDirettivo tipo) {

        return direttivoService.getByTipo(tipo).stream().map(DirettivoResponseDTO::new).collect(Collectors.toList());

    }

    //TODO: QUANDO CI SARANNO DIRETTIVI DIPARTIMENTALI
    @Override
    public List<DirettivoResponseDTO> getDirettiviByDipartimento(Long dipartimentoId) {

        return direttivoService.getByDipartimento(dipartimentoId).stream().map(DirettivoResponseDTO::new).collect(Collectors.toList());

    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<DirettivoResponseDTO> getDirettiviInCarica() {

        return direttivoService.getDirettiviInCarica().stream().map(DirettivoResponseDTO::new).collect(Collectors.toList());

    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo) {

        return direttivoService.getByTipo(tipo).stream()
                .filter(direttivo -> direttivo.isAttivo()).map(DirettivoResponseDTO::new).collect(Collectors.toList());

    }
    ////////////////////////////////////////////////////////////////////////////
    /// FINE DIRETTIVO//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO SICUREZZA //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    @Override
    public void creaPassword(Long personaId, String password) {

        // recupero la persona
        Persona persona = personaService.getById(personaId);

        // controllo che la nuova password sia valorizzata
        if (password == null || password.isBlank()) {
            log.error("LA PASSWORD NON PUO' ESSERE VUOTA: {}", password);
            throw new IllegalArgumentException("La password non può essere vuota.");
        }

        // se ha già una password, non permetto di "crearla" di nuovo
        if (persona.getPassword() != null && !persona.getPassword().isBlank()) {
            log.error("LA PASSWORD E' GIA' STATA CREATA: {}", password);
            throw new IllegalStateException("La password è già stata impostata per questa persona. Usa il reset.");
        }

        // creo/imposto la password codificata
        persona.setPassword(passwordEncoder.encode(password));
        personaService.update(persona);
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        try {
            log.info("L'UTENTE {} STA PROVANDO A LOGGARSI ", request.getEmail().trim().toLowerCase(Locale.ROOT));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );


            // Principal = il nostro PersonaUserDetails
            PersonaUserDetails userDetails = (PersonaUserDetails) authentication.getPrincipal();
            if (userDetails == null || userDetails.getPersona() == null) {
                log.error("Tentativo di login fallito per email {}", request.getEmail());
                throw new BadCredentialsException("IMPOSSIBILE AUTENTICARSI");
            }
            Persona persona = userDetails.getPersona();

            Set<String> ruoli = persona.getRuoli().stream()
                    .map(Ruolo::getNome)                 // enum Roles
                    .map(Roles::getAuthority)          // "DIRETTIVO", "USER", ...
                    .collect(Collectors.toSet());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setId(persona.getId());
            response.setNome(persona.getNome());
            response.setCognome(persona.getCognome());
            response.setEmail(persona.getEmail());
            response.setRuoli(ruoli);

            return response;

        } catch (BadCredentialsException ex) {
            log.error("Tentativo di login fallito per email {}", request.getEmail());
            throw new BadCredentialsException("IMPOSSIBILE AUTENTICARSI");
        }
    }
}
