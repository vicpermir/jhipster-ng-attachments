package io.github.vicpermir.repository;

import io.github.vicpermir.domain.Report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Report entity.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query(value = "select distinct report from Report report left join fetch report.attachments",
        countQuery = "select count(distinct report) from Report report")
    Page<Report> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct report from Report report left join fetch report.attachments")
    List<Report> findAllWithEagerRelationships();

    @Query("select report from Report report left join fetch report.attachments where report.id =:id")
    Optional<Report> findOneWithEagerRelationships(@Param("id") Long id);
}
