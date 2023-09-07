package fr.progilone.pgcn.domain.storage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DocPage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;

/**
 * Domain for the stored File
 *
 * @author Jonathan BRUNET
 */
@Entity
@Table(name = StoredFile.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "sto_stored_file", value = StoredFile.class)})
public class StoredFile extends AbstractDomainObject {

    public static final String TABLE_NAME = "sto_stored_file";

    public enum StoredFileType {
        MASTER,
        DERIVED
    }

    @Column(name = "filename")
    private String filename;

    @Column(name = "length")
    private Long length;

    @Column(name = "width")
    private Long width;

    @Column(name = "height")
    private Long height;

    @Column(name = "title_toc", columnDefinition = "TEXT")
    private String titleToc;

    @Column(name = "type_toc")
    private String typeToc;

    @Column(name = "order_toc")
    private String orderToc;

    /**
     * Page digest (main folder)
     */
    @Column(name = "page_digest")
    private String pageDigest;

    @Column(name = "mimetype")
    private String mimetype;

    @Column(name = "compression_type")
    private String compressionType;

    @Column(name = "compression_rate")
    private Integer compressionRate;

    @Column(name = "resolution")
    private Integer resolution;

    @Column(name = "colorspace")
    private String colorspace;

    @Column(name = "text_ocr", columnDefinition = "TEXT")
    private String textOcr;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private StoredFileType type;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "page")
    private DocPage page;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "format_configuration")
    private ViewsFormatConfiguration formatConfiguration;

    @Column(name = "file_format")
    @Enumerated(EnumType.STRING)
    private ViewsFormatConfiguration.FileFormat fileFormat;

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(final Long length) {
        this.length = length;
    }

    public String getPageDigest() {
        return pageDigest;
    }

    public void setPageDigest(final String pageDigest) {
        this.pageDigest = pageDigest;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(final String mimeType) {
        this.mimetype = mimeType;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(final String compressionType) {
        this.compressionType = compressionType;
    }

    public Integer getCompressionRate() {
        return compressionRate;
    }

    public void setCompressionRate(final Integer compressionRate) {
        this.compressionRate = compressionRate;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(final Integer resolution) {
        this.resolution = resolution;
    }

    public String getColorspace() {
        return colorspace;
    }

    public void setColorspace(final String colorspace) {
        this.colorspace = colorspace;
    }

    public String getTextOcr() {
        return textOcr;
    }

    public void setTextOcr(final String textOcr) {
        this.textOcr = textOcr;
    }

    public StoredFileType getType() {
        return type;
    }

    public void setType(final StoredFileType type) {
        this.type = type;
    }

    public DocPage getPage() {
        return page;
    }

    public void setPage(final DocPage page) {
        this.page = page;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(final Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(final Long height) {
        this.height = height;
    }

    public String getTitleToc() {
        return titleToc;
    }

    public void setTitleToc(final String titleToc) {
        this.titleToc = titleToc;
    }

    public String getTypeToc() {
        return typeToc;
    }

    public void setTypeToc(final String typeToc) {
        this.typeToc = typeToc;
    }

    public String getOrderToc() {
        return orderToc;
    }

    public void setOrderToc(final String orderToc) {
        this.orderToc = orderToc;
    }

    public ViewsFormatConfiguration getFormatConfiguration() {
        return formatConfiguration;
    }

    public void setFormatConfiguration(final ViewsFormatConfiguration formatConfiguration) {
        this.formatConfiguration = formatConfiguration;
    }

    public ViewsFormatConfiguration.FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(final ViewsFormatConfiguration.FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("filename", filename)
                          .add("length", length)
                          .add("width", width)
                          .add("height", height)
                          .add("title TOC", titleToc)
                          .add("mimetype", mimetype)
                          .toString();
    }

    public StoredFile getWithoutOcrText() {
        setTextOcr(null);
        return this;
    }

    /**
     * Return digest based on identifier
     *
     * @return
     */
    public String getDigest() {
        return StringUtils.lowerCase(identifier.replaceAll("[^A-Za-z0-9]+", ""));
    }

}
