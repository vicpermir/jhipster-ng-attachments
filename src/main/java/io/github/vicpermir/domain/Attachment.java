package io.github.vicpermir.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Attachment.
 */
@Entity
@Table(name = "attachment")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @NotNull
    @Column(name = "extension", nullable = false)
    private String extension;

    @NotNull
    @Column(name = "size_in_bytes", nullable = false)
    private Integer sizeInBytes;

    @NotNull
    @Column(name = "sha_256", nullable = false)
    private String sha256;

    @NotNull
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @CreatedDate
    @NotNull
    @Column(name = "upload_date", nullable = false, updatable = false)
    private Instant uploadDate = Instant.now();

    @ManyToMany(mappedBy = "attachments")
    @JsonIgnore
    private Set<Report> reports = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public Attachment filename(String filename) {
        this.filename = filename;
        return this;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public Attachment originalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getExtension() {
        return extension;
    }

    public Attachment extension(String extension) {
        this.extension = extension;
        return this;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Integer getSizeInBytes() {
        return sizeInBytes;
    }

    public Attachment sizeInBytes(Integer sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        return this;
    }

    public void setSizeInBytes(Integer sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public Attachment sha256(String sha256) {
        this.sha256 = sha256;
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getContentType() {
        return contentType;
    }

    public Attachment contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public Attachment uploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public Attachment reports(Set<Report> reports) {
        this.reports = reports;
        return this;
    }

    public Attachment addReports(Report report) {
        this.reports.add(report);
        report.getAttachments().add(this);
        return this;
    }

    public Attachment removeReports(Report report) {
        this.reports.remove(report);
        report.getAttachments().remove(this);
        return this;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return id != null && id.equals(((Attachment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Attachment{" +
            "id=" + getId() +
            ", filename='" + getFilename() + "'" +
            ", originalFilename='" + getOriginalFilename() + "'" +
            ", extension='" + getExtension() + "'" +
            ", sizeInBytes=" + getSizeInBytes() +
            ", sha256='" + getSha256() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", uploadDate='" + getUploadDate() + "'" +
            "}";
    }
}
