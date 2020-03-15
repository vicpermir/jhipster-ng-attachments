package io.github.vicpermir.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link io.github.vicpermir.domain.Attachment} entity. This class is used
 * in {@link io.github.vicpermir.web.rest.AttachmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /attachments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AttachmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter filename;

    private StringFilter originalFilename;

    private StringFilter extension;

    private IntegerFilter sizeInBytes;

    private StringFilter sha256;

    private StringFilter contentType;

    private InstantFilter uploadDate;

    private LongFilter reportsId;

    public AttachmentCriteria() {
    }

    public AttachmentCriteria(AttachmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.filename = other.filename == null ? null : other.filename.copy();
        this.originalFilename = other.originalFilename == null ? null : other.originalFilename.copy();
        this.extension = other.extension == null ? null : other.extension.copy();
        this.sizeInBytes = other.sizeInBytes == null ? null : other.sizeInBytes.copy();
        this.sha256 = other.sha256 == null ? null : other.sha256.copy();
        this.contentType = other.contentType == null ? null : other.contentType.copy();
        this.uploadDate = other.uploadDate == null ? null : other.uploadDate.copy();
        this.reportsId = other.reportsId == null ? null : other.reportsId.copy();
    }

    @Override
    public AttachmentCriteria copy() {
        return new AttachmentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFilename() {
        return filename;
    }

    public void setFilename(StringFilter filename) {
        this.filename = filename;
    }

    public StringFilter getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(StringFilter originalFilename) {
        this.originalFilename = originalFilename;
    }

    public StringFilter getExtension() {
        return extension;
    }

    public void setExtension(StringFilter extension) {
        this.extension = extension;
    }

    public IntegerFilter getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(IntegerFilter sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public StringFilter getSha256() {
        return sha256;
    }

    public void setSha256(StringFilter sha256) {
        this.sha256 = sha256;
    }

    public StringFilter getContentType() {
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public InstantFilter getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(InstantFilter uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LongFilter getReportsId() {
        return reportsId;
    }

    public void setReportsId(LongFilter reportsId) {
        this.reportsId = reportsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AttachmentCriteria that = (AttachmentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(filename, that.filename) &&
            Objects.equals(originalFilename, that.originalFilename) &&
            Objects.equals(extension, that.extension) &&
            Objects.equals(sizeInBytes, that.sizeInBytes) &&
            Objects.equals(sha256, that.sha256) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(uploadDate, that.uploadDate) &&
            Objects.equals(reportsId, that.reportsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        filename,
        originalFilename,
        extension,
        sizeInBytes,
        sha256,
        contentType,
        uploadDate,
        reportsId
        );
    }

    @Override
    public String toString() {
        return "AttachmentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (filename != null ? "filename=" + filename + ", " : "") +
                (originalFilename != null ? "originalFilename=" + originalFilename + ", " : "") +
                (extension != null ? "extension=" + extension + ", " : "") +
                (sizeInBytes != null ? "sizeInBytes=" + sizeInBytes + ", " : "") +
                (sha256 != null ? "sha256=" + sha256 + ", " : "") +
                (contentType != null ? "contentType=" + contentType + ", " : "") +
                (uploadDate != null ? "uploadDate=" + uploadDate + ", " : "") +
                (reportsId != null ? "reportsId=" + reportsId + ", " : "") +
            "}";
    }

}
