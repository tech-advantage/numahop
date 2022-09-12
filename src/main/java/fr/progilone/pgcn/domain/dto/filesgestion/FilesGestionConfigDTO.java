package fr.progilone.pgcn.domain.dto.filesgestion;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDeliveryFolderDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

public class FilesGestionConfigDTO extends AbstractVersionedDTO {

    private String identifier;
    private String triggerType;
    private int delay;
    private boolean useExportFtp;
    private String destinationDir;
    private boolean deleteMaster;
    private boolean deletePdf;
    private boolean deletePrint;
    private boolean deleteView;
    private boolean deleteThumb;
    private boolean saveMaster;
    private boolean savePdf;
    private boolean savePrint;
    private boolean saveView;
    private boolean saveThumb;
    private boolean saveAipSip;
    private ExportFTPConfigurationDTO activeExportFTPConfiguration;
    private ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder;

    private SimpleLibraryDTO library;


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }


    public FilesGestionConfigDTO() {
        super();
    }


    public String getTriggerType() {
        return triggerType;
    }


    public void setTriggerType(final String triggerType) {
        this.triggerType = triggerType;
    }


    public int getDelay() {
        return delay;
    }


    public void setDelay(final int delay) {
        this.delay = delay;
    }


    public boolean isUseExportFtp() {
        return useExportFtp;
    }


    public void setUseExportFtp(final boolean useExportFtp) {
        this.useExportFtp = useExportFtp;
    }


    public String getDestinationDir() {
        return destinationDir;
    }


    public void setDestinationDir(final String destinationDir) {
        this.destinationDir = destinationDir;
    }


    public boolean isDeleteMaster() {
        return deleteMaster;
    }


    public void setDeleteMaster(final boolean deleteMaster) {
        this.deleteMaster = deleteMaster;
    }


    public boolean isDeletePrint() {
        return deletePrint;
    }


    public void setDeletePrint(final boolean deletePrint) {
        this.deletePrint = deletePrint;
    }


    public boolean isDeleteView() {
        return deleteView;
    }


    public void setDeleteView(final boolean deleteView) {
        this.deleteView = deleteView;
    }


    public boolean isDeleteThumb() {
        return deleteThumb;
    }


    public void setDeleteThumb(final boolean deleteThumb) {
        this.deleteThumb = deleteThumb;
    }


    public boolean isSaveMaster() {
        return saveMaster;
    }


    public void setSaveMaster(final boolean saveMaster) {
        this.saveMaster = saveMaster;
    }


    public boolean isSavePrint() {
        return savePrint;
    }


    public void setSavePrint(final boolean savePrint) {
        this.savePrint = savePrint;
    }


    public boolean isSaveView() {
        return saveView;
    }


    public void setSaveView(final boolean saveView) {
        this.saveView = saveView;
    }


    public boolean isSaveThumb() {
        return saveThumb;
    }


    public void setSaveThumb(final boolean saveThumb) {
        this.saveThumb = saveThumb;
    }


    public boolean isDeletePdf() {
        return deletePdf;
    }


    public void setDeletePdf(final boolean deletePdf) {
        this.deletePdf = deletePdf;
    }


    public boolean isSavePdf() {
        return savePdf;
    }


    public void setSavePdf(final boolean savePdf) {
        this.savePdf = savePdf;
    }


    public boolean isSaveAipSip() {
        return saveAipSip;
    }


    public void setSaveAipSip(final boolean saveAipSip) {
        this.saveAipSip = saveAipSip;
    }


    public SimpleLibraryDTO getLibrary() {
        return library;
    }


    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public ExportFTPConfigurationDTO getActiveExportFTPConfiguration() {
        return activeExportFTPConfiguration;
    }

    public void setActiveExportFTPConfiguration(ExportFTPConfigurationDTO activeExportFTPConfiguration) {
        this.activeExportFTPConfiguration = activeExportFTPConfiguration;
    }

    public ExportFTPConfigurationDeliveryFolderDTO getActiveExportFTPDeliveryFolder() {
        return activeExportFTPDeliveryFolder;
    }

    public void setActiveExportFTPDeliveryFolder(ExportFTPConfigurationDeliveryFolderDTO activeExportFTPDeliveryFolder) {
        this.activeExportFTPDeliveryFolder = activeExportFTPDeliveryFolder;
    }
}
