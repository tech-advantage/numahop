package fr.progilone.pgcn.domain.dto.administration.omeka;

import java.util.List;
import java.util.Objects;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

/**
 *
 */
public class OmekaConfigurationDTO {

    private String identifier;
    private String label;
    private String storageServer;
    private String port;
    private SimpleLibraryDTO library;
    private List<OmekaListDTO> omekaCollections;
    private List<OmekaListDTO> omekaItems;
    private List<OmekaListDTO> omekaLists;
    private boolean active;
    private String address;
    private String login;
    private String password;
    private String accessUrl;
    private String mailCsv;
    
    private boolean exportMets;
    private boolean exportMaster;
    private boolean exportView;
    private boolean exportThumb;
    private boolean exportPdf;
    

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

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    

    public String getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(final String storageServer) {
        this.storageServer = storageServer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
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


    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(final String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getMailCsv() {
        return mailCsv;
    }

    public void setMailCsv(final String mailCsv) {
        this.mailCsv = mailCsv;
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

    public List<OmekaListDTO> getOmekaCollections() {
        return omekaCollections;
    }

    public void setOmekaCollections(final List<OmekaListDTO> omekaCollections) {
        this.omekaCollections = omekaCollections;
    }

    public List<OmekaListDTO> getOmekaItems() {
        return omekaItems;
    }

    public void setOmekaItems(final List<OmekaListDTO> omekaItems) {
        this.omekaItems = omekaItems;
    }

    public List<OmekaListDTO> getOmekaLists() {
        return omekaLists;
    }

    public void setOmekaLists(final List<OmekaListDTO> omekaLists) {
        this.omekaLists = omekaLists;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final OmekaConfigurationDTO that = (OmekaConfigurationDTO) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
