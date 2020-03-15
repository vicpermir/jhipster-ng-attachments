package io.github.vicpermir.web.rest;

import io.github.vicpermir.service.AttachmentService;
import io.github.vicpermir.web.rest.errors.BadRequestAlertException;
import io.github.vicpermir.service.dto.AttachmentDTO;
import io.github.vicpermir.service.dto.AttachmentCriteria;
import io.github.vicpermir.service.AttachmentQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link io.github.vicpermir.domain.Attachment}.
 */
@RestController
@RequestMapping("/api")
public class AttachmentResource {

    private final Logger log = LoggerFactory.getLogger(AttachmentResource.class);

    private static final String ENTITY_NAME = "attachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttachmentService attachmentService;

    private final AttachmentQueryService attachmentQueryService;

    public AttachmentResource(AttachmentService attachmentService, AttachmentQueryService attachmentQueryService) {
        this.attachmentService = attachmentService;
        this.attachmentQueryService = attachmentQueryService;
    }

    /**
     * {@code POST  /attachments} : Create a new attachment.
     *
     * @param attachmentDTO the attachmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attachmentDTO, or with status {@code 400 (Bad Request)} if the attachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attachments")
    public ResponseEntity<AttachmentDTO> createAttachment(@Valid @RequestBody AttachmentDTO attachmentDTO) throws URISyntaxException {
        log.debug("REST request to save Attachment : {}", attachmentDTO);
        if (attachmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new attachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttachmentDTO result = attachmentService.save(attachmentDTO);
        return ResponseEntity.created(new URI("/api/attachments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attachments} : Updates an existing attachment.
     *
     * @param attachmentDTO the attachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attachmentDTO,
     * or with status {@code 400 (Bad Request)} if the attachmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attachments")
    public ResponseEntity<AttachmentDTO> updateAttachment(@Valid @RequestBody AttachmentDTO attachmentDTO) throws URISyntaxException {
        log.debug("REST request to update Attachment : {}", attachmentDTO);
        if (attachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AttachmentDTO result = attachmentService.save(attachmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attachmentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attachments} : get all the attachments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attachments in body.
     */
    @GetMapping("/attachments")
    public ResponseEntity<List<AttachmentDTO>> getAllAttachments(AttachmentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Attachments by criteria: {}", criteria);
        Page<AttachmentDTO> page = attachmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attachments/count} : count all the attachments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/attachments/count")
    public ResponseEntity<Long> countAttachments(AttachmentCriteria criteria) {
        log.debug("REST request to count Attachments by criteria: {}", criteria);
        return ResponseEntity.ok().body(attachmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /attachments/:id} : get the "id" attachment.
     *
     * @param id the id of the attachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attachments/{id}")
    public ResponseEntity<AttachmentDTO> getAttachment(@PathVariable Long id) {
        log.debug("REST request to get Attachment : {}", id);
        Optional<AttachmentDTO> attachmentDTO = attachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attachmentDTO);
    }

    /**
     * {@code DELETE  /attachments/:id} : delete the "id" attachment.
     *
     * @param id the id of the attachmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        log.debug("REST request to delete Attachment : {}", id);
        attachmentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * GET  /attachments/:id/download : download the "id" attachment.
     *
     * @param id the id of the attachmentDTO to download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body attachment file, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        log.debug("REST request to download Attachment : {}", id);
        Optional<AttachmentDTO> attachmentDTO = attachmentService.findOne(id);
        
        if (attachmentDTO.isPresent()) {
            AttachmentDTO a = attachmentDTO.get();
            Path path = Paths.get(attachmentService.getUploadPath(a.getUploadDate()) + File.separator + a.getFilename()).toAbsolutePath();
            log.debug("AttachmentResource: Getting file from PATH=" + path);
            File f = new File(path.toString());
            
            if (!f.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            try {
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                return ResponseEntity.ok()
                        .header("Content-Disposition", String.format("attachment; filename=\"" + a.getOriginalFilename() + "\""))
                        .contentLength(a.getSizeInBytes())
                        .contentType(MediaType.parseMediaType(a.getContentType()))
                        .body(resource);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
