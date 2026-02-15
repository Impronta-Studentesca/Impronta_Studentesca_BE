package it.impronta_studentesca_be.entity;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_token",
        indexes = {
                @Index(name = "idx_password_token_persona_purpose_used", columnList = "persona_id,purpose,used_at"),
                @Index(name = "idx_password_token_expires_at", columnList = "expires_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * HASH (SHA-256) DEL TOKEN IN CHIARO (MAI SALVARE IL TOKEN RAW).
     */
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    /**
     * PERSONA TARGET (NON USO RELAZIONE PER TENERE L'ENTITY LEGGERA E EVITARE FETCH).
     */
    @Column(name = "persona_id", nullable = false)
    private Long personaId;

    /**
     * SCOPO: CREA O MODIFICA PASSWORD.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    private PasswordTokenPurpose purpose;

    /**
     * SCADENZA DEL TOKEN.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * QUANDO VIENE USATO (MONOUSO). NULL = NON USATO.
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * METADATI UTILI (OPZIONALI).
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
