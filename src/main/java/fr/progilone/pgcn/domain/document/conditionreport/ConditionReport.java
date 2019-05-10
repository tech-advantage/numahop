package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Parent;

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
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

import static fr.progilone.pgcn.service.es.EsConstant.*;

/**
 * Constat d'état: données générales
 */
@Entity
@Table(name = ConditionReport.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport", value = ConditionReport.class)})
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = ConditionReport.ES_TYPE, createIndex = false)
public class ConditionReport extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport";
    public static final String ES_TYPE = "cond_report";

    /**
     * Constats d'états effectués
     */
    @OneToMany(mappedBy = "report", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Field(type = FieldType.Object)
    private final Set<ConditionReportDetail> details = new HashSet<>();

    /**
     * Unité documentaire rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    /**
     * Le champ "Unité documentaire rattachée" est répété pour la config elasticsearch @Parent, qui doit être de type String
     */
    @Column(name = "doc_unit", insertable = false, updatable = false)
    @Parent(type = DocUnit.ES_TYPE)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String docUnitId;

    /**
     * Responsable bibliothèque: nom
     */
    @Column(name = "lib_resp_name")
    private String libRespName;

    /**
     * Responsable bibliothèque: téléphone
     */
    @Column(name = "lib_resp_phone")
    private String libRespPhone;

    /**
     * Responsable bibliothèque: email
     */
    @Column(name = "lib_resp_email")
    private String libRespEmail;

    /**
     * Chef d'équipe "traitement de conservation": nom
     */
    @Column(name = "leader_name")
    private String leaderName;

    /**
     * Chef d'équipe "traitement de conservation": téléphone
     */
    @Column(name = "leader_phone")
    private String leaderPhone;

    /**
     * Chef d'équipe "traitement de conservation": email
     */
    @Column(name = "leader_email")
    private String leaderEmail;

    /**
     * Prestataire de numérisation: nom
     */
    @Column(name = "provider_name")
    private String providerName;

    /**
     * Prestataire de numérisation: téléphone
     */
    @Column(name = "provider_phone")
    private String providerPhone;

    /**
     * Prestataire de numérisation: email
     */
    @Column(name = "provider_email")
    private String providerEmail;

    /**
     * Contact prestataire: nom
     */
    @Column(name = "prov_contact_name")
    private String providerContactName;

    /**
     * Contact prestataire: téléphone
     */
    @Column(name = "prov_contact_phone")
    private String providerContactPhone;

    /**
     * Contact prestataire: email
     */
    @Column(name = "prov_contact_email")
    private String providerContactEmail;

    /**
     * Liste des pièces jointes
     */
    @OneToMany(mappedBy = "report", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<ConditionReportAttachment> attachments = new HashSet<>();

    /**
     * Champ DocUnit: PgcnId répété pour l'indexation
     */
    @Transient
    @Field(type = FieldType.String, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)
    private final String docUnitPgcnId = null;

    /**
     * Champ DocUnit: label répété pour l'indexation
     */
    @Transient
    @Field(type = FieldType.String, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)
    private final String docUnitLabel = null;

    /**
     * Champ DocUnit: condReportType répété pour l'indexation
     */
    @Transient
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private DocUnit.CondReportType docUnitCondReportType = null;

    public Set<ConditionReportDetail> getDetails() {
        return details;
    }

    public void setDetails(final Set<ConditionReportDetail> details) {
        this.details.clear();
        if (details != null) {
            details.forEach(this::addDetail);
        }
    }

    public void addDetail(final ConditionReportDetail detail) {
        if (detail != null) {
            this.details.add(detail);
            detail.setReport(this);
        }
    }

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public String getDocUnitId() {
        return docUnitId;
    }

    public void setDocUnitId(final String docUnitId) {
        this.docUnitId = docUnitId;
    }

    public String getDocUnitPgcnId() {
        return docUnit != null ? docUnit.getPgcnId() : null;
    }

    public String getDocUnitLabel() {
        return docUnit != null ? docUnit.getLabel() : null;
    }

    public DocUnit.CondReportType getDocUnitCondReportType() {
        return docUnit != null ? docUnit.getCondReportType() : null;
    }

    public String getLibRespName() {
        return libRespName;
    }

    public void setLibRespName(final String libRespName) {
        this.libRespName = libRespName;
    }

    public String getLibRespPhone() {
        return libRespPhone;
    }

    public void setLibRespPhone(final String libRespPhone) {
        this.libRespPhone = libRespPhone;
    }

    public String getLibRespEmail() {
        return libRespEmail;
    }

    public void setLibRespEmail(final String libRespEmail) {
        this.libRespEmail = libRespEmail;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(final String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderPhone() {
        return leaderPhone;
    }

    public void setLeaderPhone(final String leaderPhone) {
        this.leaderPhone = leaderPhone;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public void setLeaderEmail(final String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(final String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(final String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public String getProviderContactName() {
        return providerContactName;
    }

    public void setProviderContactName(final String providerContactName) {
        this.providerContactName = providerContactName;
    }

    public String getProviderContactPhone() {
        return providerContactPhone;
    }

    public void setProviderContactPhone(final String providerContactPhone) {
        this.providerContactPhone = providerContactPhone;
    }

    public String getProviderContactEmail() {
        return providerContactEmail;
    }

    public void setProviderContactEmail(final String providerContactEmail) {
        this.providerContactEmail = providerContactEmail;
    }

    public Set<ConditionReportAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final Set<ConditionReportAttachment> attachments) {
        this.attachments.clear();
        if (attachments != null) {
            attachments.forEach(this::addAttachment);
        }
    }

    public void addAttachment(final ConditionReportAttachment attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
            attachment.setReport(this);
        }
    }
}
