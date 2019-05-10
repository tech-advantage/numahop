package fr.progilone.pgcn.domain.dto.library;

import java.time.LocalDateTime;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.SimpleOcrLangConfigDTO;
import fr.progilone.pgcn.domain.dto.user.AddressDTO;
import fr.progilone.pgcn.domain.dto.user.RoleDTO;

/**
 * DTO représentant une bibliothèque
 */
public class LibraryDTO extends AbstractVersionedDTO {

    private String identifier;
    private String name;
    private String website;
    private String phoneNumber;
    private String email;
    private String prefix;
    private String number;
    private boolean active;
    private String institution;
    private AddressDTO address;
    private String cinesService;
    private Set<SimpleFTPConfigurationDTO> ftpConfigurations;
    private SimpleFTPConfigurationDTO activeFTPConfiguration;
    private Set<SimpleCheckConfigurationDTO> checkConfigurations;
    private SimpleCheckConfigurationDTO activeCheckConfiguration;
    private Set<SimpleViewsFormatConfigurationDTO> viewsFormatConfigurations;
    private SimpleViewsFormatConfigurationDTO activeFormatConfiguration;
    private Set<SimpleOcrLangConfigDTO> ocrLangConfigurations;
    private SimpleOcrLangConfigDTO activeOcrLangConfiguration;
    
    
    private RoleDTO defaultRole;
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

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public final String getWebsite() {
        return website;
    }

    public final void setWebsite(final String website) {
        this.website = website;
    }

    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(final String email) {
        this.email = email;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public final String getNumber() {
        return number;
    }

    public final void setNumber(final String number) {
        this.number = number;
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive(final boolean active) {
        this.active = active;
    }

    public final AddressDTO getAddress() {
        return address;
    }

    public final void setAddress(final AddressDTO address) {
        this.address = address;
    }

    public final String getCinesService() {
        return cinesService;
    }

    public final void setCinesService(final String cinesService) {
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

    public RoleDTO getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(final RoleDTO defaultRole) {
        this.defaultRole = defaultRole;
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
