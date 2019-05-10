/**
 *
 */
package fr.progilone.pgcn.domain.document;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.train.Train;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.service.es.EsConstant.*;

/**
 * @author jbrunet
 */
@Entity
@Table(name = PhysicalDocument.TABLE_NAME)
public class PhysicalDocument extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_physical_document";
    public static final String TABLE_NAME_PHYSICAL_DIGITAL_DOC = "doc_physical_document_digital";

    /*
     * FIXME
     * Lien constat d'état
     */

    /**
     * Lien vers l'unité documentaire
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = TABLE_NAME_PHYSICAL_DIGITAL_DOC,
               joinColumns = {@JoinColumn(name = "physical_document", referencedColumnName = "identifier")},
               inverseJoinColumns = {@JoinColumn(name = "digital_document", referencedColumnName = "identifier")})
    private final Set<DigitalDocument> digitalDocuments = new HashSet<>();

    /**
     * statut du document physique
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PhysicalDocumentStatus status;

    @Column(name = "name")
    private String name;

    @Column(name = "total_page")
    @Field(type = FieldType.Integer)
    private Integer totalPage;

    @Column(name = "digital_id")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String digitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train")
    private Train train;

    /**
     * Liste des résultats de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "physicalDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<AutomaticCheckResult> automaticCheckResults = new HashSet<>();

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public Set<DigitalDocument> getDigitalDocuments() {
        return digitalDocuments;
    }

    public void setDigitalDocuments(Set<DigitalDocument> digitalDocuments) {
        this.digitalDocuments.clear();
        if (digitalDocuments != null) {
            digitalDocuments.forEach(this::addDigitalDocument);
        }
    }

    public void addDigitalDocument(DigitalDocument doc) {
        if (doc != null) {
            this.digitalDocuments.add(doc);
            doc.getPhysicalDocuments().add(this);
        }
    }

    public PhysicalDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(PhysicalDocumentStatus status) {
        this.status = status;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AutomaticCheckResult> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(Set<AutomaticCheckResult> results) {
        this.automaticCheckResults.clear();
        if (results != null) {
            results.forEach(this::addAutomaticCheckResult);
        }
    }

    public void addAutomaticCheckResult(AutomaticCheckResult result) {
        if (result != null) {
            this.automaticCheckResults.add(result);
            result.setPhysicalDocument(this);
        }
    }

    /**
     * FIXME
     * Format, Degré d'ouverture
     * liste paramétrable
     */

    /**
     * Statuts d'un document physique
     */
    public enum PhysicalDocumentStatus {
        /**
         * Créé
         */
        CREATED,
        /**
         * Sélectionné
         */
        SELECTED,
        /**
         * Prélevé
         */
        GATHERED,
        /**
         * Constat d'état réalisé
         */
        STATE_CHECK_REALISED,
        /**
         * En cours de numérisation
         */
        IN_DIGITIZATION,
        /**
         * A controler (retour)
         */
        TO_CHECK,
        /**
         * A ranger
         */
        TO_SHELVE,
        /**
         * Réintégré
         */
        REINTEGRATED;
    }
}
