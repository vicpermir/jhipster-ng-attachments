package io.github.vicpermir.repository;

import io.github.vicpermir.domain.Attachment;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Attachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {

    @Query("SELECT a FROM Attachment a WHERE sha256 = :sha256")
    Optional<Attachment> findBySha256(@Param("sha256") String sha256);

}
