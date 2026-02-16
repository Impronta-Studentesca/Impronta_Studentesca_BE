package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.service.EmailService;
import it.impronta_studentesca_be.service.PasswordTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;
import java.util.Locale;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final String MAILJET_SEND_URL = "https://api.mailjet.com/v3.1/send";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PasswordTokenService passwordTokenService;

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.api.secret}")
    private String apiSecret;

    @Value("${mailjet.sender.email}")
    private String fromEmail;

    @Value("${mailjet.sender.name}")
    private String fromName;

    @Value("${mailjet.reply.email}")
    private String replyEmail;

    @Value("${mailjet.reply.name}")
    private String replyName;


    @Value("${app.backoffice.base-url}")
    private String baseUrl;

    @Value("${app.backoffice.password-path}")
    private String passwordPath;

    @Async
    @Override
    public void sendLinkPasswordUtente(Long personaId, String mailPersona, String nomePersona, boolean isModifica) {

        log.info("INIZIO INVIO EMAIL LINK PASSWORD - PERSONA_ID={} - EMAIL={} - NOME={} - IS_MODIFICA={}",
                personaId, safe(mailPersona), safe(nomePersona), isModifica);

        try {
            if (personaId == null) throw new IllegalArgumentException("PERSONA_ID NULL");
            if (mailPersona == null || mailPersona.isBlank()) throw new IllegalArgumentException("EMAIL MANCANTE");

            String email = mailPersona.trim().toLowerCase(Locale.ROOT);

            // route FE: /password/{azione}/{personaId}?token=...
            String action = isModifica ? "modifica" : "crea";

            // 1) token
            String token = passwordTokenService.createPasswordToken(personaId, isModifica);

            // 2) link FE
            String link = buildPasswordLink(action, personaId, token);

            // 3) contenuto
            final String subject;
            final String html;
            final String text;

            if (!isModifica) {
                subject = "Benvenuto/a nello Staff di Impronta Studentesca – Attiva la tua utenza";
                html = buildWelcomeHtml(link, nomePersona);
                text = buildWelcomeText(link, nomePersona);
            } else {
                subject = "Modifica password – Impronta Studentesca";
                html = buildResetHtml(link, nomePersona);
                text = buildResetText(link, nomePersona);
            }

            // 4) logo inline
            Object[] inlined = buildInlineLogoAttachment();

            // 5) invio (FIX: ora passo toName + TextPart)
            sendMailjetHtml(
                    email,
                    nomePersona,     // toName
                    subject,
                    html,
                    text,
                    inlined
            );

            log.info("FINE INVIO EMAIL LINK PASSWORD OK - PERSONA_ID={} - EMAIL={}", personaId, email);

        } catch (Exception e) {
            log.error("ERRORE INVIO EMAIL LINK PASSWORD - PERSONA_ID={} - EMAIL={}",
                    personaId, safe(mailPersona), e);
        }
    }

    private String buildPasswordLink(String action, Long personaId, String token) {
        String path = (passwordPath == null || passwordPath.isBlank()) ? "/password" : passwordPath.trim();
        if (!path.startsWith("/")) path = "/" + path;
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(path)
                .path("/")
                .path(action)
                .path("/")
                .path(personaId.toString())
                .queryParam("token", token)
                .build(true)
                .toUriString();
    }

    private String buildWelcomeHtml(String link, String nomePersona) {
        String saluto = buildSaluto(nomePersona);

        return """
<div style="font-family: Arial, sans-serif; line-height: 1.7; background:#f6f7fb; padding:20px;">
  <div style="max-width: 640px; margin: 0 auto; background:#ffffff; border-radius:14px; overflow:hidden; box-shadow:0 8px 26px rgba(0,0,0,0.08);">

    <div style="padding:14px 18px; display:flex; align-items:center; gap:12px; background:#ffffff;">
      <img src="cid:logo-impronta" alt="Impronta Studentesca"
           style="height:24px; width:auto; display:block;" />
      <div>
        <div style="font-size: 12px; color:#6b7280; margin-bottom:2px;">Segreteria</div>
        <div style="font-size: 16px; font-weight:800; color:#111827;">Impronta Studentesca</div>
      </div>
    </div>

    <div style="padding: 0 18px 18px;">
      <div style="height:1px; background:#e5e7eb; margin: 6px 0 16px;"></div>

      <p style="margin:0 0 14px;"><strong>%s</strong></p>

      <p style="margin:0 0 14px;"><strong>Benvenuto/a nello Staff di Impronta Studentesca.</strong></p>

      <p style="margin:0 0 14px;">
        Siamo felici di comunicarti il tuo ingresso nello staff: è un passo importante che ci rende orgogliosi e che contribuisce
        a rafforzare il lavoro quotidiano dell’associazione, fatto di impegno, collaborazione e attenzione concreta verso le esigenze degli studenti.
      </p>

      <p style="margin:0 0 14px;">
        Nei prossimi giorni riceverai tutte le informazioni utili per iniziare al meglio: canali di comunicazione, organizzazione interna,
        attività in programma e modalità operative. L’obiettivo è permetterti di inserirti con serenità e sentirti subito parte del gruppo,
        con la possibilità di contribuire in modo graduale e secondo le tue disponibilità.
      </p>

      <p style="margin:0 0 14px;">
        Per qualsiasi dubbio o necessità, la Segreteria resta a disposizione: puoi contattarci in qualunque momento.
      </p>

      <p style="margin:0 0 16px;">
        Ancora benvenuto/a nello staff. 💙❤️
      </p>

      <div style="margin: 16px 0; padding: 14px; background:#f3f4f6; border-radius: 12px;">
        <p style="margin:0 0 10px; color:#111827;">
          <strong>Attivazione utenza</strong><br/>
          Per attivare la tua utenza è necessario creare la password personale tramite il pulsante qui sotto.
        </p>

        <p style="margin: 12px 0 0;">
          <a href="%s"
             style="background:#111827;color:#ffffff;padding:12px 18px;border-radius:10px;text-decoration:none;display:inline-block;font-weight:800;">
            Crea password e attiva utenza
          </a>
        </p>
      </div>

      <p style="margin:0;">
        Cordiali saluti,<br/>
        <strong>Segreteria – Impronta Studentesca</strong>
      </p>

      <div style="height:1px; background:#e5e7eb; margin: 16px 0;"></div>
      <p style="margin:0; font-size: 12px; color:#6b7280;">
        Nota di sicurezza: questo link è personale e a tempo limitato. Se non riconosci questa richiesta, ignora questa email.
      </p>
    </div>
  </div>

  <div style="max-width: 640px; margin: 10px auto 0; font-size: 11px; color:#9ca3af; text-align:center;">
    Comunicazione automatica della Segreteria – Impronta Studentesca
  </div>
</div>
""".formatted(saluto, link);
    }

    private String buildResetHtml(String link, String nomePersona) {
        String saluto = buildSaluto(nomePersona);

        return """
<div style="font-family: Arial, sans-serif; line-height: 1.7; background:#f6f7fb; padding:20px;">
  <div style="max-width: 640px; margin: 0 auto; background:#ffffff; border-radius:14px; overflow:hidden; box-shadow:0 8px 26px rgba(0,0,0,0.08);">

    <div style="padding:14px 18px; display:flex; align-items:center; gap:12px; background:#ffffff;">
      <img src="cid:logo-impronta" alt="Impronta Studentesca"
           style="height:24px; width:auto; display:block;" />
      <div>
        <div style="font-size: 12px; color:#6b7280; margin-bottom:2px;">Segreteria</div>
        <div style="font-size: 16px; font-weight:800; color:#111827;">Impronta Studentesca</div>
      </div>
    </div>

    <div style="padding: 0 18px 18px;">
      <div style="height:1px; background:#e5e7eb; margin: 6px 0 16px;"></div>

      <p style="margin:0 0 14px;"><strong>%s</strong></p>

      <p style="margin:0 0 14px;"><strong>Richiesta di modifica password</strong></p>

      <p style="margin:0 0 14px;">
        È stata richiesta la modifica della password della tua utenza.
        Per procedere, utilizza il pulsante qui sotto.
      </p>

      <p style="margin: 12px 0 0;">
        <a href="%s"
           style="background:#111827;color:#ffffff;padding:12px 18px;border-radius:10px;text-decoration:none;display:inline-block;font-weight:800;">
          Modifica password
        </a>
      </p>

      <div style="height:1px; background:#e5e7eb; margin: 16px 0;"></div>

      <p style="margin:0;">
        Cordiali saluti,<br/>
        <strong>Segreteria – Impronta Studentesca</strong>
      </p>

      <p style="margin:12px 0 0; font-size: 12px; color:#6b7280;">
        Nota di sicurezza: se non riconosci questa richiesta, ignora questa email.
      </p>
    </div>
  </div>

  <div style="max-width: 640px; margin: 10px auto 0; font-size: 11px; color:#9ca3af; text-align:center;">
    Comunicazione automatica della Segreteria – Impronta Studentesca
  </div>
</div>
""".formatted(saluto, link);
    }

    private String buildWelcomeText(String link, String nomePersona) {
        String saluto = buildSalutoPlain(nomePersona);

        return saluto + "\n\n" +
                "Benvenuto/a nello Staff di Impronta Studentesca.\n\n" +
                "Siamo felici di comunicarti il tuo ingresso nello staff: è un passo importante che ci rende orgogliosi e che contribuisce " +
                "a rafforzare il lavoro quotidiano dell’associazione, fatto di impegno, collaborazione e attenzione concreta verso le esigenze degli studenti.\n\n" +
                "Nei prossimi giorni riceverai tutte le informazioni utili per iniziare al meglio: canali di comunicazione, organizzazione interna, " +
                "attività in programma e modalità operative.\n\n" +
                "Attivazione utenza: per attivare la tua utenza è necessario creare la password personale tramite questo link:\n" +
                link + "\n\n" +
                "Cordiali saluti,\n" +
                "Segreteria – Impronta Studentesca\n";
    }

    private String buildResetText(String link, String nomePersona) {
        String saluto = buildSalutoPlain(nomePersona);

        return saluto + "\n\n" +
                "Richiesta di modifica password.\n\n" +
                "È stata richiesta la modifica della password della tua utenza. Per procedere, utilizza questo link:\n" +
                link + "\n\n" +
                "Cordiali saluti,\n" +
                "Segreteria – Impronta Studentesca\n";
    }

    private String buildSaluto(String nomePersona) {
        if (nomePersona == null || nomePersona.isBlank()) return "Ciao,";
        String nome = escapeHtml(nomePersona.trim());
        return "Ciao " + nome + ",";
    }

    private String buildSalutoPlain(String nomePersona) {
        if (nomePersona == null || nomePersona.isBlank()) return "Ciao,";
        return "Ciao " + nomePersona.trim() + ",";
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private Object[] buildInlineLogoAttachment() {
        try {
            ClassPathResource res = new ClassPathResource("static/Logo_Impronta_round_160.png");
            byte[] bytes = StreamUtils.copyToByteArray(res.getInputStream());
            String b64 = Base64.getEncoder().encodeToString(bytes);

            Map<String, Object> inline = Map.of(
                    "ContentType", "image/png",
                    "Filename", "Logo_Impronta_round_160.png",
                    "Base64Content", b64,
                    "ContentID", "logo-impronta"
            );

            return new Object[]{inline};
        } catch (Exception e) {
            log.warn("IMPOSSIBILE CARICARE LOGO INLINE (static/Logo_Impronta_round_160.png). Invio email senza logo.", e);
            return new Object[0];
        }
    }

    private void sendMailjetHtml(
            String toEmail,
            String toName,
            String subject,
            String htmlBody,
            String textBody,
            Object[] inlinedAttachments
    ) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(apiKey, apiSecret);

            Map<String, Object> from = Map.of(
                    "Email", fromEmail,
                    "Name", fromName
            );

            Map<String, Object> to = Map.of(
                    "Email", toEmail,
                    "Name", (toName == null || toName.isBlank()) ? toEmail : toName
            );

            Map<String, Object> message = new HashMap<>();
            message.put("From", from);
            message.put("To", List.of(to));
            message.put("Subject", subject);
            message.put("HTMLPart", htmlBody);

            // per deliverability
            if (textBody != null && !textBody.isBlank()) {
                message.put("TextPart", textBody);
            }

            // Reply-To (rispondi a gmail)
            message.put("ReplyTo", Map.of(
                    "Email", replyEmail,
                    "Name", replyName
            ));

            // Tracking OFF (NON in Headers!)
            message.put("TrackOpens", "disabled");
            message.put("TrackClicks", "disabled");

            if (inlinedAttachments != null && inlinedAttachments.length > 0) {
                message.put("InlinedAttachments", List.of(inlinedAttachments));
            }

            Map<String, Object> payload = Map.of("Messages", List.of(message));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    MAILJET_SEND_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("MAILJET SEND FALLITO - STATUS={} - BODY={}",
                        response.getStatusCodeValue(), safe(response.getBody()));
                throw new RuntimeException("MAILJET SEND FALLITO: " + response.getBody());
            }

            log.info("EMAIL MAILJET INVIATA - TO={} - SUBJECT={} - STATUS={}",
                    safe(toEmail), safe(subject), response.getStatusCodeValue());

        } catch (Exception e) {
            log.error("ERRORE INVIO EMAIL MAILJET - TO={} - SUBJECT={}", safe(toEmail), safe(subject), e);
            throw e;
        }
    }

    private String safe(String v) {
        return v == null ? "NULL" : v.replaceAll("[\\r\\n]", "").trim();
    }
}
