package fr.progilone.pgcn.domain.dto.library;

import java.time.LocalDateTime;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.SimpleOcrLangConfigDTO;
import fr.progilone.pgcn.domain.dto.user.AddressDTO;

/**
 * DTO représentant une bibliothèque
 */
public class LibraryDTO extends SimpleLibraryDTO {

    private String website;
    private String phoneNumber;
    private String email;
    private String number;
    private boolean active;
    private String institution;
    private AddressDTO address;
    // responsable bibliotheque
    private String cinesService;
    private Set<SimpleFTPConfigurationDTO> ftpConfigurations;
    private SimpleFTPConfigurationDTO activeFTPConfiguration;
    private Set<SimpleCheckConfigurationDTO> checkConfigurations;
    private SimpleCheckConfigurationDTO activeCheckConfiguration;
    private Set<SimpleViewsFormatConfigurationDTO> viewsFormatConfigurations;
    private SimpleViewsFormatConfigurationDTO activeFormatConfiguration;
    private Set<SimpleOcrLangConfigDTO> ocrLangConfigurations;
    private SimpleOcrLangConfigDTO activeOcrLangConfiguration;
    
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public LibraryDTO() {
    }

    public final String getWebsite() {
        return website;
    }

    public void setWebsite(final String website) {
        this.website = website;
    }

    public final String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public final String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public final String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public final boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public final AddressDTO getAddress() {
        return address;
    }

    public void setAddress(final AddressDTO address) {
        this.address = address;
    }

    public final String getCinesService() {
        return cinesService;
    }

    public void setCinesService(final String cinesService) {
        this.cinesService = cinesService;
    }

    public void setActiveFTPConfiguration(final SimpleFTPConfigurationDTO activeFTPConfiguration) {
        this.activeFTPConfiguration = activeFTPConfiguration;
    }

    public SimpleFTPConfigurationDTO getActiveFTPConfiguration() {
        return activeFTPConfiguration;
    }

    public Set<SimpleFTPConfigurationDTO> getFtpConfigurations() {
        return ftpConfigurations;
    }

    public void setFtpConfigurations(final Set<SimpleFTPConfigurationDTO> ftpConfigurations) {
        this.ftpConfigurations = ftpConfigurations;
    }

    public Set<SimpleCheckConfigurationDTO> getCheckConfigurations() {
        return checkConfigurations;
    }

    public void setCheckConfigurations(final Set<SimpleCheckConfigurationDTO> checkConfigurations) {
        this.checkConfigurations = checkConfigurations;
    }

    public SimpleCheckConfigurationDTO getActiveCheckConfiguration() {
        return activeCheckConfiguration;
    }

    public void setActiveCheckConfiguration(final SimpleCheckConfigurationDTO activeCheckConfiguration) {
        this.activeCheckConfiguration = activeCheckConfiguration;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(final String institution) {
        this.institution = institution;
    }

    public Set<SimpleViewsFormatConfigurationDTO> getViewsFormatConfigurations() {
        return viewsFormatConfigurations;
    }

    public void setViewsFormatConfigurations(final Set<SimpleViewsFormatConfigurationDTO> viewFormatConfigurations) {
        this.viewsFormatConfigurations = viewFormatConfigurations;
    }

    public SimpleViewsFormatConfigurationDTO getActiveFormatConfiguration() {
        return activeFormatConfiguration;
    }

    public void setActiveFormatConfiguration(final SimpleViewsFormatConfigurationDTO activeFormatConfiguration) {
        this.activeFormatConfiguration = activeFormatConfiguration;
    }

    public Set<SimpleOcrLangConfigDTO> getOcrLangConfigurations() {
        return ocrLangConfigurations;
    }

    public void setOcrLangConfigurations(final Set<SimpleOcrLangConfigDTO> ocrLangConfigurations) {
        this.ocrLangConfigurations = ocrLangConfigurations;
    }

    public SimpleOcrLangConfigDTO getActiveOcrLangConfiguration() {
        return activeOcrLangConfiguration;
    }

    public void setActiveOcrLangConfiguration(final SimpleOcrLangConfigDTO activeOcrLangConfiguration) {
        this.activeOcrLangConfiguration = activeOcrLangConfiguration;
    }
}
