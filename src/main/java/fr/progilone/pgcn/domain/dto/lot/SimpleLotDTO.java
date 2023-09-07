package fr.progilone.pgcn.domain.dto.lot;

import com.querydsl.core.annotations.QueryProjection;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.lot.Lot;

/**
 * DTO représentant un lot allégé
 *
 * @author jbrunet
 */
public class SimpleLotDTO {

    private String identifier;
    private String label;
    private String code;
    private String requiredFormat;
    private SimpleProjectDTO project;
    private Lot.LotStatus status;
    private Lot.Type type;
    private boolean filesArchived;
    private String providerLogin;
    private OcrLanguageDTO activeOcrLanguage;

    public SimpleLotDTO() {
    }

    @QueryProjection
    public SimpleLotDTO(final String identifier, final String label) {
        super();
        this.identifier = identifier;
        this.label = label;
    }

    public SimpleLotDTO(final String identifier,
                        final String code,
                        final String label,
                        final String requiredFormat,
                        final SimpleProjectDTO project,
                        final Lot.LotStatus status,
                        final Lot.Type type,
                        final boolean filesArchived,
                        final OcrLanguageDTO activeOcrLanguage) {
        super();
        this.label = label;
        this.code = code;
        this.identifier = identifier;
        this.requiredFormat = requiredFormat;
        this.project = project;
        this.status = status;
        this.type = type;
        this.filesArchived = filesArchived;
        this.activeOcrLanguage = activeOcrLanguage;
    }

    public Lot.LotStatus getStatus() {
        return status;
    }

    public void setStatus(final Lot.LotStatus status) {
        this.status = status;
    }

    public final String getCode() {
        return code;
    }

    public final String getLabel() {
        return label;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setRequiredFormat(final String requiredFormat) {
        this.requiredFormat = requiredFormat;
    }

    public void setType(final Lot.Type type) {
        this.type = type;
    }

    public String getRequiredFormat() {
        return requiredFormat;
    }

    public Lot.Type getType() {
        return type;
    }

    public boolean isFilesArchived() {
        return filesArchived;
    }

    public void setFilesArchived(final boolean filesArchived) {
        this.filesArchived = filesArchived;
    }

    public String getProviderLogin() {
        return providerLogin;
    }

    public void setProviderLogin(final String providerLogin) {
        this.providerLogin = providerLogin;
    }

    public SimpleProjectDTO getProject() {
        return project;
    }

    public void setProject(final SimpleProjectDTO project) {
        this.project = project;
    }

    public OcrLanguageDTO getActiveOcrLanguage() {
        return activeOcrLanguage;
    }

    public void setActiveOcrLanguage(final OcrLanguageDTO activeOcrLanguage) {
        this.activeOcrLanguage = activeOcrLanguage;
    }

    /**
     * Builder pour la classe SimpleLotDTO
     *
     * @author jbrunet
     */
    public static final class Builder {

        private String identifier;
        private String code;
        private String label;
        private String requiredFormat;
        private SimpleProjectDTO project;
        private Lot.LotStatus status;
        private Lot.Type type;
        private boolean filesArchived;
        private OcrLanguageDTO activeOcrLanguage;

        public Builder reinit() {
            this.identifier = null;
            this.code = null;
            this.label = null;
            this.requiredFormat = null;
            this.project = null;
            this.status = null;
            this.type = null;
            this.filesArchived = false;
            this.activeOcrLanguage = null;
            return this;
        }

        public Builder setIdentifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setCode(final String code) {
            this.code = code;
            return this;
        }

        public Builder setLabel(final String label) {
            this.label = label;
            return this;
        }

        public Builder setStatus(final Lot.LotStatus status) {
            this.status = status;
            return this;
        }

        public Builder setFilesArchived(final boolean archived) {
            this.filesArchived = archived;
            return this;
        }

        public Builder setRequiredFormat(final String requiredFormat) {
            this.requiredFormat = requiredFormat;
            return this;
        }

        public Builder setType(final Lot.Type type) {
            this.type = type;
            return this;
        }

        public Builder setProject(final SimpleProjectDTO project) {
            this.project = project;
            return this;
        }

        public Builder setActiveOcrLanguage(final OcrLanguageDTO activeOcrLanguage) {
            this.activeOcrLanguage = activeOcrLanguage;
            return this;
        }

        public SimpleLotDTO build() {
            return new SimpleLotDTO(identifier, code, label, requiredFormat, project, status, type, filesArchived, activeOcrLanguage);
        }

    }
}
