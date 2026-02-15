package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import it.impronta_studentesca_be.entity.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

    @Modifying(clearAutomatically = true)
    @Query("""
        update PasswordToken t
        set t.usedAt = :now
        where t.tokenHash = :tokenHash
          and t.personaId = :personaId
          and t.purpose = :purpose
          and t.usedAt is null
          and t.expiresAt > :now
    """)
    int consumeValid(@Param("tokenHash") String tokenHash,
                     @Param("personaId") Long personaId,
                     @Param("purpose") PasswordTokenPurpose purpose,
                     @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("""
        update PasswordToken t
        set t.usedAt = :now
        where t.personaId = :personaId
          and t.purpose = :purpose
          and t.usedAt is null
    """)
    int revokeActive(@Param("personaId") Long personaId,
                     @Param("purpose") PasswordTokenPurpose purpose,
                     @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
        update PasswordToken pt
           set pt.usedAt = :now
         where pt.tokenHash = :tokenHash
           and pt.usedAt is null
    """)
    int revokeByTokenHash(@Param("tokenHash") String tokenHash,
                          @Param("now") LocalDateTime now);
}
