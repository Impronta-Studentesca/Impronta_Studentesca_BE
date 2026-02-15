package it.impronta_studentesca_be.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendLinkPasswordUtente(Long personaId, String mailPersona, String nomePersona, boolean isModifica);
}
