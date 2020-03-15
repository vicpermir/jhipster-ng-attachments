package io.github.vicpermir.service;

import io.github.vicpermir.config.ApplicationProperties;
import io.github.vicpermir.domain.Attachment;
import io.github.vicpermir.repository.AttachmentRepository;
import io.github.vicpermir.service.dto.AttachmentDTO;
import io.github.vicpermir.service.mapper.AttachmentMapper;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service Implementation for managing {@link Attachment}.
 */
@Service
@Transactional
public class AttachmentService {

    private final Logger log = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;

    private final ApplicationProperties applicationProperties;

    public AttachmentService(AttachmentRepository attachmentRepository, AttachmentMapper attachmentMapper, ApplicationProperties applicationProperties) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Save a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AttachmentDTO save(AttachmentDTO attachmentDTO) {
        log.debug("Request to save Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }

    /**
     * Get all the attachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AttachmentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Attachments");
        return attachmentRepository.findAll(pageable)
            .map(attachmentMapper::toDto);
    }

    /**
     * Get one attachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        log.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findById(id)
            .map(attachmentMapper::toDto);
    }

    /**
     * Delete the attachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
    }

    /**
     * Get one attachment by sha256.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findBySha256(String sha256) {
        log.debug("Request to get Attachment by Sha256 : {}", sha256);
        return attachmentRepository.findBySha256(sha256)
            .map(attachmentMapper::toDto);
    }

    /**
     * Creates a new file
     * @param fileName
     * @param base64String
     * @return 
     */
    public Boolean createBase64File(String fileName, byte[] bytes) {
        String uploadPath = this.getUploadPath();
        Path path = Paths.get(uploadPath, fileName).toAbsolutePath();

        try {
            Files.createDirectories(Paths.get(uploadPath));
            Files.write(path, bytes);
        } catch ( IOException e ) {
            e.printStackTrace(System.out);
        }

        File file = new File(path.toString());

        return file.isFile();
    }
    
    /**
     * Returns the currently configured upload path
     * @return 
     */
    public String getUploadPath() {
        return this.getUploadPath(Instant.now());
    }

    /**
     * Returns the currently configured upload path for an upload date
     * Full upload paths in the form of /[year]/[month]/[file]
     * @param uploadDate
     * @return full path
     */
    public String getUploadPath(Instant uploadDate) {
        String baseUploadDir = applicationProperties.getBaseDir() + applicationProperties.getUploadDir();
        LocalDateTime now = LocalDateTime.ofInstant(uploadDate, ZoneId.systemDefault());
        int year  = now.getYear();
        int month = now.getMonth().getValue();
        return baseUploadDir + File.separator + year + File.separator + month;
    }

    /**
     * Process file attachments
     */
    public Set<AttachmentDTO> processAttachments(Set<AttachmentDTO> attachments) {
        Set<AttachmentDTO> result = new HashSet<>();
        if (attachments != null && attachments.size() > 0) {
            for (AttachmentDTO a : attachments) {
                if (a.getId() == null) {
                    Optional<AttachmentDTO> attachmentPrevio = this.findBySha256(a.getSha256());
                    if(attachmentPrevio.isPresent()) {
                        a.setId(attachmentPrevio.get().getId());
                    } else {
                        String fileExtension = FilenameUtils.getExtension(a.getOriginalFilename());
                        String fileName = UUID.randomUUID() + "." + fileExtension;
                        if (StringUtils.isBlank(a.getContentType())) {
                            a.setContentType("application/octet-stream");
                        }
                        Boolean saved = this.createBase64File(fileName, a.getFile());
                        if (saved) {
                            a.setFilename(fileName);
                        }
                    }
                }
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Delete a collection of attachments (files included)
     */
    public void deleteAttachments(Collection<AttachmentDTO> attachments) {
        if (attachments != null && attachments.size() > 0) {
            attachments.forEach(this::deleteAttachment);
        }
    }

    /**
     * Safely delete an attachment file from the server
     */
    public void deleteAttachment(AttachmentDTO attachment) {
        log.debug("AttachmentService: Deleting attachment: {}", attachment);
        Path path = Paths.get(getUploadPath() + File.separator + attachment.getFilename()).toAbsolutePath();
        try {
            if (Files.deleteIfExists(path)) {
                log.debug("AttachmentService: Successfully deleted file on PATH=" + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
