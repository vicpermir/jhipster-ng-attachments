package io.github.vicpermir.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "report_attachments",
               joinColumns = @JoinColumn(name = "report_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "attachments_id", referencedColumnName = "id"))
    private Set<Attachment> attachments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Report name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public Report attachments(Set<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Report addAttachments(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.getReports().add(this);
        return this;
    }

    public Report removeAttachments(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.getReports().remove(this);
        return this;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }
        return id != null && id.equals(((Report) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
