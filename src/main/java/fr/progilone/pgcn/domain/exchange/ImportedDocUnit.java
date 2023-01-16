package fr.progilone.pgcn.domain.exchange;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import org.apache.commons.lang3.RegExUtils;

import javax.annotation.Nullable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Récapitulatif de l'importation d'une unité documentaire
 * <p>
 * Created by Sebastien on 08/12/2016.
 */
@Entity
@Table(name = ImportedDocUnit.TABLE_NAME)
public class ImportedDocUnit extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_doc_unit";
    public static final String TABLE_NAME_DUPL = "exc_doc_unit_dupl";
    public static final String TABLE_NAME_MESSAGE = "exc_doc_unit_msg";

    /**
     * Rapport général
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "report")
    private ImportReport report;

    /**
     * Date de début de l'import
     */
    @Column(name = "date_import")
    private LocalDateTime importDate;

    /**
     * Action à effectuer pour l'import de cette unité documentaire
     */
    @Column(name = "process", nullable = false)
    @Enumerated(EnumType.STRING)
    private Process process;

    /**
     * Clé identifiant le parent, pour les imports de type HIERARCHY_IN_MULTIPLE_IMPORT
     */
    @Column(name = "parent_key")
    private String parentKey;

    /**
     * Unités documentaires doublons de celle-ci
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_NAME_DUPL,
               joinColumns = @JoinColumn(name = "imp_unit", referencedColumnName = "identifier"),
               inverseJoinColumns = @JoinColumn(name = "doc_unit", referencedColumnName = "identifier"))
    private final Set<DocUnit> duplicatedUnits = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = TABLE_NAME_MESSAGE, joinColumns = @JoinColumn(name = "imp_unit"))
    private final Set<Message> messages = new HashSet<>();

    /**
     * Unité documentaire créée à partir de cet {@link ImportedDocUnit}
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    /**
     * Pgcn Id de l'unité documentaire importée
     */
    @Column(name = "doc_unit_pgcn_id")
    private String docUnitPgcnId;

    /**
     * Libellé de l'unité documentaire importée
     */
    @Column(name = "doc_unit_label")
    private String docUnitLabel;

    /**
     * Identifiant de l'unité documentaire parente
     */
    @Column(name = "parent_doc_unit")
    private String parentDocUnit;

    /**
     * Pgcn Id de l'unité documentaire parente
     */
    @Column(name = "parent_pgcn_id")
    private String parentDocUnitPgcnId;

    /**
     * Libellé de l'unité documentaire parente
     */
    @Column(name = "parent_label")
    private String parentDocUnitLabel;

    /**
     * Code de regroupement, par exemple pour regrouper des UD issues d'une même notice MARC
     *
     * @return
     */
    @Column(name = "group_code")
    private String groupCode;

    public ImportReport getReport() {
        return report;
    }

    public void setReport(final ImportReport report) {
        this.report = report;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(final LocalDateTime importDate) {
        this.importDate = importDate;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(final Process process) {
        this.process = process;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(final String parentKey) {
        this.parentKey = parentKey;
    }

    public Set<DocUnit> getDuplicatedUnits() {
        return duplicatedUnits;
    }

    public void setDuplicatedUnits(final Collection<DocUnit> duplicatedUnits) {
        this.duplicatedUnits.clear();
        if (duplicatedUnits != null) {
            duplicatedUnits.forEach(this::addDuplicatedUnit);
        }
    }

    public void addDuplicatedUnit(final DocUnit duplicatedUnit) {
        if (duplicatedUnit != null) {
            this.duplicatedUnits.add(duplicatedUnit);
        }
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(final Set<Message> messages) {
        this.messages.clear();
        if (messages != null) {
            messages.forEach(this::addMessages);
        }
    }

    public void addMessages(final Message message) {
        if (message != null) {
            this.messages.add(message);
        }
    }

    @Nullable
    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    /**
     * Set docUnit + champs de docUnit répétés dans {@link ImportedDocUnit}
     *
     * @param docUnit
     */
    public void initDocUnitFields(final DocUnit docUnit) {
        setDocUnit(docUnit);
        setDocUnitLabel(docUnit != null ? docUnit.getLabel() : null);
        setDocUnitPgcnId(docUnit != null ? docUnit.getPgcnId() : null);
    }

    public String getDocUnitPgcnId() {
        return docUnitPgcnId;
    }

    public void setDocUnitPgcnId(final String docUnitPgcnId) {
        this.docUnitPgcnId = docUnitPgcnId;
    }

    public String getDocUnitLabel() {
        return docUnitLabel;
    }

    public void setDocUnitLabel(final String docUnitLabel) {
        this.docUnitLabel = RegExUtils.replaceAll(docUnitLabel, "[\u0088\u0089]", "");
    }

    public String getParentDocUnit() {
        return parentDocUnit;
    }

    public void setParentDocUnit(final String parentDocUnit) {
        this.parentDocUnit = parentDocUnit;
    }

    public String getParentDocUnitPgcnId() {
        return parentDocUnitPgcnId;
    }

    public void setParentDocUnitPgcnId(final String parentDocUnitPgcnId) {
        this.parentDocUnitPgcnId = parentDocUnitPgcnId;
    }

    public String getParentDocUnitLabel() {
        return parentDocUnitLabel;
    }

    public void setParentDocUnitLabel(final String parentDocUnitLabel) {
        this.parentDocUnitLabel = parentDocUnitLabel;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(final String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * Liste des actions possible pour l'import de cette unité documentaire
     */
    public enum Process {
        ADD,
        // Création de l'unité documentaire provenant de la source d'import
        REPLACE,
        // Remplacement de l'unité documentaire existante par celle provenant de la source d'import
        IGNORE      // L'unité documentaire provenant de la source d'import n'est pas importée
    }

    /**
     * Messages détaillant les problèmes rencontrés en cours de traitement
     */
    @Embeddable
    public static class Message {

        /**
         * Code du message
         */
        @Column(name = "code", nullable = false)
        private String code;

        /**
         * Complément d'information sur ce message (détail de l'exception, ...)
         */
        @Column(name = "complement", columnDefinition = "text")
        private String complement;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getComplement() {
            return complement;
        }

        public void setComplement(String complement) {
            this.complement = complement;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("code", code).add("complement", complement).toString();
        }
    }
}
