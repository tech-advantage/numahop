package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = ConditionReportAttachment.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport_attachment", value = ConditionReportAttachment.class)})
public class ConditionReportAttachment extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_attachment";

    /**
     * Constat d'état
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "report")
    private ConditionReport report;

    /**
     * Nom du fichier importé
     */
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    /**
     * Taille du fichier importé
     */
    @Column(name = "file_size")
    private Long fileSize;

    public ConditionReport getReport() {
        return report;
    }

    public void setReport(final ConditionReport report) {
        this.report = report;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(final String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("originalFilename", originalFilename).add("fileSize", fileSize).toString();
    }
}
