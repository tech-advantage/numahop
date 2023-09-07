package fr.progilone.pgcn.domain.ocrlangconfiguration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Classe permettant de gérer les fichiers de langage disponibles pour OCR.
 */
@Entity
@Table(name = OcrLangConfiguration.TABLE_NAME)
// @JsonSubTypes({@JsonSubTypes.Type(name = "ocrLangConfiguration", value = OcrLangConfiguration.class)})
public class OcrLangConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_ocr_lang";

    @Column(name = "label")
    private String label;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * Liste des confs de language ocr actifs.
     */
    @OneToMany(mappedBy = "ocrLangConfiguration", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<ActivatedOcrLanguage> activatedOcrLanguages = new LinkedHashSet<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Set<ActivatedOcrLanguage> getActivatedOcrLanguages() {
        return activatedOcrLanguages;
    }

    public void setActivatedOcrLanguages(final Set<ActivatedOcrLanguage> languages) {
        this.activatedOcrLanguages.clear();
        if (languages != null) {
            languages.forEach(this::addLanguage);
        }
    }

    public void addLanguage(final ActivatedOcrLanguage language) {
        if (language != null) {
            this.activatedOcrLanguages.add(language);
            language.setOcrLangConfiguration(this);
        }
    }

}
