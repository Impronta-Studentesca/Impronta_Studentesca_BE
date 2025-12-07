package it.impronta_studentesca_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "persona_ruoli",
            joinColumns = @JoinColumn(name = "persona_id"),
            inverseJoinColumns = @JoinColumn(name = "ruolo_id")
    )
    @Builder.Default
    private Set<Ruolo> ruoli = new HashSet<>();

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = true)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corso_di_studi_id")
    private CorsoDiStudi corsoDiStudi;

    @Column(name = "anno_corso")
    private Integer annoCorso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ufficio_id")
    private Ufficio ufficio;

    @Column(name = "data_registrazione", nullable = false)
    private LocalDateTime dataRegistrazione;

    @Column(name = "foto_url", length = 1024)
    private String fotoUrl;

    @Column(name = "foto_thumbnail_url", length = 1024)
    private String fotoThumbnailUrl;

    @Column(name = "foto_file_id", length = 255)
    private String fotoFileId;


    public void setEmail(String email) {
        // trim + lowercase sicuro
        this.email = (email == null) ? null : email.trim().toLowerCase();
    }

    @PrePersist
    protected void onCreate() {
        generaDataRegistrazione();
        normalizeEmail();
    }

    @PreUpdate
    protected void onUpdate() {
        normalizeEmail();
    }

    private void generaDataRegistrazione() {
        if (dataRegistrazione == null) {
            dataRegistrazione = LocalDateTime.now();
        }
    }

    private void normalizeEmail() {
        if (email != null) {
            email = email.trim().toLowerCase(Locale.ROOT);
        }
    }


}