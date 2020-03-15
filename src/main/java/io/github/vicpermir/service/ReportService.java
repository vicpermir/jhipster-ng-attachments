package io.github.vicpermir.service;

import io.github.vicpermir.domain.Report;
import io.github.vicpermir.repository.ReportRepository;
import io.github.vicpermir.service.dto.ReportDTO;
import io.github.vicpermir.service.mapper.ReportMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Report}.
 */
@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;

    private final ReportMapper reportMapper;

    private final AttachmentService attachmentService;

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper, AttachmentService attachmentService) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.attachmentService = attachmentService;
    }

    /**
     * Save a report.
     *
     * @param reportDTO the entity to save.
     * @return the persisted entity.
     */
    public ReportDTO save(ReportDTO reportDTO) {
        log.debug("Request to save Report : {}", reportDTO);
        
        // Add attachments if any
        reportDTO.setAttachments(attachmentService.processAttachments(reportDTO.getAttachments()));

        Report report = reportMapper.toEntity(reportDTO);
        report = reportRepository.save(report);
        return reportMapper.toDto(report);
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReportDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reports");
        return reportRepository.findAll(pageable)
            .map(reportMapper::toDto);
    }

    /**
     * Get all the reports with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReportDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reportRepository.findAllWithEagerRelationships(pageable).map(reportMapper::toDto);
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReportDTO> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findOneWithEagerRelationships(id)
            .map(reportMapper::toDto);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
    }
}
