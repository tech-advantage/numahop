package fr.progilone.pgcn.domain.dto.administration;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

import java.util.List;
import java.util.Objects;

/**
 * Created by Sébastien on 30/12/2016.
 */
public class SftpConfigurationDTO {

    private String identifier;
    private String label;
    private String username;
    private String host;
    private Integer port;
    private String targetDir;
    private SimpleLibraryDTO library;
    private List<CinesPACDTO> pacs;
    private boolean active;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(final String targetDir) {
        this.targetDir = targetDir;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final SftpConfigurationDTO that = (SftpConfigurationDTO) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public List<CinesPACDTO> getPacs() {
        return pacs;
    }

    public void setPacs(List<CinesPACDTO> pacs) {
        this.pacs = pacs;
    }
}
