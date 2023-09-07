package fr.progilone.pgcn.domain.es.delivery;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryMethod;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryPayment;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Classe métier permettant de gérer les livraisons.
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-delivery")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsDelivery {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String libraryId;

    @Field(type = FieldType.Keyword)
    private String lotId;

    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    @Field(type = FieldType.Integer)
    private Integer documentCount;

    @Field(type = FieldType.Keyword)
    private DeliveryPayment payment;

    @Field(type = FieldType.Keyword)
    private DeliveryStatus status;

    @Field(type = FieldType.Keyword)
    private DeliveryMethod method;

    @Field(type = FieldType.Date)
    private LocalDate receptionDate;

    public static EsDelivery from(final Delivery delivery) {
        final EsDelivery esDelivery = new EsDelivery();
        esDelivery.setIdentifier(delivery.getIdentifier());
        if (delivery.getLot() != null) {
            esDelivery.setLotId(delivery.getLot().getIdentifier());
            if (delivery.getLot().getProject() != null && delivery.getLot().getProject().getLibrary() != null) {
                esDelivery.setLibraryId(delivery.getLot().getProject().getLibrary().getIdentifier());
            }
        }
        esDelivery.setDocumentCount(delivery.getDocuments().size());
        esDelivery.setLabel(delivery.getLabel());
        esDelivery.setMethod(delivery.getMethod());
        esDelivery.setPayment(delivery.getPayment());
        esDelivery.setReceptionDate(delivery.getReceptionDate());
        esDelivery.setStatus(delivery.getStatus());
        return esDelivery;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(final String lotId) {
        this.lotId = lotId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(final Integer documentCount) {
        this.documentCount = documentCount;
    }

    public DeliveryPayment getPayment() {
        return payment;
    }

    public void setPayment(final DeliveryPayment payment) {
        this.payment = payment;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(final DeliveryStatus status) {
        this.status = status;
    }

    public DeliveryMethod getMethod() {
        return method;
    }

    public void setMethod(final DeliveryMethod method) {
        this.method = method;
    }

    public LocalDate getReceptionDate() {
        return receptionDate;
    }

    public void setReceptionDate(final LocalDate receptionDate) {
        this.receptionDate = receptionDate;
    }

}
