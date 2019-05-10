package fr.progilone.pgcn.service.exchange.cines;

public final class SipUtils {

    private SipUtils() {
    }

    /**
     * Génère une enum valide pour le sip.xml à partir du type MIME
     * @param mimeType
     * @return
     */
    public static String convertMimeTypeToSipFormat(String mimeType) {
        switch(mimeType) {
        case "image/gif": return "GIF";
        case "image/jp2": return "JPEG2000";
        case "image/jpeg": return "JPEG";
        case "image/tiff": return "TIFF";
        case "application/pdf": return "PDF";
        case "image/png": return "PNG";
        case "image/svg+xml": return "SVG";
        default: return "";
        }
    }
}
