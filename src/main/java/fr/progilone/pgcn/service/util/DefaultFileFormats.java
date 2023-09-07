package fr.progilone.pgcn.service.util;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration.FileFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultFileFormats {

    /* Valeurs par défaut paramétrées */
    @Value("${images.format.default.thumbHeight}")
    private long defThumbHeight;

    @Value("${images.format.default.thumbWidth}")
    private long defThumbWidth;

    @Value("${images.format.default.viewHeight}")
    private long defViewHeight;

    @Value("${images.format.default.viewWidth}")
    private long defViewWidth;

    @Value("${images.format.default.printHeight}")
    private long defPrintHeight;

    @Value("${images.format.default.printWidth}")
    private long defPrintWidth;

    public long getDefThumbHeight() {
        return defThumbHeight;
    }

    public long getDefThumbWidth() {
        return defThumbWidth;
    }

    public long getDefViewHeight() {
        return defViewHeight;
    }

    public long getDefViewWidth() {
        return defViewWidth;
    }

    public long getDefPrintHeight() {
        return defPrintHeight;
    }

    public long getDefPrintWidth() {
        return defPrintWidth;
    }

    public long getWidthByFormat(final FileFormat format) {
        final long width;
        switch (format) {
            case THUMB:
                width = getDefThumbWidth();
                break;
            case VIEW:
                width = getDefViewWidth();
                break;
            case PRINT:
                width = getDefPrintWidth();
                break;
            default:
                width = 0l;
                break;
        }
        return width;
    }

    public long getHeightByFormat(final FileFormat format) {
        final long height;
        switch (format) {
            case THUMB:
                height = getDefThumbHeight();
                break;
            case VIEW:
                height = getDefViewHeight();
                break;
            case PRINT:
                height = getDefPrintHeight();
                break;
            default:
                height = 0l;
                break;
        }
        return height;
    }

}
