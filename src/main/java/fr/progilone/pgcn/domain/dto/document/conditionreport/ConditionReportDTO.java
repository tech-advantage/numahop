package fr.progilone.pgcn.domain.dto.document.conditionreport;

import com.google.common.base.MoreObjects;

public class ConditionReportDTO {

    private String identifier;
    private String libRespName;
    private String libRespPhone;
    private String libRespEmail;
    private String leaderName;
    private String leaderPhone;
    private String leaderEmail;
    private String providerName;
    private String providerPhone;
    private String providerEmail;
    private String providerContactName;
    private String providerContactPhone;
    private String providerContactEmail;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLibRespName() {
        return libRespName;
    }

    public void setLibRespName(final String libRespName) {
        this.libRespName = libRespName;
    }

    public String getLibRespPhone() {
        return libRespPhone;
    }

    public void setLibRespPhone(final String libRespPhone) {
        this.libRespPhone = libRespPhone;
    }

    public String getLibRespEmail() {
        return libRespEmail;
    }

    public void setLibRespEmail(final String libRespEmail) {
        this.libRespEmail = libRespEmail;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(final String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderPhone() {
        return leaderPhone;
    }

    public void setLeaderPhone(final String leaderPhone) {
        this.leaderPhone = leaderPhone;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public void setLeaderEmail(final String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(final String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(final String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public String getProviderContactName() {
        return providerContactName;
    }

    public void setProviderContactName(final String providerContactName) {
        this.providerContactName = providerContactName;
    }

    public String getProviderContactPhone() {
        return providerContactPhone;
    }

    public void setProviderContactPhone(final String providerContactPhone) {
        this.providerContactPhone = providerContactPhone;
    }

    public String getProviderContactEmail() {
        return providerContactEmail;
    }

    public void setProviderContactEmail(final String providerContactEmail) {
        this.providerContactEmail = providerContactEmail;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("identifier", identifier).toString();
    }
}
