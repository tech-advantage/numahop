package fr.progilone.pgcn.domain.administration.viewsformat;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = ViewsFormatConfiguration.TABLE_NAME)
public class ViewsFormatConfiguration extends AbstractDomainObject {

    /**
     * Nom de la table en base de données.
     */
    public static final String TABLE_NAME = "conf_views_format_configuration";

    /* Valeurs par défaut paramétrées - non persistées */
    @Transient
    private DefaultFileFormats defaultFormats;

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    /**
     * Valeurs configurées.
     */
    @Column(name = "thumb_width")
    private Long thumbWidth;

    @Column(name = "thumb_height")
    private Long thumbHeight;

    @Column(name = "view_width")
    private Long viewWidth;

    @Column(name = "view_height")
    private Long viewHeight;

    @Column(name = "print_width")
    private Long printWidth;

    @Column(name = "print_height")
    private Long printHeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public DefaultFileFormats getDefaultFormats() {
        return defaultFormats;
    }

    public void setDefaultFormats(final DefaultFileFormats defaultFormats) {
        this.defaultFormats = defaultFormats;
    }

    public long getWidthByFormat(final FileFormat format) {
        final long width;
        switch (format) {
            case THUMB:
                if (getThumbWidth() > 0) {
                    width = getThumbWidth();
                } else {
                    width = getDefaultFormats().getDefThumbWidth();
                }
                break;
            case VIEW:
                if (getViewWidth() > 0) {
                    width = getViewWidth();
                } else {
                    width = getDefaultFormats().getDefViewWidth();
                }
                break;
            case PRINT:
                if (getPrintWidth() > 0) {
                    width = getPrintWidth();
                } else {
                    width = getDefaultFormats().getDefPrintWidth();
                }
                break;
            default:
                width = 0l;
                break;
        }
        return width;
    }

    public long getHeightByFormat(final FileFormat format) {
        final long width;
        switch (format) {
            case THUMB:
                if (getThumbHeight() > 0) {
                    width = getThumbHeight();
                } else {
                    width = getDefaultFormats().getDefThumbHeight();
                }
                break;
            case VIEW:
                if (getViewHeight() > 0) {
                    width = getViewHeight();
                } else {
                    width = getDefaultFormats().getDefViewHeight();
                }
                break;
            case PRINT:
                if (getPrintHeight() > 0) {
                    width = getPrintHeight();
                } else {
                    width = getDefaultFormats().getDefPrintHeight();
                }
                break;
            default:
                width = 0l;
                break;
        }
        return width;
    }

    /**
     * format de fichiers images.
     */
    public enum FileFormat {

        /**
         * format master
         */
        MASTER("formatMaster", "Master"),
        /**
         * format d'impression
         */
        PRINT("formatPrint", "Print"),
        /**
         * Format vignette
         */
        THUMB("formatThumb", "Thumbnail"),
        /**
         * Format vue standard
         */
        VIEW("formatView", "View"),
        /**
         * Format zoom
         */
        ZOOM("formatZoom", "Zoom"),
        /**
         * et super mega zoom
         */
        XTRAZOOM("formatXtraZoom", "XtraZoom");

        private final String identifier;
        private final String label;

        FileFormat(final String identifier, final String label) {
            this.identifier = identifier;
            this.label = label;
        }

        public String identifier() {
            return this.identifier;
        }

        public String label() {
            return this.label;
        }

    }

}
