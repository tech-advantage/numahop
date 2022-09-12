package fr.progilone.pgcn.domain.dto.lot;

import java.util.Date;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDeliveryFolderDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.dto.project.ProjectDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.workflow.SimpleWorkflowModelDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.user.User;

/**
 * DTO représentant un lot
 *
 * @author jbrunet
 * @see User
 */
public class LotDTO extends AbstractVersionedDTO {

    private String identifier;
    private String label;
    private String code;
    private String type;
    private String description;
    private Boolean active;
    private String status;
    private String condNotes;
    private String numNotes;
    private String requiredFormat;
    private Date deliveryDateForseen;
    private Date realEndDate;
    private ProjectDTO project;
    private Set<SimpleDocUnitDTO> docUnits;
    private SimpleFTPConfigurationDTO activeFTPConfiguration;
    private ExportFTPConfigurationDTO activeExportFTPConfiguration;
    private SimpleCheckConfigurationDTO activeCheckConfiguration;
    private SimpleViewsFormatConfigurationDTO activeFormatConfiguration;
    private InternetArchiveCollectionDTO collectionIA;
    private CinesPACDTO planClassementPAC;
    private SimpleUserDTO provider;
    private String requiredTypeCompression;
    private Integer requiredTauxCompression;
    private String requiredResolution;
    private String requiredColorspace;
    private SimpleWorkflowModelDTO workflowModel;
    private OmekaConfigurationDTO omekaConfiguration;
    private OmekaListDTO omekaCollection;
    private OmekaListDTO omekaItem;
    private OcrLanguageDTO activeOcrLanguage;
    private ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder;

    public LotDTO(final String identifier,
                  final String label,
                  final String code,
                  final String type,
                  final String description,
                  final Boolean active,
                  final String status,
                  final String condNotes,
                  final String numNotes,
                  final String requiredFormat,
                  final Date deliveryDateForseen,
                  final Date realEndDate,
                  final ProjectDTO project,
                  final Set<SimpleDocUnitDTO> docUnits,
                  final SimpleFTPConfigurationDTO activeFTPConfiguration,
                  final ExportFTPConfigurationDTO activeExportFTPConfiguration,
                  final SimpleCheckConfigurationDTO activeCheckConfiguration,
                  final SimpleViewsFormatConfigurationDTO activeFormatConfiguration,
                  final InternetArchiveCollectionDTO collectionIA,
                  final CinesPACDTO planClassementPAC,
                  final SimpleUserDTO provider,
                  final String requiredTypeCompression,
                  final Integer requiredTauxCompression,
                  final String requiredResolution,
                  final String requiredColorspace,
                  final OmekaConfigurationDTO omekaConfiguration,
                  final OmekaListDTO omekaCollection,
                  final OmekaListDTO omekaItem,
                  final OcrLanguageDTO activeOcrLanguage,
                  final ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder) {
        this.identifier = identifier;
        this.label = label;
        this.code = code;
        this.type = type;
        this.description = description;
        this.active = active;
        this.status = status;
        this.condNotes = condNotes;
        this.numNotes = numNotes;
        this.requiredFormat = requiredFormat;
        this.deliveryDateForseen = deliveryDateForseen;
        this.realEndDate = realEndDate;
        this.project = project;
        this.docUnits = docUnits;
        this.activeFTPConfiguration = activeFTPConfiguration;
        this.activeExportFTPConfiguration = activeExportFTPConfiguration;
        this.activeCheckConfiguration = activeCheckConfiguration;
        this.activeFormatConfiguration = activeFormatConfiguration;
        this.collectionIA = collectionIA;
        this.planClassementPAC = planClassementPAC;
        this.provider = provider;
        this.requiredTypeCompression = requiredTypeCompression;
        this.requiredTauxCompression = requiredTauxCompression;
        this.requiredResolution = requiredResolution;
        this.requiredColorspace = requiredColorspace;
        this.omekaConfiguration = omekaConfiguration;
        this.omekaCollection = omekaCollection;
        this.omekaItem = omekaItem;
        this.activeOcrLanguage = activeOcrLanguage;
        this.activeExportFTPDeliveryFolder = activeExportFTPDeliveryFolder;
    }

    public LotDTO() {
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(final ProjectDTO project) {
        this.project = project;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getCondNotes() {
        return condNotes;
    }

    public void setCondNotes(final String condNotes) {
        this.condNotes = condNotes;
    }

    public String getNumNotes() {
        return numNotes;
    }

    public void setNumNotes(final String numNotes) {
        this.numNotes = numNotes;
    }

    public Date getDeliveryDateForseen() {
        return deliveryDateForseen;
    }

    public void setDeliveryDateForseen(final Date deliveryDateForseen) {
        this.deliveryDateForseen = deliveryDateForseen;
    }

    public Date getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(final Date realEndDate) {
        this.realEndDate = realEndDate;
    }

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public void setRequiredFormat(final String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }

    public Set<SimpleDocUnitDTO> getDocUnits() {
        return docUnits;
    }

    public void setDocUnits(final Set<SimpleDocUnitDTO> docUnits) {
        this.docUnits = docUnits;
    }

    public SimpleFTPConfigurationDTO getActiveFTPConfiguration() {
        return activeFTPConfiguration;
    }

    public void setActiveFTPConfiguration(final SimpleFTPConfigurationDTO activeFTPConfiguration) { this.activeFTPConfiguration = activeFTPConfiguration; }

    public ExportFTPConfigurationDTO getActiveExportFTPConfiguration() { return activeExportFTPConfiguration; }

    public void setActiveExportFTPConfiguration(ExportFTPConfigurationDTO activeExportFTPConfiguration) { this.activeExportFTPConfiguration = activeExportFTPConfiguration; }

    public SimpleCheckConfigurationDTO getActiveCheckConfiguration() {
        return activeCheckConfiguration;
    }

    public void setActiveCheckConfiguration(final SimpleCheckConfigurationDTO activeCheckConfiguration) {
        this.activeCheckConfiguration = activeCheckConfiguration;
    }

    public SimpleViewsFormatConfigurationDTO getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final SimpleViewsFormatConfigurationDTO activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
    }

    public InternetArchiveCollectionDTO getCollectionIA() {
        return collectionIA;
    }

    public void setCollectionIA(final InternetArchiveCollectionDTO collectionIA) {
        this.collectionIA = collectionIA;
    }

    public CinesPACDTO getPlanClassementPAC() {
        return planClassementPAC;
    }

    public void setPlanClassementPAC(final CinesPACDTO planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public SimpleUserDTO getProvider() {
        return provider;
    }

    public void setProvider(final SimpleUserDTO provider) {
        this.provider = provider;
    }

    public String getRequiredTypeCompression() {
        return requiredTypeCompression;
    }

    public void setRequiredTypeCompression(final String requiredTypeCompression) {
        this.requiredTypeCompression = requiredTypeCompression;
    }

    public Integer getRequiredTauxCompression() {
        return requiredTauxCompression;
    }

    public void setRequiredTauxCompression(final Integer requiredTauxCompression) {
        this.requiredTauxCompression = requiredTauxCompression;
    }

    public String getRequiredResolution() {
        return requiredResolution;
    }

    public void setRequiredResolution(final String requiredResolution) {
        this.requiredResolution = requiredResolution;
    }

    public String getRequiredColorspace() {
        return requiredColorspace;
    }

    public void setRequiredColorspace(final String requiredColorspace) {
        this.requiredColorspace = requiredColorspace;
    }

    public SimpleWorkflowModelDTO getWorkflowModel() {
        return workflowModel;
    }

    public void setWorkflowModel(final SimpleWorkflowModelDTO workflowModel) {
        this.workflowModel = workflowModel;
    }

    public OmekaConfigurationDTO getOmekaConfiguration() {
        return omekaConfiguration;
    }

    public void setOmekaConfiguration(final OmekaConfigurationDTO omekaConfiguration) {
        this.omekaConfiguration = omekaConfiguration;
    }

    public OmekaListDTO getOmekaCollection() {
        return omekaCollection;
    }

    public void setOmekaCollection(final OmekaListDTO omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaListDTO getOmekaItem() {
        return omekaItem;
    }

    public void setOmekaItem(final OmekaListDTO omekaItem) {
        this.omekaItem = omekaItem;
    }

    public OcrLanguageDTO getActiveOcrLanguage() {
        return activeOcrLanguage;
    }

    public void setActiveOcrLanguage(final OcrLanguageDTO activeOcrLanguage) {
        this.activeOcrLanguage = activeOcrLanguage;
    }

    public ExportFTPConfigurationDeliveryFolderDTO getActiveExportFTPDeliveryFolder() {
        return activeExportFTPDeliveryFolder;
    }

    public void setActiveExportFTPDeliveryFolder(ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder) {
        this.activeExportFTPDeliveryFolder = activeExportFTPDeliveryFolder;
    }

    /**
     * Builder pour la classe LotDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String label;
        private String code;
        private String type;
        private String description;
        private Boolean active;
        private String status;
        private String condNotes;
        private String numNotes;
        private String requiredFormat;
        private Date deliveryDateForseen;
        private Date realEndDate;
        private ProjectDTO project;
        private Set<SimpleDocUnitDTO> docUnits;
        private SimpleFTPConfigurationDTO activeFTPConfiguration;
        private ExportFTPConfigurationDTO activeExportFTPConfiguration;
        private SimpleCheckConfigurationDTO activeCheckConfiguration;
        private SimpleViewsFormatConfigurationDTO activeFormatConfiguration;
        private InternetArchiveCollectionDTO collectionIA;
        private CinesPACDTO planClassementPAC;
        private SimpleUserDTO provider;
        private String requiredTypeCompression;
        private Integer requiredTauxCompression;
        private String requiredResolution;
        private String requiredColorspace;
        private OmekaConfigurationDTO omekaConfiguration;
        private OmekaListDTO omekaCollection;
        private OmekaListDTO omekaItem;
        private OcrLanguageDTO activeOcrLanguage;
        private ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder;

        public Builder reinit() {
            this.identifier = null;
            this.label = null;
            this.code = null;
            this.type = null;
            this.description = null;
            this.active = null;
            this.status = null;
            this.condNotes = null;
            this.numNotes = null;
            this.requiredFormat = null;
            this.deliveryDateForseen = null;
            this.realEndDate = null;
            this.docUnits = null;
            this.activeFTPConfiguration = null;
            this.activeExportFTPConfiguration = null;
            this.activeCheckConfiguration = null;
            this.activeFormatConfiguration = null;
            this.collectionIA = null;
            this.planClassementPAC = null;
            this.provider = null;
            this.requiredTypeCompression = null;
            this.requiredTauxCompression = null;
            this.requiredResolution = null;
            this.requiredColorspace = null;
            this.omekaConfiguration = null;
            this.omekaCollection = null;
            this.omekaItem = null;
            this.activeOcrLanguage = null;
            this.activeExportFTPDeliveryFolder = null;
            return this;
        }

        public Builder setProject(final ProjectDTO project) {
            this.project = project;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setCode(final String code) {
            this.code = code;
            return this;
        }

        public Builder setType(final String type) {
            this.type = type;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setActive(final Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setStatus(final Lot.LotStatus status) {
            this.status = status.name();
            return this;
        }

        public Builder setCondNotes(final String condNotes) {
            this.condNotes = condNotes;
            return this;
        }

        public Builder setNumNotes(final String numNotes) {
            this.numNotes = numNotes;
            return this;
        }

        public Builder setDeliveryDateForseen(final Date deliveryDateForseen) {
            this.deliveryDateForseen = deliveryDateForseen;
            return this;
        }

        public Builder setRealEndDate(final Date realEndDate) {
            this.realEndDate = realEndDate;
            return this;
        }

        public Builder setRequiredFormat(final String requiredFormat) {
            this.requiredFormat = requiredFormat;
            return this;
        }

        public Builder setDocUnits(final Set<SimpleDocUnitDTO> docUnits) {
            this.docUnits = docUnits;
            return this;
        }

        public Builder setActiveFTPConfiguration(final SimpleFTPConfigurationDTO activeFTPConfiguration) {
            this.activeFTPConfiguration = activeFTPConfiguration;
            return this;
        }

        public Builder setActiveCheckConfiguration(final SimpleCheckConfigurationDTO activeCheckConfiguration) {
            this.activeCheckConfiguration = activeCheckConfiguration;
            return this;
        }

        public Builder setActiveExportFTPConfiguration(ExportFTPConfigurationDTO activeExportFTPConfiguration) {
            this.activeExportFTPConfiguration = activeExportFTPConfiguration;
            return this;
        }

        public Builder setActiveFormatConfiguration(final SimpleViewsFormatConfigurationDTO activeFormatConfiguration) {
            this.activeFormatConfiguration = activeFormatConfiguration;
            return this;
        }

        public Builder setCollectionIA(final InternetArchiveCollectionDTO collectionIA) {
            this.collectionIA = collectionIA;
            return this;
        }

        public Builder setOmekaConfiguration(final OmekaConfigurationDTO omekaConfiguration) {
            this.omekaConfiguration = omekaConfiguration;
            return this;
        }

        public Builder setOmekaCollection(final OmekaListDTO omekaCollection) {
            this.omekaCollection = omekaCollection;
            return this;
        }

        public Builder setOmekaItem(final OmekaListDTO omekaItem) {
            this.omekaItem = omekaItem;
            return this;
        }

        public Builder setPlanClassementPAC(final CinesPACDTO planClassementPAC) {
            this.planClassementPAC = planClassementPAC;
            return this;
        }

        public Builder setProvider(final SimpleUserDTO provider) {
            this.provider = provider;
            return this;
        }

        public Builder setRequiredTypeCompression(final String requiredTypeCompression) {
            this.requiredTypeCompression = requiredTypeCompression;
            return this;
        }

        public Builder setRequiredTauxCompression(final Integer requiredTauxCompression) {
            this.requiredTauxCompression = requiredTauxCompression;
            return this;
        }

        public Builder setRequiredResolution(final String requiredResolution) {
            this.requiredResolution = requiredResolution;
            return this;
        }

        public Builder setRequiredColorspace(final String requiredColorspace) {
            this.requiredColorspace = requiredColorspace;
            return this;
        }

        public Builder setActiveOcrLanguage(final OcrLanguageDTO ocrLanguage) {
            this.activeOcrLanguage = ocrLanguage;
            return this;
        }

        public Builder setDeliveryFolder(ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder) {
            this.activeExportFTPDeliveryFolder = activeExportFTPDeliveryFolder;
            return this;
        }

        public LotDTO build() {
            return new LotDTO(identifier,
                              label,
                              code,
                              type,
                              description,
                              active,
                              status,
                              condNotes,
                              numNotes,
                              requiredFormat,
                              deliveryDateForseen,
                              realEndDate,
                              project, docUnits,
                              activeFTPConfiguration,
                              activeExportFTPConfiguration,
                              activeCheckConfiguration,
                              activeFormatConfiguration,
                              collectionIA,
                              planClassementPAC, provider,
                              requiredTypeCompression,
                              requiredTauxCompression,
                              requiredResolution,
                              requiredColorspace,
                              omekaConfiguration,
                              omekaCollection,
                              omekaItem,
                              activeOcrLanguage,
                              activeExportFTPDeliveryFolder);
        }
    }
}
