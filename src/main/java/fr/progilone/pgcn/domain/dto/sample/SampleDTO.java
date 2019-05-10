package fr.progilone.pgcn.domain.dto.sample;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.progilone.pgcn.domain.dto.AbstractDTO;
import fr.progilone.pgcn.domain.dto.delivery.DeliveryDTO;
import fr.progilone.pgcn.domain.dto.document.DocPageDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDigitalDocumentDTO;

public class SampleDTO extends AbstractDTO {

    private String identifier;
    private String samplingMode;
    private SortedSet<DocPageDTO> pages = new TreeSet<>();
    private Set<SimpleDigitalDocumentDTO> documents = new HashSet<>();

    private DeliveryDTO delivery;


    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    /**
     * @return the samplingMode
     */
    public String getSamplingMode() {
        return samplingMode;
    }
    /**
     * @param samplingMode the samplingMode to set
     */
    public void setSamplingMode(String samplingMode) {
        this.samplingMode = samplingMode;
    }
    /**
     * @return the pages
     */
    public SortedSet<DocPageDTO> getPages() {
        return pages;
    }
    /**
     * @param pages the pages to set
     */
    public void setPages(SortedSet<DocPageDTO> pages) {
        this.pages = pages;
    }

    public Set<SimpleDigitalDocumentDTO> getDocuments() {
        pages.forEach(p-> this.documents.add(p.getDigitalDocument()));
        return documents;
    }
    /**
     * @return the delivery
     */
    public DeliveryDTO getDelivery() {
        return delivery;
    }
    /**
     * @param delivery the delivery to set
     */
    public void setDelivery(DeliveryDTO delivery) {
        this.delivery = delivery;
    }

}
