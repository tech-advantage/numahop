package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;

public class ConditionReportAttachmentDTO {

    private String identifier;
    private String originalFilename;
    private Long fileSize;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
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
        return MoreObjects.toStringHelper(this)
                          .add("identifier", identifier)
                          .add("originalFilename", originalFilename)
                          .add("fileSize", fileSize)
                          .toString();
    }
}
