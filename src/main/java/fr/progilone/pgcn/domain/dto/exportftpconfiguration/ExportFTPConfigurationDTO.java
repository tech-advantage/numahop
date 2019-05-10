package fr.progilone.pgcn.domain.dto.exportftpconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

public class ExportFTPConfigurationDTO extends AbstractVersionedDTO {
    
    private String identifier;
    private String label;
    private SimpleLibraryDTO library;
    private String address;
    private String login;
    private String password;
    private String storageServer;
    private String port;
    private boolean active;
    
    private boolean exportMets;
    private boolean exportMaster;
    private boolean exportView;
    private boolean exportThumb;
    private boolean exportPdf;
    private boolean exportAipSip;

    public ExportFTPConfigurationDTO(final String identifier,
                               final String label,
                               final SimpleLibraryDTO library,
                               final String address,
                               final String login,
                               final String password,
                               final String storageServer,
                               final String port,
                               final boolean active,
                               final boolean exportAipSip, final boolean exportMets,
                               final boolean exportMaster, final boolean exportView,
                               final boolean exportThumb, final boolean exportPdf) {
        this.identifier = identifier;
        this.label = label;
        this.library = library;
        this.address = address;
        this.login = login;
        this.password = password;
        this.storageServer = storageServer;
        this.port = port;
        this.active = active;
        this.exportAipSip = exportAipSip;
        this.exportMets = exportMets;
        this.exportMaster = exportMaster;
        this.exportView = exportView;
        this.exportThumb = exportThumb;
        this.exportPdf = exportPdf;
    }

    public ExportFTPConfigurationDTO() {

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

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(final String storageServer) {
        this.storageServer = storageServer;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public boolean isExportMets() {
        return exportMets;
    }

    public void setExportMets(final boolean exportMets) {
        this.exportMets = exportMets;
    }

    public boolean isExportMaster() {
        return exportMaster;
    }

    public void setExportMaster(final boolean exportMaster) {
        this.exportMaster = exportMaster;
    }

    public boolean isExportView() {
        return exportView;
    }

    public void setExportView(final boolean exportView) {
        this.exportView = exportView;
    }

    public boolean isExportThumb() {
        return exportThumb;
    }

    public void setExportThumb(final boolean exportThumb) {
        this.exportThumb = exportThumb;
    }

    public boolean isExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(final boolean exportPdf) {
        this.exportPdf = exportPdf;
    }

    public boolean isExportAipSip() {
        return exportAipSip;
    }

    public void setExportAipSip(final boolean exportAipSip) {
        this.exportAipSip = exportAipSip;
    }
 
}
