package fr.progilone.pgcn.domain.es.train;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.train.Train.TrainStatus;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Classe métier permettant de gérer les trains dans Elasticsearch.
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-train")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsTrain {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String libraryId;

    @Field(type = FieldType.Keyword)
    private String projectId;

    /**
     * Libellé
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    /**
     * Etat
     */
    @Field(type = FieldType.Boolean)
    private boolean active;

    /**
     * statut
     */
    @Field(type = FieldType.Keyword)
    private TrainStatus status;

    /**
     * Date de livraison prévue
     */
    @Field(type = FieldType.Date)
    private LocalDate providerSendingDate;

    /**
     * Date de retour
     */
    @Field(type = FieldType.Date)
    private LocalDate returnDate;

    public static EsTrain from(final Train train) {
        final EsTrain esTrain = new EsTrain();
        esTrain.setIdentifier(train.getIdentifier());
        if (train.getProject() != null) {
            esTrain.setProjectId(train.getProject().getIdentifier());
            if (train.getProject().getLibrary() != null) {
                esTrain.setLibraryId(train.getProject().getLibrary().getIdentifier());
            }
        }
        esTrain.setLabel(train.getLabel());
        esTrain.setActive(train.isActive());
        esTrain.setStatus(train.getStatus());
        esTrain.setProviderSendingDate(train.getProviderSendingDate());
        esTrain.setReturnDate(train.getReturnDate());
        return esTrain;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

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

    public TrainStatus getStatus() {
        return status;
    }

    public void setStatus(final TrainStatus status) {
        this.status = status;
    }

    public LocalDate getProviderSendingDate() {
        return providerSendingDate;
    }

    public void setProviderSendingDate(final LocalDate providerSendingDate) {
        this.providerSendingDate = providerSendingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(final LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .omitNullValues()
                          .add("label", label)
                          .add("active", active)
                          .add("status", status)
                          .add("providerSendingDate", providerSendingDate)
                          .add("returnDate", returnDate)
                          .toString();
    }
}
