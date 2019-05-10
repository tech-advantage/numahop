package fr.progilone.pgcn.domain.exchange.template;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Template velocity
 */
@Entity
@Table(name = Template.TABLE_NAME)
public class Template extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_template";

    /**
     * Nom du template
     */
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Name name;

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

    /**
     * Bibliothèque
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("identifier", identifier)
                          .add("name", name)
                          .add("originalFilename", originalFilename)
                          .add("fileSize", fileSize)
                          .toString();
    }
}
