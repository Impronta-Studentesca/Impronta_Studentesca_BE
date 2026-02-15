package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.FileDocumentale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface FileDocumentaleRepository extends JpaRepository<FileDocumentale, Long> {

    Optional<FileDocumentale> findByDriveFileId(String driveFileId);

    List<FileDocumentale> findAllByNomeAndMimeType(String nome, String mimeType);

    Optional<FileDocumentale> findFirstByNomeAndMimeTypeOrderByUpdatedAtDescCreatedAtDesc(
            String nome, String mimeType
    );

    @Modifying
    @Query("""
        update FileDocumentale f
        set f.daModificare = true, f.updatedAt = CURRENT_TIMESTAMP
        where f.nome = :nome and f.mimeType = :mimeType
    """)
    int markDaModificareByNomeAndMimeType(@Param("nome") String nome,
                                          @Param("mimeType") String mimeType);

}
