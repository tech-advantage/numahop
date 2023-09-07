package fr.progilone.pgcn.domain.dto.ftpconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

public class FTPConfigurationDTO extends AbstractVersionedDTO {

    private String identifier;
    private String label;
    private SimpleLibraryDTO library;
    private String address;
    private String login;

    private String password;

    private String deliveryFolder;

    public FTPConfigurationDTO(String identifier, String label, SimpleLibraryDTO library, String address, String login, String password, String deliveryFolder) {
        this.identifier = identifier;
        this.label = label;
        this.library = library;
        this.address = address;
        this.login = login;
        this.password = password;
        this.deliveryFolder = deliveryFolder;
    }

    public FTPConfigurationDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeliveryFolder() {
        return deliveryFolder;
    }

    public void setDeliveryFolder(String deliveryFolder) {
        this.deliveryFolder = deliveryFolder;
    }

    public static final class Builder {

        private String identifier;
        private String label;
        private SimpleLibraryDTO library;
        private String address;
        private String login;
        private String password;
        private String deliveryFolder;

        public Builder init() {
            this.identifier = null;
            this.label = null;
            this.library = null;
            this.address = null;
            this.login = null;
            this.password = null;
            this.deliveryFolder = null;
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setLibrary(SimpleLibraryDTO library) {
            this.library = library;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDeliveryFolder(String deliveryFolder) {
            this.deliveryFolder = deliveryFolder;
            return this;
        }

        public FTPConfigurationDTO build() {
            return new FTPConfigurationDTO(identifier, label, library, address, login, password, deliveryFolder);
        }
    }
}
