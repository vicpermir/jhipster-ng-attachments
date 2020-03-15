package io.github.vicpermir.web.rest;

import io.github.vicpermir.JhAttachmentsApp;
import io.github.vicpermir.domain.Attachment;
import io.github.vicpermir.domain.Report;
import io.github.vicpermir.repository.AttachmentRepository;
import io.github.vicpermir.service.AttachmentService;
import io.github.vicpermir.service.dto.AttachmentDTO;
import io.github.vicpermir.service.mapper.AttachmentMapper;
import io.github.vicpermir.service.dto.AttachmentCriteria;
import io.github.vicpermir.service.AttachmentQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AttachmentResource} REST controller.
 */
@SpringBootTest(classes = JhAttachmentsApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class AttachmentResourceIT {

    private static final String DEFAULT_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXTENSION = "AAAAAAAAAA";
    private static final String UPDATED_EXTENSION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SIZE_IN_BYTES = 1;
    private static final Integer UPDATED_SIZE_IN_BYTES = 2;
    private static final Integer SMALLER_SIZE_IN_BYTES = 1 - 1;

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPLOAD_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOAD_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AttachmentQueryService attachmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttachmentMockMvc;

    private Attachment attachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createEntity(EntityManager em) {
        Attachment attachment = new Attachment()
            .filename(DEFAULT_FILENAME)
            .originalFilename(DEFAULT_ORIGINAL_FILENAME)
            .extension(DEFAULT_EXTENSION)
            .sizeInBytes(DEFAULT_SIZE_IN_BYTES)
            .sha256(DEFAULT_SHA_256)
            .contentType(DEFAULT_CONTENT_TYPE)
            .uploadDate(DEFAULT_UPLOAD_DATE);
        return attachment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createUpdatedEntity(EntityManager em) {
        Attachment attachment = new Attachment()
            .filename(UPDATED_FILENAME)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .extension(UPDATED_EXTENSION)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .sha256(UPDATED_SHA_256)
            .contentType(UPDATED_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE);
        return attachment;
    }

    @BeforeEach
    public void initTest() {
        attachment = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttachment() throws Exception {
        int databaseSizeBeforeCreate = attachmentRepository.findAll().size();

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);
        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate + 1);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getFilename()).isEqualTo(DEFAULT_FILENAME);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(DEFAULT_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(DEFAULT_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(DEFAULT_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(DEFAULT_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testAttachment.getUploadDate()).isEqualTo(DEFAULT_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void createAttachmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attachmentRepository.findAll().size();

        // Create the Attachment with an existing ID
        attachment.setId(1L);
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setFilename(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOriginalFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setOriginalFilename(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExtensionIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setExtension(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSizeInBytesIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setSizeInBytes(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSha256IsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setSha256(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setContentType(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUploadDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().size();
        // set the field null
        attachment.setUploadDate(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        restAttachmentMockMvc.perform(post("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAttachments() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList
        restAttachmentMockMvc.perform(get("/api/attachments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME)))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME)))
            .andExpect(jsonPath("$.[*].extension").value(hasItem(DEFAULT_EXTENSION)))
            .andExpect(jsonPath("$.[*].sizeInBytes").value(hasItem(DEFAULT_SIZE_IN_BYTES)))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getAttachment() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get the attachment
        restAttachmentMockMvc.perform(get("/api/attachments/{id}", attachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attachment.getId().intValue()))
            .andExpect(jsonPath("$.filename").value(DEFAULT_FILENAME))
            .andExpect(jsonPath("$.originalFilename").value(DEFAULT_ORIGINAL_FILENAME))
            .andExpect(jsonPath("$.extension").value(DEFAULT_EXTENSION))
            .andExpect(jsonPath("$.sizeInBytes").value(DEFAULT_SIZE_IN_BYTES))
            .andExpect(jsonPath("$.sha256").value(DEFAULT_SHA_256))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.uploadDate").value(DEFAULT_UPLOAD_DATE.toString()));
    }


    @Test
    @Transactional
    public void getAttachmentsByIdFiltering() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        Long id = attachment.getId();

        defaultAttachmentShouldBeFound("id.equals=" + id);
        defaultAttachmentShouldNotBeFound("id.notEquals=" + id);

        defaultAttachmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAttachmentShouldNotBeFound("id.greaterThan=" + id);

        defaultAttachmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAttachmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAttachmentsByFilenameIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename equals to DEFAULT_FILENAME
        defaultAttachmentShouldBeFound("filename.equals=" + DEFAULT_FILENAME);

        // Get all the attachmentList where filename equals to UPDATED_FILENAME
        defaultAttachmentShouldNotBeFound("filename.equals=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByFilenameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename not equals to DEFAULT_FILENAME
        defaultAttachmentShouldNotBeFound("filename.notEquals=" + DEFAULT_FILENAME);

        // Get all the attachmentList where filename not equals to UPDATED_FILENAME
        defaultAttachmentShouldBeFound("filename.notEquals=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByFilenameIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename in DEFAULT_FILENAME or UPDATED_FILENAME
        defaultAttachmentShouldBeFound("filename.in=" + DEFAULT_FILENAME + "," + UPDATED_FILENAME);

        // Get all the attachmentList where filename equals to UPDATED_FILENAME
        defaultAttachmentShouldNotBeFound("filename.in=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByFilenameIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename is not null
        defaultAttachmentShouldBeFound("filename.specified=true");

        // Get all the attachmentList where filename is null
        defaultAttachmentShouldNotBeFound("filename.specified=false");
    }
                @Test
    @Transactional
    public void getAllAttachmentsByFilenameContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename contains DEFAULT_FILENAME
        defaultAttachmentShouldBeFound("filename.contains=" + DEFAULT_FILENAME);

        // Get all the attachmentList where filename contains UPDATED_FILENAME
        defaultAttachmentShouldNotBeFound("filename.contains=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByFilenameNotContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where filename does not contain DEFAULT_FILENAME
        defaultAttachmentShouldNotBeFound("filename.doesNotContain=" + DEFAULT_FILENAME);

        // Get all the attachmentList where filename does not contain UPDATED_FILENAME
        defaultAttachmentShouldBeFound("filename.doesNotContain=" + UPDATED_FILENAME);
    }


    @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename equals to DEFAULT_ORIGINAL_FILENAME
        defaultAttachmentShouldBeFound("originalFilename.equals=" + DEFAULT_ORIGINAL_FILENAME);

        // Get all the attachmentList where originalFilename equals to UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldNotBeFound("originalFilename.equals=" + UPDATED_ORIGINAL_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename not equals to DEFAULT_ORIGINAL_FILENAME
        defaultAttachmentShouldNotBeFound("originalFilename.notEquals=" + DEFAULT_ORIGINAL_FILENAME);

        // Get all the attachmentList where originalFilename not equals to UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldBeFound("originalFilename.notEquals=" + UPDATED_ORIGINAL_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename in DEFAULT_ORIGINAL_FILENAME or UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldBeFound("originalFilename.in=" + DEFAULT_ORIGINAL_FILENAME + "," + UPDATED_ORIGINAL_FILENAME);

        // Get all the attachmentList where originalFilename equals to UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldNotBeFound("originalFilename.in=" + UPDATED_ORIGINAL_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename is not null
        defaultAttachmentShouldBeFound("originalFilename.specified=true");

        // Get all the attachmentList where originalFilename is null
        defaultAttachmentShouldNotBeFound("originalFilename.specified=false");
    }
                @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename contains DEFAULT_ORIGINAL_FILENAME
        defaultAttachmentShouldBeFound("originalFilename.contains=" + DEFAULT_ORIGINAL_FILENAME);

        // Get all the attachmentList where originalFilename contains UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldNotBeFound("originalFilename.contains=" + UPDATED_ORIGINAL_FILENAME);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByOriginalFilenameNotContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where originalFilename does not contain DEFAULT_ORIGINAL_FILENAME
        defaultAttachmentShouldNotBeFound("originalFilename.doesNotContain=" + DEFAULT_ORIGINAL_FILENAME);

        // Get all the attachmentList where originalFilename does not contain UPDATED_ORIGINAL_FILENAME
        defaultAttachmentShouldBeFound("originalFilename.doesNotContain=" + UPDATED_ORIGINAL_FILENAME);
    }


    @Test
    @Transactional
    public void getAllAttachmentsByExtensionIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension equals to DEFAULT_EXTENSION
        defaultAttachmentShouldBeFound("extension.equals=" + DEFAULT_EXTENSION);

        // Get all the attachmentList where extension equals to UPDATED_EXTENSION
        defaultAttachmentShouldNotBeFound("extension.equals=" + UPDATED_EXTENSION);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByExtensionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension not equals to DEFAULT_EXTENSION
        defaultAttachmentShouldNotBeFound("extension.notEquals=" + DEFAULT_EXTENSION);

        // Get all the attachmentList where extension not equals to UPDATED_EXTENSION
        defaultAttachmentShouldBeFound("extension.notEquals=" + UPDATED_EXTENSION);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByExtensionIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension in DEFAULT_EXTENSION or UPDATED_EXTENSION
        defaultAttachmentShouldBeFound("extension.in=" + DEFAULT_EXTENSION + "," + UPDATED_EXTENSION);

        // Get all the attachmentList where extension equals to UPDATED_EXTENSION
        defaultAttachmentShouldNotBeFound("extension.in=" + UPDATED_EXTENSION);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByExtensionIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension is not null
        defaultAttachmentShouldBeFound("extension.specified=true");

        // Get all the attachmentList where extension is null
        defaultAttachmentShouldNotBeFound("extension.specified=false");
    }
                @Test
    @Transactional
    public void getAllAttachmentsByExtensionContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension contains DEFAULT_EXTENSION
        defaultAttachmentShouldBeFound("extension.contains=" + DEFAULT_EXTENSION);

        // Get all the attachmentList where extension contains UPDATED_EXTENSION
        defaultAttachmentShouldNotBeFound("extension.contains=" + UPDATED_EXTENSION);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByExtensionNotContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where extension does not contain DEFAULT_EXTENSION
        defaultAttachmentShouldNotBeFound("extension.doesNotContain=" + DEFAULT_EXTENSION);

        // Get all the attachmentList where extension does not contain UPDATED_EXTENSION
        defaultAttachmentShouldBeFound("extension.doesNotContain=" + UPDATED_EXTENSION);
    }


    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes equals to DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.equals=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes equals to UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.equals=" + UPDATED_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes not equals to DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.notEquals=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes not equals to UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.notEquals=" + UPDATED_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes in DEFAULT_SIZE_IN_BYTES or UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.in=" + DEFAULT_SIZE_IN_BYTES + "," + UPDATED_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes equals to UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.in=" + UPDATED_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes is not null
        defaultAttachmentShouldBeFound("sizeInBytes.specified=true");

        // Get all the attachmentList where sizeInBytes is null
        defaultAttachmentShouldNotBeFound("sizeInBytes.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes is greater than or equal to DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.greaterThanOrEqual=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes is greater than or equal to UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.greaterThanOrEqual=" + UPDATED_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes is less than or equal to DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.lessThanOrEqual=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes is less than or equal to SMALLER_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.lessThanOrEqual=" + SMALLER_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes is less than DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.lessThan=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes is less than UPDATED_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.lessThan=" + UPDATED_SIZE_IN_BYTES);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySizeInBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sizeInBytes is greater than DEFAULT_SIZE_IN_BYTES
        defaultAttachmentShouldNotBeFound("sizeInBytes.greaterThan=" + DEFAULT_SIZE_IN_BYTES);

        // Get all the attachmentList where sizeInBytes is greater than SMALLER_SIZE_IN_BYTES
        defaultAttachmentShouldBeFound("sizeInBytes.greaterThan=" + SMALLER_SIZE_IN_BYTES);
    }


    @Test
    @Transactional
    public void getAllAttachmentsBySha256IsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 equals to DEFAULT_SHA_256
        defaultAttachmentShouldBeFound("sha256.equals=" + DEFAULT_SHA_256);

        // Get all the attachmentList where sha256 equals to UPDATED_SHA_256
        defaultAttachmentShouldNotBeFound("sha256.equals=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySha256IsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 not equals to DEFAULT_SHA_256
        defaultAttachmentShouldNotBeFound("sha256.notEquals=" + DEFAULT_SHA_256);

        // Get all the attachmentList where sha256 not equals to UPDATED_SHA_256
        defaultAttachmentShouldBeFound("sha256.notEquals=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySha256IsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 in DEFAULT_SHA_256 or UPDATED_SHA_256
        defaultAttachmentShouldBeFound("sha256.in=" + DEFAULT_SHA_256 + "," + UPDATED_SHA_256);

        // Get all the attachmentList where sha256 equals to UPDATED_SHA_256
        defaultAttachmentShouldNotBeFound("sha256.in=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySha256IsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 is not null
        defaultAttachmentShouldBeFound("sha256.specified=true");

        // Get all the attachmentList where sha256 is null
        defaultAttachmentShouldNotBeFound("sha256.specified=false");
    }
                @Test
    @Transactional
    public void getAllAttachmentsBySha256ContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 contains DEFAULT_SHA_256
        defaultAttachmentShouldBeFound("sha256.contains=" + DEFAULT_SHA_256);

        // Get all the attachmentList where sha256 contains UPDATED_SHA_256
        defaultAttachmentShouldNotBeFound("sha256.contains=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    public void getAllAttachmentsBySha256NotContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where sha256 does not contain DEFAULT_SHA_256
        defaultAttachmentShouldNotBeFound("sha256.doesNotContain=" + DEFAULT_SHA_256);

        // Get all the attachmentList where sha256 does not contain UPDATED_SHA_256
        defaultAttachmentShouldBeFound("sha256.doesNotContain=" + UPDATED_SHA_256);
    }


    @Test
    @Transactional
    public void getAllAttachmentsByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType equals to DEFAULT_CONTENT_TYPE
        defaultAttachmentShouldBeFound("contentType.equals=" + DEFAULT_CONTENT_TYPE);

        // Get all the attachmentList where contentType equals to UPDATED_CONTENT_TYPE
        defaultAttachmentShouldNotBeFound("contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByContentTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType not equals to DEFAULT_CONTENT_TYPE
        defaultAttachmentShouldNotBeFound("contentType.notEquals=" + DEFAULT_CONTENT_TYPE);

        // Get all the attachmentList where contentType not equals to UPDATED_CONTENT_TYPE
        defaultAttachmentShouldBeFound("contentType.notEquals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType in DEFAULT_CONTENT_TYPE or UPDATED_CONTENT_TYPE
        defaultAttachmentShouldBeFound("contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE);

        // Get all the attachmentList where contentType equals to UPDATED_CONTENT_TYPE
        defaultAttachmentShouldNotBeFound("contentType.in=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType is not null
        defaultAttachmentShouldBeFound("contentType.specified=true");

        // Get all the attachmentList where contentType is null
        defaultAttachmentShouldNotBeFound("contentType.specified=false");
    }
                @Test
    @Transactional
    public void getAllAttachmentsByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType contains DEFAULT_CONTENT_TYPE
        defaultAttachmentShouldBeFound("contentType.contains=" + DEFAULT_CONTENT_TYPE);

        // Get all the attachmentList where contentType contains UPDATED_CONTENT_TYPE
        defaultAttachmentShouldNotBeFound("contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where contentType does not contain DEFAULT_CONTENT_TYPE
        defaultAttachmentShouldNotBeFound("contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE);

        // Get all the attachmentList where contentType does not contain UPDATED_CONTENT_TYPE
        defaultAttachmentShouldBeFound("contentType.doesNotContain=" + UPDATED_CONTENT_TYPE);
    }


    @Test
    @Transactional
    public void getAllAttachmentsByUploadDateIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where uploadDate equals to DEFAULT_UPLOAD_DATE
        defaultAttachmentShouldBeFound("uploadDate.equals=" + DEFAULT_UPLOAD_DATE);

        // Get all the attachmentList where uploadDate equals to UPDATED_UPLOAD_DATE
        defaultAttachmentShouldNotBeFound("uploadDate.equals=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByUploadDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where uploadDate not equals to DEFAULT_UPLOAD_DATE
        defaultAttachmentShouldNotBeFound("uploadDate.notEquals=" + DEFAULT_UPLOAD_DATE);

        // Get all the attachmentList where uploadDate not equals to UPDATED_UPLOAD_DATE
        defaultAttachmentShouldBeFound("uploadDate.notEquals=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByUploadDateIsInShouldWork() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where uploadDate in DEFAULT_UPLOAD_DATE or UPDATED_UPLOAD_DATE
        defaultAttachmentShouldBeFound("uploadDate.in=" + DEFAULT_UPLOAD_DATE + "," + UPDATED_UPLOAD_DATE);

        // Get all the attachmentList where uploadDate equals to UPDATED_UPLOAD_DATE
        defaultAttachmentShouldNotBeFound("uploadDate.in=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllAttachmentsByUploadDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList where uploadDate is not null
        defaultAttachmentShouldBeFound("uploadDate.specified=true");

        // Get all the attachmentList where uploadDate is null
        defaultAttachmentShouldNotBeFound("uploadDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllAttachmentsByReportsIsEqualToSomething() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);
        Report reports = ReportResourceIT.createEntity(em);
        em.persist(reports);
        em.flush();
        attachment.addReports(reports);
        attachmentRepository.saveAndFlush(attachment);
        Long reportsId = reports.getId();

        // Get all the attachmentList where reports equals to reportsId
        defaultAttachmentShouldBeFound("reportsId.equals=" + reportsId);

        // Get all the attachmentList where reports equals to reportsId + 1
        defaultAttachmentShouldNotBeFound("reportsId.equals=" + (reportsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAttachmentShouldBeFound(String filter) throws Exception {
        restAttachmentMockMvc.perform(get("/api/attachments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME)))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME)))
            .andExpect(jsonPath("$.[*].extension").value(hasItem(DEFAULT_EXTENSION)))
            .andExpect(jsonPath("$.[*].sizeInBytes").value(hasItem(DEFAULT_SIZE_IN_BYTES)))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())));

        // Check, that the count call also returns 1
        restAttachmentMockMvc.perform(get("/api/attachments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAttachmentShouldNotBeFound(String filter) throws Exception {
        restAttachmentMockMvc.perform(get("/api/attachments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAttachmentMockMvc.perform(get("/api/attachments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingAttachment() throws Exception {
        // Get the attachment
        restAttachmentMockMvc.perform(get("/api/attachments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttachment() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        int databaseSizeBeforeUpdate = attachmentRepository.findAll().size();

        // Update the attachment
        Attachment updatedAttachment = attachmentRepository.findById(attachment.getId()).get();
        // Disconnect from session so that the updates on updatedAttachment are not directly saved in db
        em.detach(updatedAttachment);
        updatedAttachment
            .filename(UPDATED_FILENAME)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .extension(UPDATED_EXTENSION)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .sha256(UPDATED_SHA_256)
            .contentType(UPDATED_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE);
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(updatedAttachment);

        restAttachmentMockMvc.perform(put("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isOk());

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(UPDATED_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(UPDATED_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(UPDATED_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testAttachment.getUploadDate()).isEqualTo(UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().size();

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttachmentMockMvc.perform(put("/api/attachments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(attachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAttachment() throws Exception {
        // Initialize the database
        attachmentRepository.saveAndFlush(attachment);

        int databaseSizeBeforeDelete = attachmentRepository.findAll().size();

        // Delete the attachment
        restAttachmentMockMvc.perform(delete("/api/attachments/{id}", attachment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Attachment> attachmentList = attachmentRepository.findAll();
        assertThat(attachmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
