package fr.progilone.pgcn.service.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;

/**
 * Classe fournissant des méthodes permettant de manipuler des images
 *
 * @author David
 */
public final class ImageUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_JPG = "jpg";

    private ImageUtils() {
    }

    public static byte[] getImageInBytes(final String imageString) {
        // On supprime la partie de type "data:image/png;base64,"
        return Base64.decodeBase64(imageString.substring(imageString.indexOf(',') + 1));
    }

    /**
     * Converti une chaine Base 64 en {@link BufferedImage}
     *
     * @param imageString
     * @return Retourne une image décodée
     * @throws IOException
     */
    public static BufferedImage decodeToImage(final String imageString) throws IOException {
        BufferedImage image = null;
        //
        final byte[] imageByte = getImageInBytes(imageString);
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
            image = ImageIO.read(bis);
        }
        return image;
    }

    /**
     * Converti une {@link BufferedImage} en String encodée en Base64
     *
     * @param image
     * @param type
     * @return La chaine encodée
     * @throws IOException
     */
    public static String encodeToString(final BufferedImage image, final String type) throws IOException {
        String imageString = null;
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, type, bos);
            final byte[] imageBytes = bos.toByteArray();
            imageString = "data:image/" + type + ";base64," + Base64.encodeBase64String(imageBytes);
        }
        return imageString;
    }

    public static String resizeImage(final String image, final int maxSize, final String type) {
        if (StringUtils.isNotBlank(image)) {
            try {
                final BufferedImage bi = ImageUtils.decodeToImage(image);
                if (bi != null) {
                    if (bi.getHeight() > maxSize || bi.getWidth() > maxSize) {
                        return ImageUtils.encodeToString(Scalr.resize(bi, Method.AUTOMATIC, maxSize), type);
                    }
                }
            } catch (final IOException e) {
                LOG.error("Problème de traitement d'une image", e);
            }
        }
        return image;
    }

    /**
     * Génération d'une vignette au format PNG
     *
     * @param sourceFile
     * @param destFile
     * @param width
     * @param height
     */
    public static boolean createThumbnail(final File sourceFile, final File destFile, final Integer width, final Integer height) {
        if (sourceFile != null && destFile != null && width != null && height != null) {
            try {
                BufferedImage bi = ImageIO.read(sourceFile);
                bi = Scalr.resize(bi, Method.SPEED, width, height);
                return ImageIO.write(bi, FORMAT_PNG, destFile);
            } catch (final IOException e) {
                LOG.error("Problème de création de la vignette de l'image", e);
            } catch (final ArrayIndexOutOfBoundsException e1) {
                LOG.error("L'image spécifiée ne possède pas de vignette", e1);
            }
        } else {
            LOG.error("La création de la vignette est impossible : le fichier source ou le fichier destination n'existe pas");
        }
        return false;
    }

    /**
     * Génération d'une vignette au format PNG à partir d'un flux entrant
     *
     * @param sourceStream
     * @param destFile
     * @param width
     * @param height
     */
    public static boolean createThumbnail(final InputStream sourceStream, final File destFile, final Integer width, final Integer height) {
        if (sourceStream != null && destFile != null && width != null && height != null) {
            try {
                BufferedImage bi = ImageIO.read(sourceStream);
                if (width > 0 && height > 0) {
                    bi = Scalr.resize(bi, Method.SPEED, width, height);
                }
                return ImageIO.write(bi, FORMAT_PNG, destFile);
            } catch (final IOException e) {
                LOG.error("Problème de création de la vignette de l'image", e);
            } catch (final ArrayIndexOutOfBoundsException e1) {
                LOG.error("L'image spécifiée ne possède pas de vignette", e1);
            }
        } else {
            LOG.error("La création de la vignette est impossible : le fichier source ou le fichier destination n'existe pas");
        }
        return false;
    }

    /**
     * Génération d'une image dérivée au format JPEG
     * 
     * @param sourceFile
     * @param destFile
     * @param format
     * @param formatConfiguration
     * @return
     */
    public static boolean createDerived(final File sourceFile, final File destFile, 
                                        final ViewsFormatConfiguration.FileFormat format, 
                                        final ViewsFormatConfiguration formatConfiguration) {

        int width = 0;
        int height = 0;
        if (format != null) {
            if (ViewsFormatConfiguration.FileFormat.XTRAZOOM.equals(format)) {
                width = ImageUtils.getWidth(sourceFile);
                height = ImageUtils.getHeight(sourceFile);
            } else if (ViewsFormatConfiguration.FileFormat.ZOOM.equals(format)) {
                width = ImageUtils.getWidth(sourceFile) / 2;
                height = ImageUtils.getHeight(sourceFile) / 2;
            } else {
                if (formatConfiguration != null) {
                    width = (int)  formatConfiguration.getWidthByFormat(format);
                    height = (int) formatConfiguration.getHeightByFormat(format);
                }
            }
        }
        
        if (sourceFile == null || destFile == null || width == 0 || height == 0) {
            LOG.error("Création du fichier dérivé impossible: le fichier source ou le fichier destination n'existe pas.");
            return false;
        }

        try {
            BufferedImage bi = ImageIO.read(sourceFile);
            // resize de qualite !
            bi = Scalr.resize(bi, Method.ULTRA_QUALITY, width, height);

          final ImageWriter writer = ImageIO.getImageWritersBySuffix(FORMAT_JPG).next();
          final ImageWriteParam params = writer.getDefaultWriteParam();
          params.setCompressionQuality(0.9F);
          final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream( new FileOutputStream(destFile) );
          writer.setOutput( imageOutputStream );

          writer.write(null, new IIOImage(bi, null, null), params);
          return true;
        } catch (final IOException e) {
            LOG.error("Problème de création de l'image derivée de type {}", format.label(), e);
            return false;
        }
    }

    public static String encodeImageFileToString(final File thumbnail) {
        try {
            final BufferedImage bi = ImageIO.read(thumbnail);
            return encodeToString(bi, FORMAT_PNG);
        } catch (final IOException e) {
            LOG.error("Problème de création de l'image", e);
        }
        return null;
    }

    public static int getWidth(final File file) {
        try {
            final BufferedImage bi = ImageIO.read(file);
            return bi.getWidth();
        } catch (final IOException e) {
            LOG.error("Problème de récupération de la largeur de l'image", e);
            return -1;
        }
    }

    public static int getHeight(final File file) {
        try {
            final BufferedImage bi = ImageIO.read(file);
            return bi.getHeight();
        } catch (final IOException e) {
            LOG.error("Problème de récupération de la hauteur de l'image", e);
            return -1;
        }
    }

    /**
     * Retourne les dimensions de l'image.
     *
     * @param file
     * @param mimeType
     * @return
     */
    public Optional<Dimension> getImgDimension(final File file, final Optional<String> mimeType) {

        // On evite ImageIO.read car + couteux (lecture de tout le fichier)...
        Dimension dim = null;
        final String typeMime;
        if (mimeType.isPresent()) {
            typeMime = mimeType.get();
        } else {
            final Tika tika = new Tika();
            try {
                typeMime = tika.detect(file);
            } catch (final IOException e) {
                LOG.error("Can't detect mimeType of file {}", file);
                return Optional.empty();
            }
        }
        // ne gere pas le jp2, mais comme on ne fait pas les masters...
        if (StringUtils.equalsIgnoreCase("image/jp2", typeMime)) {
            return Optional.empty();
        }

        final Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType(typeMime);
        if (it.hasNext()) {
            final ImageReader reader = it.next();
            try {
                final ImageInputStream stream = new FileImageInputStream(file);
                reader.setInput(stream);
                final int width = reader.getWidth(reader.getMinIndex());
                final int height = reader.getHeight(reader.getMinIndex());
                dim = new Dimension(width, height);
            } catch (final IOException e) {
                LOG.error(e.getMessage());
            } finally {
                reader.dispose();
            }
        } else {
            LOG.warn("No reader found for given mimeType: {}", mimeType);
        }
        return Optional.of(dim);
    }

}
