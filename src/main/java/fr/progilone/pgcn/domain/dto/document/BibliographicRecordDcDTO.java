package fr.progilone.pgcn.domain.dto.document;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO reprenant les données Dublin Core d'une notice bibliographique,
 * utilisé en particulier pour l'export CINES
 * <p>
 * Created by Sébastien on 10/07/2017.
 */
public class BibliographicRecordDcDTO {

    private final List<String> title = new ArrayList<>();
    private final List<String> creator = new ArrayList<>();
    private final List<String> subject = new ArrayList<>();
    private final List<String> description = new ArrayList<>();
    private final List<String> publisher = new ArrayList<>();
    private final List<String> contributor = new ArrayList<>();
    private final List<String> date = new ArrayList<>();
    private final List<String> type = new ArrayList<>();
    private final List<String> format = new ArrayList<>();
    private final List<String> identifier = new ArrayList<>();
    private final List<String> source = new ArrayList<>();
    private final List<String> language = new ArrayList<>();
    private final List<String> relation = new ArrayList<>();
    private final List<String> coverage = new ArrayList<>();
    private final List<String> rights = new ArrayList<>();

    // custom properties
    private List<DocPropertyDTO> customProperties;

    public List<String> getTitle() {
        return title;
    }

    public List<String> getCreator() {
        return creator;
    }

    public List<String> getSubject() {
        return subject;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getPublisher() {
        return publisher;
    }

    public List<String> getContributor() {
        return contributor;
    }

    public List<String> getDate() {
        return date;
    }

    public List<String> getType() {
        return type;
    }

    public List<String> getFormat() {
        return format;
    }

    public List<String> getIdentifier() {
        return identifier;
    }

    public List<String> getSource() {
        return source;
    }

    public List<String> getLanguage() {
        return language;
    }

    public List<String> getRelation() {
        return relation;
    }

    public List<String> getCoverage() {
        return coverage;
    }

    public List<String> getRights() {
        return rights;
    }

    public List<DocPropertyDTO> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(final List<DocPropertyDTO> customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("title", title)
                          .add("creator", creator)
                          .add("subject", subject)
                          .add("description", description)
                          .add("publisher", publisher)
                          .add("contributor", contributor)
                          .add("date", date)
                          .add("type", type)
                          .add("format", format)
                          .add("identifier", identifier)
                          .add("source", source)
                          .add("language", language)
                          .add("relation", relation)
                          .add("coverage", coverage)
                          .add("rights", rights)
                          .toString();
    }
}
