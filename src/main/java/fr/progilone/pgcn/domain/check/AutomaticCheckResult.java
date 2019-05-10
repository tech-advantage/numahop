package fr.progilone.pgcn.domain.check;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;

/**
 * Classe représentant un résultat de contrôle automatique
 *
 * @author jbrunet
 */
@Entity
@Table(name = AutomaticCheckResult.TABLE_NAME)
public class AutomaticCheckResult extends AbstractDomainObject {

    public static final String TABLE_NAME = "check_automatic_result";

    /**
     * Résultat du contrôle
     */
    @Column(name = "result", nullable = false)
    @Enumerated(EnumType.STRING)
    private AutoCheckResult result;

    /**
     * Message optionnel
     */
    @Column(name = "message", columnDefinition = "text")
    private String message;

    /**
     * Entités liés
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "check_type")
    private AutomaticCheckType check;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document")
    private DigitalDocument digitalDocument;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_document")
    private PhysicalDocument physicalDocument;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "page")
    private DocPage page;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery")
    private Delivery delivery;
    
    /* Liste fichiers en erreur */
    @Transient
    private List<String> errorFiles = new ArrayList<>();

    public AutoCheckResult getResult() {
        return result;
    }

    public void setResult(AutoCheckResult result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AutomaticCheckType getCheck() {
        return check;
    }

    public void setCheck(AutomaticCheckType check) {
        this.check = check;
    }

    public DigitalDocument getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(DigitalDocument digitalDocument) {
        this.digitalDocument = digitalDocument;
    }

    public PhysicalDocument getPhysicalDocument() {
        return physicalDocument;
    }

    public void setPhysicalDocument(PhysicalDocument physicalDocument) {
        this.physicalDocument = physicalDocument;
    }

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public DocPage getPage() {
        return page;
    }

    public void setPage(DocPage page) {
        this.page = page;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public List<String> getErrorFiles() {
        return errorFiles;
    }

    public void setErrorFiles(List<String> errorFiles) {
        this.errorFiles = errorFiles;
    }

    public void addErrorFile(String fileName) {
        if (result != null) {
            this.errorFiles.add(fileName);
        }
    }

    /**
     * Résultats possibles du contrôle
     *
     * @author jbrunet
     */
    public enum AutoCheckResult {
        OK, /* contrôle passé avec succès */
        KO, /* contrôle en échec */
        OTHER /* se référer au message pour le détail */
    }

    /**
     * Récupération des infos du parent (label) (EAGER fetched)
     */
    public String getLabel() {
        return check.getLabel();
    }

    /**
     * Récupération des infos du parent (type) (EAGER fetched)
     */
    public AutoCheckType getType() {
        return check.getType();
    }
}
