package fr.progilone.pgcn.service.exchange.internetarchive;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Données associées à un item pour l'envoi sur Internet Archive
 *
 * @author jbrunet
 * Créé le 21 avr. 2017
 */
public class InternetArchiveItemDTO extends AbstractDTO {

    private String identifier;
    private String archiveIdentifier;
    private List<String> collections = new ArrayList<>();
    private String contributor;
    private String coverage;
    private String creator;
    private String credits;
    private String date; // ISO 8601 compliant required
    private String description;
    @JsonIgnore
    private TreeMap<Integer, String> descriptions = new TreeMap<>();
    private String language;
    private String licenseUrl;
    private MediaType mediatype;
    private String customMediatype;
    private String notes;
    private String publisher;
    private String rights;
    private List<String> subjects = new ArrayList<>();
    private String title;
    private List<CustomHeader> customHeaders = new ArrayList<>();
    private int total;
    private String type;
    private String source;

    public InternetArchiveItemDTO() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(final String contributor) {
        this.contributor = contributor;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(final String coverage) {
        this.coverage = coverage;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(final String credits) {
        this.credits = credits;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(final String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public MediaType getMediatype() {
        return mediatype;
    }

    public void setMediatype(final MediaType mediatype) {
        this.mediatype = mediatype;
    }

    public String getCustomMediatype() {
        return customMediatype;
    }

    public void setCustomMediatype(final String customMediatype) {
        this.customMediatype = customMediatype;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(final String rights) {
        this.rights = rights;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public TreeMap<Integer, String> getDescriptions() {
        return descriptions;
    }

    public void setSubjects(final List<String> subjects) {
        this.subjects.clear();
        if(subjects != null) {
            subjects.forEach(this::addSubject);
        }
    }

    public void addSubject(final String subject) {
        if(subject != null) {
            this.subjects.add(subject);
        }
    }

    public List<String> getCollections() {
        return collections;
    }

    public void setCollections(final List<String> collections) {
        this.collections.clear();
        if(collections != null) {
            collections.forEach(this::addCollection);
        }
    }

    public void addCollection(final String collection) {
        if(collection != null) {
            this.collections.add(collection);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public List<CustomHeader> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(final List<CustomHeader> customHeaders) {
        this.customHeaders.clear();
        if(customHeaders != null) {
            customHeaders.forEach(this::addCustomHeader);
        }
    }

    public void addCustomHeader(final CustomHeader header) {
        if(header != null && StringUtils.isNotBlank(header.getType()) && StringUtils.isNotBlank(header.getValue())) {
            this.customHeaders.add(header);
        }
    }

    public void addDescription(final String value, final Integer rank) {
        this.descriptions.put(rank, value);
    }

    public String getArchiveIdentifier() {
        return archiveIdentifier;
    }

    public void setArchiveIdentifier(final String archiveIdentifier) {
        this.archiveIdentifier = archiveIdentifier;
    }

    public enum MediaType {
        texts,
        image,
        collection,
        autre
    }

    public static class CustomHeader extends AbstractDTO {
        private String type;
        private String value;

        public CustomHeader() {}

        public String getType() {
            return type;
        }
        public void setType(final String type) {
            this.type = type;
        }
        public String getValue() {
            return value;
        }
        public void setValue(final String value) {
            this.value = value;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

}
