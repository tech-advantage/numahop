package fr.progilone.pgcn.service.storage;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.ImageUtils;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lebouchp on 27/02/2017.
 */
@Service
public class ImageDispatcherService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDispatcherService.class);

    @Autowired
    private ImageMagickService imageMagickService;
    @Autowired
    private ExifToolService exifToolService;

    /**
     * Création des fichiers dérivés.
     * - La qualité diffère selon le format spécifié.
     * - la conversion via Image Magick est privilégiée (meilleure qualité perçue)
     *
     * @param mimeType
     * @param masterFile
     * @param destinationFile
     *            peut être une vignette ou une dérivé
     * @param format
     * @return
     */
    public boolean createThumbnailDerived(final String mimeType,
                                          final File masterFile,
                                          final File destinationFile,
                                          final ViewsFormatConfiguration.FileFormat format,
                                          final ViewsFormatConfiguration formatConfiguration,
                                          final Long[] masterDims) {
        boolean success = false;

        if (imageMagickService.isConfigured()) {
            try {
                if (ViewsFormatConfiguration.FileFormat.THUMB.equals(format)) {
                    success = imageMagickService.generateThumbnail(masterFile, destinationFile, format, formatConfiguration, masterDims);
                } else {
                    success = imageMagickService.generateDerived(masterFile, destinationFile, format, formatConfiguration, masterDims);
                }
                if (success) {
                    exifToolService.copyAllMetadatas(masterFile, destinationFile);
                }
            } catch (final PgcnTechnicalException e) {
                LOG.error("Utilisation impossible du service ImageMagick pour la conversion", e);
                success = false;
            }
        } else {
            LOG.info("Le service ImageMagick n'est pas configuré => Passage au Mode Dégradé");

            switch (mimeType) {
                case "image/jp2":
                case "image/svg+xml":
                case "application/pdf":
                    success = false;
                    LOG.info("Opération non supportée pour le type {} sans ImageMagick.", mimeType);
                    break;
                default:

                    // Mode degrade => ImageIO.
                    if (ViewsFormatConfiguration.FileFormat.THUMB.equals(format)) {
                        final int thumbHeight = (int) formatConfiguration.getHeightByFormat(ViewsFormatConfiguration.FileFormat.THUMB);
                        final int thumbWidth = (int) formatConfiguration.getWidthByFormat(ViewsFormatConfiguration.FileFormat.THUMB);
                        success = ImageUtils.createThumbnail(masterFile, destinationFile, thumbWidth, thumbHeight);
                    } else {
                        success = ImageUtils.createDerived(masterFile, destinationFile, format, formatConfiguration);
                    }
                    break;
            }
        }
        return success;
    }
}
