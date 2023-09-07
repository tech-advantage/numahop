package fr.progilone.pgcn.domain.dto.administration.viewsFormat;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

public class ViewsFormatConfigurationDTO extends AbstractVersionedDTO {

    private String identifier;
    private String label;
    private Long thumbWidth;
    private Long thumbHeight;
    private Long viewWidth;
    private Long viewHeight;
    private Long printWidth;
    private Long printHeight;

    private String thumbDefaultValue;
    private String viewDefaultValue;
    private String printDefaultValue;

    private SimpleLibraryDTO library;

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

    public Long getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(final Long thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public Long getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(final Long thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public Long getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(final Long viewWidth) {
        this.viewWidth = viewWidth;
    }

    public Long getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(final Long viewHeight) {
        this.viewHeight = viewHeight;
    }

    public Long getPrintWidth() {
        return printWidth;
    }

    public void setPrintWidth(final Long printWidth) {
        this.printWidth = printWidth;
    }

    public Long getPrintHeight() {
        return printHeight;
    }

    public void setPrintHeight(final Long printHeight) {
        this.printHeight = printHeight;
    }

    public String getThumbDefaultValue() {
        return thumbDefaultValue;
    }

    public void setThumbDefaultValue(final String thumbDefaultValue) {
        this.thumbDefaultValue = thumbDefaultValue;
    }

    public String getViewDefaultValue() {
        return viewDefaultValue;
    }

    public void setViewDefaultValue(final String viewDefaultValue) {
        this.viewDefaultValue = viewDefaultValue;
    }

    public String getPrintDefaultValue() {
        return printDefaultValue;
    }

    public void setPrintDefaultValue(final String printDefaultValue) {
        this.printDefaultValue = printDefaultValue;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

}
