package fr.progilone.pgcn.domain.document;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;

/**
 * Classe représentant une page d'un document numérique
 *
 * @author Jonathan
 */
@Entity
@Table(name = DocPage.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_page", value = DocPage.class)})
public class DocPage extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_page";

    /**
     * Document Numérique correspondant
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document")
    private DigitalDocument digitalDocument;

    /**
     * Liste des fichiers rattachés (master & dérivés)
     */
    @OneToMany(mappedBy = "page", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<StoredFile> files = new HashSet<>();

    /**
     * Liste des contrôles (utilisateur)
     */
    @OneToMany(mappedBy = "page", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Check> checks = new HashSet<>();
    
    /**
     * Liste des résultats de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "page", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<AutomaticCheckResult> automaticCheckResults = new HashSet<>();
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "sample")
    private Sample sample;

    /**
     * Numéro de séquence
     */
    private Integer number;

    /**
     * Description
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * Notes de contrôle
     */
    @Column(name = "check_notes", columnDefinition = "text")
    private String checkNotes;

    /**
     * status
     */
    @Column
    @Enumerated(EnumType.STRING)
    private PageStatus status;

    public DigitalDocument getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(final DigitalDocument digitalDocument) {
        this.digitalDocument = digitalDocument;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(final Sample sample) {
        this.sample = sample;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(final String checkNotes) {
        this.checkNotes = checkNotes;
    }

    public PageStatus getStatus() {
        return status;
    }

    public void setStatus(final PageStatus status) {
        this.status = status;
    }

    public Set<StoredFile> getFiles() {
        return files;
    }

    public void setFiles(final Set<StoredFile> files) {
        this.files.clear();
        if (files != null) {
            files.forEach(this::addFile);
        }
    }

    public void addFile(final StoredFile file) {
        if (file != null) {
            this.files.add(file);
            file.setPage(this);
        }
    }

    public void setChecks(final Set<Check> checks) {
        this.checks = checks;
    }

    public Set<Check> getChecks() {
        return checks;
    }
    
    public Set<AutomaticCheckResult> getAutomaticCheckResults() {
        return automaticCheckResults;
    }
    
    public void setAutomaticCheckResults(final Set<AutomaticCheckResult> results) {
        this.automaticCheckResults.clear();
        if (results != null) {
            results.forEach(this::addAutomaticCheckResult);
        }
    }

    public void addAutomaticCheckResult(final AutomaticCheckResult result) {
        if (result != null) {
            this.automaticCheckResults.add(result);
            result.setPage(this);
        }
    }

    /**
     * Status possibles pour une page
     */
    public enum PageStatus {
        /**
         * Contrôles à faire
         */
        TO_CHECK,
        /**
         * Validé
         */
        VALIDATED,
        /**
         * Rejeté
         */
        REJECTED,
        /**
         * En attente de reception
         */
        WAITING
    }

    /**
     * Récupère le master d'une page
     * les StoredFile doivent être initialisés
     *
     * @return
     */
    public Optional<StoredFile> getMaster() {
        return this.files.stream().filter(file -> StoredFileType.MASTER.equals(file.getType())).findFirst();
    }

    /**
     * Récupère le dérivé au format spécifié (si existant)
     * 
     * @param format
     * @return
     */
    public Optional<StoredFile> getDerivedForFormat(final ViewsFormatConfiguration.FileFormat format) {
        if(format == null || format.identifier() == null) {
            return Optional.empty();
        }
        return this.files.stream()
                .filter(file -> StoredFileType.DERIVED.equals(file.getType()) 
                        && StringUtils.equalsIgnoreCase(format.identifier(), file.getFileFormat().identifier()))
                .findFirst();
    }
}
