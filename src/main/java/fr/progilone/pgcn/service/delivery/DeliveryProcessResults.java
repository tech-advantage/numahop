package fr.progilone.pgcn.service.delivery;

import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.dto.check.SplitFilename;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Permet de faire transiter des données pour la livraison entre service synchrone/asynchrone
 *
 * @author jbrunet
 *         Créé le 19 mai 2017
 */
public class DeliveryProcessResults {

    private Map<String, PrefixedDocumentsDTO> documentsDTOForPrefix = new HashMap<>();
    private Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

    private Map<String, Set<PreDeliveryDocumentFileDTO>> metadatasDTOForPrefix = new HashMap<>();
    private Map<AutoCheckType, AutomaticCheckRule> checkingRules = new HashMap<>();

    private String samplingMode;
    private Double samplingRate;

    private String libraryId;

    public DeliveryProcessResults() {
    }

    public Map<String, PrefixedDocumentsDTO> getDocumentsDTOForPrefix() {
        return documentsDTOForPrefix;
    }

    public void setDocumentsDTOForPrefix(final Map<String, PrefixedDocumentsDTO> documentsDTOForPrefix) {
        this.documentsDTOForPrefix = documentsDTOForPrefix;
    }

    public Map<String, Optional<SplitFilename>> getSplitNames() {
        return splitNames;
    }

    public void setSplitNames(final Map<String, Optional<SplitFilename>> splitNames) {
        this.splitNames = splitNames;
    }

    public Map<String, Set<PreDeliveryDocumentFileDTO>> getMetadatasDTOForPrefix() {
        return metadatasDTOForPrefix;
    }

    public void setMetadatasDTOForPrefix(final Map<String, Set<PreDeliveryDocumentFileDTO>> metadatasDTOForPrefix) {
        this.metadatasDTOForPrefix = metadatasDTOForPrefix;
    }

    public Map<AutoCheckType, AutomaticCheckRule> getCheckingRules() {
        return checkingRules;
    }

    public void setCheckingRules(final Map<AutoCheckType, AutomaticCheckRule> checkingRules) {
        this.checkingRules = checkingRules;
    }

    /**
     * @return the samplingMode
     */
    public String getSamplingMode() {
        return samplingMode;
    }

    /**
     * @param samplingMode
     *            the samplingMode to set
     */
    public void setSamplingMode(final String samplingMode) {
        this.samplingMode = samplingMode;
    }

    /**
     * @return the samplingRate
     */
    public Double getSamplingRate() {
        return samplingRate;
    }

    /**
     * @param samplingRate
     *            the samplingRate to set
     */
    public void setSamplingRate(final Double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(final String libraryId) {
        this.libraryId = libraryId;
    }

}
