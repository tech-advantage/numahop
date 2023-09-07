package fr.progilone.pgcn.domain.check;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Classe représentant un type de contrôle automatique
 *
 * @author jbrunet
 */
@Entity
@Table(name = AutomaticCheckType.TABLE_NAME)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AutomaticCheckType extends AbstractDomainObject {

    public static final String TABLE_NAME = "check_automatic_type";

    /**
     * Label
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * Type de contrôle
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AutoCheckType type;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "configurable", nullable = false)
    private boolean configurable;

    @Column(name = "order")
    private Integer order;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public AutoCheckType getType() {
        return type;
    }

    public void setType(final AutoCheckType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(final boolean configurable) {
        this.configurable = configurable;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public enum AutoCheckType {
        WITH_MASTER, /* Conditionne l'activation des controles auto */
        FILE_INTEGRITY, /* controle coherence taille des masters */
        FACILE, /* contrôle via WS FACILE du Cines */
        FILE_RADICAL, /* Contrôle du radical des fichiers */
        FILE_SEQUENCE, /* Contrôle des numéros de séquence */
        FILE_FORMAT, /* Contrôle du format des fichiers */
        FILE_TOTAL_NUMBER, /* Contrôle du nombre total de Fichiers */
        FILE_TYPE_COMPR, /* Contrôle du type de compression */
        FILE_TAUX_COMPR, /* Contrôle du taux de compression */
        FILE_RESOLUTION, /* Contrôle de la résolution */
        FILE_DEFINITION, /* Contrôle de la définition */
        FILE_COLORSPACE, /* Contrôle du profil colorimétrique */
        FILE_BIB_PREFIX, /* Contrôle nom de fichier préfixé par le préfixe de bibliothèque */
        FILE_CASE_SENSITIVE, /* Contrôle de la casse du nom de fichier */
        FILE_IMAGE_METADATA,
        METADATA_FILE, /* Controles validité des fichiers de metadonnées */
        FILE_PDF_MULTI, /* Controles presence et validite fichier pdf multicouches */
        GENER_PDF_OCR, /* Génération d'un pdf OCRisé */
        GENER_PDF_WITHOUT_OCR /* Génération d'un pdf sans OCR */
    }
}
