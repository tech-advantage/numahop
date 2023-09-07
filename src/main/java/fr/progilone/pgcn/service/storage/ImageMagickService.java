package fr.progilone.pgcn.service.storage;

import static fr.progilone.pgcn.service.storage.BinaryStorageManager.Metadatas;

import com.lowagie.text.pdf.PdfReader;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service d'interface avec Image magick
 *
 * @author jbrunet
 *
 */
@Service
public class ImageMagickService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMagickService.class);

    @Autowired
    private DefaultFileFormats defaultFileFormats;

    private static final String META_DATAS_SEPARATOR = " #META_DATAS_SEPARATOR# ";
    private static final String META_DATAS_SEPARATOR_PATTERN_QUOTE = Pattern.quote(META_DATAS_SEPARATOR.trim()); // Le .trim() est important : sans
                                                                                                                 // lui on "perd" les valeurs vides si
                                                                                                                 // elles sont en fin de chaîne
    private static final String FORMAT_ARGS = "%b" + META_DATAS_SEPARATOR
                                              + "%e"
                                              + META_DATAS_SEPARATOR
                                              + "%h"
                                              + META_DATAS_SEPARATOR
                                              + "%w"
                                              + META_DATAS_SEPARATOR
                                              + "%x"
                                              + META_DATAS_SEPARATOR
                                              + "%y"
                                              + META_DATAS_SEPARATOR
                                              + "%z"
                                              + META_DATAS_SEPARATOR
                                              + "%C"
                                              + META_DATAS_SEPARATOR
                                              + "%[profile:icc]\n";

    private static final String PATH_SEPARATOR = "/";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String DIGIT_NUMBER = "%04d";
    @Value("${exifTool.quot_char}")
    private String quoteDelim;
    private String IMConverterPath;
    private String IMIdentifyPath;
    private boolean isConfigured;

    public void initialize(final String IMConvertPath, final String IMIdentifyPath) {
        this.IMConverterPath = IMConvertPath;
        this.IMIdentifyPath = IMIdentifyPath;
        this.isConfigured = getConfigurationState();
    }

    /**
     * Génère un fichier dérivé avec une conservation correcte de qualité
     *
     * @param sourceFile
     * @param destTmpFile
     * @param format
     * @throws PgcnTechnicalException
     */
    public boolean generateDerived(final File sourceFile,
                                   final File destTmpFile,
                                   final ViewsFormatConfiguration.FileFormat format,
                                   final ViewsFormatConfiguration formatConfiguration,
                                   final Long[] masterDims) throws PgcnTechnicalException {
        return generateFile(sourceFile, destTmpFile, format, formatConfiguration, IMOperations.RESIZE, masterDims);
    }

    /**
     * Génère un fichier dérivé de type vignette avec compression correspondante (perte de qualité)
     *
     * @param sourceFile
     * @param destTmpFile
     * @param format
     * @return true si le traitement s'est bien passé
     * @throws PgcnTechnicalException
     */
    public boolean generateThumbnail(final File sourceFile,
                                     final File destTmpFile,
                                     final ViewsFormatConfiguration.FileFormat format,
                                     final ViewsFormatConfiguration formatConfiguration,
                                     final Long[] masterDims) throws PgcnTechnicalException {
        return generateFile(sourceFile, destTmpFile, format, formatConfiguration, IMOperations.THUMBNAIL, masterDims);
    }

    /**
     * convert several img to a new pdf
     *
     * @param imgsToConvert
     * @param destPath
     * @return created pdf file
     */
    public File convertImgFromDirectoryToPdf(final String prefix, final List<File> imgsToConvert, final String destPath) {
        // get the path of img
        File newPdf = null;

        if (!imgsToConvert.isEmpty()) {
            final String destPdf = destPath + PATH_SEPARATOR
                                   + prefix
                                   + PDF_EXTENSION;

            for (int i = 0; i < imgsToConvert.size(); i++) {
                try {
                    Files.copy(imgsToConvert.get(i).toPath(),
                               Paths.get(destPath + PATH_SEPARATOR
                                         + String.format(DIGIT_NUMBER, i)));
                } catch (final IOException e) {
                    LOG.error("Impossible to copy from " + destPath
                              + ", {}",
                              e);
                }
            }
            ;

            try {
                final ProcessBuilder builder = new ProcessBuilder(IMConverterPath, "*", "-quality", "100", destPdf); // NOSONAR

                builder.directory(new File(destPath));
                final Process process = builder.start();
                if (process.waitFor() == 0) {
                    // ok, convert is done
                    newPdf = new File(destPdf);

                    removeDerivedFiles(imgsToConvert, destPath);
                    if (!newPdf.canRead()) {
                        LOG.info("[ImageMagick] Unable to extract images from pdf file {}", prefix);
                    }
                } else {
                    if (process.isAlive()) {
                        process.destroyForcibly().waitFor();
                    }
                    // Les fichiers sont quand même générés mais le process est bloqué par des warnings
                    newPdf = new File(destPdf);

                    removeDerivedFiles(imgsToConvert, destPath);

                    if (!newPdf.canRead()) {
                        LOG.info("[ImageMagick] Unable to extract images from pdf file {} - interval:{}",
                                 prefix,
                                 process.isAlive() ? "processus still running..."
                                                   : process.exitValue());
                    }

                    final List<String> infos = IOUtils.readLines(process.getInputStream(), StandardCharsets.UTF_8);
                    if (!infos.isEmpty()) {
                        LOG.debug("[ImageMagick] info during images extraction : {}", infos);
                    }
                    final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (!errors.isEmpty()) {
                        LOG.error("[ImageMagick] Error during images extraction : {}", errors);
                    }
                }
            } catch (final IOException | InterruptedException e) {
                LOG.error("[ImageMagick] Unable to extract images from pdf file", e);
            }
        }

        return newPdf;
    }

    /**
     * Remove the derived images after pdf generated
     *
     * @param imgsToConvert
     * @param destPath
     */
    private void removeDerivedFiles(final List<File> imgsToConvert, final String destPath) {
        for (int i = 0; i < imgsToConvert.size(); i++) {
            try {
                Files.deleteIfExists(Paths.get(destPath + PATH_SEPARATOR
                                               + String.format(DIGIT_NUMBER, i)));
            } catch (final IOException e) {
                LOG.error("Impossible to copy from " + destPath
                          + ", {}",
                          e);
            }
        }
        ;
    }

    /**
     * Extraction des pages d'un pdf en images jpg.
     *
     * @param sourceFile
     * @param destFile
     * @return
     */
    public List<File> extractImgFromPdf(final File sourceFile, final String destFile) {

        LOG.info("[Livraison] Extraction des images depuis le fichier pdf {}", sourceFile.getName());
        List<File> files = new ArrayList<>();
        try {
            final int nbPages = getPdfPageNumber(sourceFile.getAbsolutePath());
            LOG.debug("$$PDF_DEBUG - nb pages donné par PdfReader = {}", nbPages);

            final int intervalPage = 10;
            final int quotient = nbPages / intervalPage;
            final int rest = nbPages % intervalPage;

            if (nbPages > 0) {
                for (int i = 0; i <= quotient; i++) {
                    List<File> filesInterval = new ArrayList<>();

                    String interval = "[" + intervalPage * i
                                      + "-"
                                      + (intervalPage * (i + 1) - 1)
                                      + "]";
                    if (i == quotient) {
                        interval = "[" + intervalPage * i
                                   + "-"
                                   + (intervalPage * i + (rest - 1))
                                   + "]";
                    }

                    LOG.info("[ImageMagick] Extract images from pdf file {} {}", sourceFile, interval);

                    final ProcessBuilder builder = new ProcessBuilder(IMConverterPath,
                                                                      "+adjoin",
                                                                      "-density",
                                                                      "300",
                                                                      "-quality",
                                                                      "100",
                                                                      sourceFile.getAbsolutePath() + interval, // NOSONAR
                                                                      destFile); // NOSONA

                    final Process process = builder.start();
                    if (process.waitFor() == 0) {
                        // ok, convert is done
                        filesInterval = getExtractedFiles(destFile);
                        if (filesInterval.isEmpty()) {
                            LOG.info("[ImageMagick] Unable to extract images from pdf file {} - interval {}", sourceFile, interval);
                        } else {
                            LOG.debug("PDF - Process *NORMAL* nb pages extraites du pdf = {}", filesInterval.size());
                        }

                    } else {
                        if (process.isAlive()) {
                            process.destroyForcibly().waitFor();
                        }
                        // Les fichiers sont quand même générés mais le process est bloqué par des warnings
                        filesInterval = getExtractedFiles(destFile);
                        if (filesInterval.isEmpty()) {
                            LOG.info("[ImageMagick] Unable to extract images from pdf file {} - interval:{} - {}",
                                     sourceFile,
                                     interval,
                                     process.isAlive() ? "processus still running..."
                                                       : process.exitValue());
                        } else {
                            LOG.debug("PDF - Process *BLOCAGES PAR WARNINGS* nb pages extraites du pdf = {}", filesInterval.size());
                        }

                        final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                        if (!errors.isEmpty()) {
                            LOG.error("[ImageMagick] Error during images extraction : {}", errors);
                        }
                    }
                }
            }
            files = getExtractedFiles(destFile);
        } catch (final IOException | InterruptedException e) {
            LOG.error("[ImageMagick] Unable to extract images from pdf file", e);
        }
        return files;
    }

    /**
     *
     * @param destFile
     * @return
     * @throws IOException
     */
    private List<File> getExtractedFiles(final String destFile) throws IOException {
        final List<File> files = new ArrayList<>();
        final Path dir = Paths.get(destFile).getParent();
        if (dir != null) {
            try (final DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, "*.jpg")) {
                dirStream.forEach(file -> {
                    files.add(file.toFile());
                });
            }
        }
        return files;
    }

    /**
     * Retourne le nbre de pages du fichier pdf.
     *
     * @param pdfFilePath
     * @return
     */
    private int getPdfPageNumber(final String pdfFilePath) {
        PdfReader reader = null;
        int nbPages = 0;
        try {
            reader = new PdfReader(pdfFilePath);
            nbPages = reader.getNumberOfPages();
        } catch (final IOException e) {
            LOG.error("Unable to get pages number from pdf file", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return nbPages;
    }

    /**
     * Génère un fichier à l'aide d'IM depuis les paramètres dédiés.
     *
     * @param sourceFile
     * @param destTmpFile
     * @param format
     * @param operation
     * @return
     * @throws PgcnTechnicalException
     */
    @SuppressWarnings("findsecbugs:COMMAND_INJECTION")
    private boolean generateFile(final File sourceFile,
                                 final File destTmpFile,
                                 final ViewsFormatConfiguration.FileFormat format,
                                 final ViewsFormatConfiguration formatConfiguration,
                                 final IMOperations operation,
                                 final Long[] masterDims) throws PgcnTechnicalException {
        if (!this.isConfigured) {
            throw new PgcnTechnicalException("Le service de conversion IM n'est pas configuré, impossible de générer les fichiers dérivés");
        }

        final long printWidth = formatConfiguration != null ? formatConfiguration.getWidthByFormat(ViewsFormatConfiguration.FileFormat.PRINT)
                                                            : defaultFileFormats.getDefPrintWidth();

        final long printHeight = formatConfiguration != null ? formatConfiguration.getHeightByFormat(ViewsFormatConfiguration.FileFormat.PRINT)
                                                             : defaultFileFormats.getDefPrintHeight();

        if (sourceFile != null && destTmpFile != null
            && format != null) {

            final String dims;
            final int width = masterDims[0].intValue();
            final int height = masterDims[1].intValue();
            final boolean isXtraZoom = ViewsFormatConfiguration.FileFormat.XTRAZOOM.equals(format);

            if (isXtraZoom) {
                if (width > (2 * printWidth) && height > (2 * printHeight)) {
                    dims = String.valueOf(width) + "x"
                           + String.valueOf(height);
                } else {
                    // pas besoin de XTRA ZOOM => on sort..
                    return false;
                }
            } else if (ViewsFormatConfiguration.FileFormat.ZOOM.equals(format)) {
                // Determine les tailles à dériver selon la taille du master.
                if (width > (5 * printWidth) && height > (5 * printHeight)) {  // + de 5 x les dimensions du Print => /3
                    dims = String.valueOf(width / 3) + "x"
                           + String.valueOf(height / 3);
                } else if (width > (2 * printWidth) && height > (2 * printHeight)) {  // + de 2 x les dimensions du Print => /2
                    dims = String.valueOf(width / 2) + "x"
                           + String.valueOf(height / 2);
                } else {
                    dims = String.valueOf(width) + "x"
                           + String.valueOf(height);
                }
            } else {

                final long otherWidth = formatConfiguration != null ? formatConfiguration.getWidthByFormat(format)
                                                                    : defaultFileFormats.getWidthByFormat(format);

                final long otherHeight = formatConfiguration != null ? formatConfiguration.getHeightByFormat(format)
                                                                     : defaultFileFormats.getHeightByFormat(format);

                dims = String.valueOf(otherWidth).concat("x").concat(String.valueOf(otherHeight));
            }
            ProcessBuilder builder = null;
            try {
                if (isXtraZoom) {
                    // Conversion à taille originale mais qualité dégradée => 80%.
                    builder = new ProcessBuilder(IMConverterPath,
                                                 sourceFile.getAbsolutePath(),
                                                 operation.getValue(),
                                                 dims, // NOSONAR
                                                 "-quality",
                                                 "80%",
                                                 destTmpFile.getAbsolutePath());
                } else {
                    // Conversion (default quality: 92)
                    builder = new ProcessBuilder(IMConverterPath,
                                                 sourceFile.getAbsolutePath(),
                                                 operation.getValue(),
                                                 dims, // NOSONAR
                                                 "-set",
                                                 "density",
                                                 "300",
                                                 "-set",
                                                 "units",
                                                 "PixelsPerInch",
                                                 destTmpFile.getAbsolutePath());
                }
                builder.redirectError(Redirect.INHERIT);
                builder.redirectOutput(Redirect.INHERIT);
                final Process process = builder.start();
                if (process.waitFor() == 0) {
                    // when convert is done, write to the correct output
                    // only if the size is not null
                    if (destTmpFile.length() > 0L) {
                        return true;
                    } else {
                        LOG.info("[ImageMagick] Unable to generate file {}", destTmpFile.getName());
                        return false;
                    }
                } else {
                    if (process.isAlive()) {
                        process.destroyForcibly().waitFor();
                    }
                    // Le fichier est quand même généré mais le process est bloqué par des warnings
                    if (destTmpFile.length() > 0L) {
                        return true;
                    } else {
                        LOG.info("[ImageMagick] Unable to generate file {}", destTmpFile.getName());
                        return false;
                    }

                }
            } catch (final IOException | InterruptedException e) {
                LOG.error("[ImageMagick] Unable to generate derived files", e);
            }
        }
        return false;
    }

    /**
     * Collecte les metadatas du fichier image.
     *
     * @param file
     * @return
     * @throws PgcnTechnicalException
     */
    @SuppressWarnings("findsecbugs:COMMAND_INJECTION")
    public Optional<Metadatas> getMetadatasOfFile(final File file, final boolean isPDF) throws PgcnTechnicalException {

        if (!this.isConfigured) {
            throw new PgcnTechnicalException("Le service de conversion IM n'est pas configuré, impossible de collecter les metadatas.");
        }
        if (file == null) {
            LOG.info("Can't get metadatas of null file");
            return Optional.empty();
        }

        final ProcessBuilder builder = new ProcessBuilder(IMIdentifyPath,
                                                          "-quiet",
                                                          "-units",
                                                          "PixelsPerInch",
                                                          "-format",
                                                          quoteDelim + FORMAT_ARGS
                                                                     + quoteDelim,
                                                          file.getAbsolutePath() + (isPDF ? "[0]"
                                                                                          : ""));

        builder.redirectErrorStream(true);
        try {

            final Process process = builder.start();
            final Metadatas metas = collectMetadatas(process.getInputStream());
            if (process.waitFor() == 0) {
                LOG.info("[ImageMagick] metadatas collected - {}", file.getName());
                return Optional.of(metas);
            } else {
                if (process.isAlive()) {
                    process.destroyForcibly().waitFor();
                }
                if (!metas.isEmpty()) {
                    return Optional.of(metas);
                }
                if (process.isAlive()) {
                    LOG.info("[ImageMagick] Problem when collecting metadatas : {}",
                             process.isAlive() ? "processus still running..."
                                               : process.exitValue());
                    final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (!errors.isEmpty()) {
                        LOG.error("[ImageMagick] Error during metadatas collect : {}", errors);
                    }
                }
            }
        } catch (final IOException | InterruptedException e) {
            throw new PgcnTechnicalException(e);
        }
        return Optional.empty();
    }

    /**
     * Retourne une map minimale des metadatas de l'image.
     *
     * @param is
     * @return
     * @throws IOException
     */
    private Metadatas collectMetadatas(final InputStream is) throws IOException {

        final Metadatas.Builder metas = new Metadatas.Builder();
        IOUtils.readLines(is, StandardCharsets.UTF_8).forEach(ligne -> {
            if (StringUtils.isNotBlank(ligne)) {
                final String[] tab = ligne.split(META_DATAS_SEPARATOR_PATTERN_QUOTE);
                if (tab.length != 9) {
                    return;
                }

                metas.setFilesize(StringUtils.trim(tab[0]));
                metas.setFormat(StringUtils.trim(tab[1]));
                final String colorSpace = StringUtils.trim(tab[8]);
                if (StringUtils.containsIgnoreCase(colorSpace, "Adobe")) {
                    metas.setColorSpace("Adobe RGB");
                } else {
                    metas.setColorSpace("sRGB");
                }
                metas.setCompression(StringUtils.trim(tab[7]));
                metas.setQuality(StringUtils.trim(tab[6]));
                metas.setResolution(StringUtils.trim(tab[4]) + "x"
                                    + StringUtils.trim(tab[5]));
                metas.setHeight(StringUtils.trim(tab[2]));
                metas.setWidth(StringUtils.trim(tab[3]));
            }
        });
        return metas.build();
    }

    /**
     * Vérification de la bonne configuration
     */
    @SuppressWarnings("findsecbugs:COMMAND_INJECTION")
    protected boolean getConfigurationState() {
        if (!new File(IMConverterPath).exists()) {
            LOG.error("[ImageMagick] Converter path \"{}\" does not exist", IMConverterPath);
            return false;
        }
        try {
            final ProcessBuilder builder = new ProcessBuilder(IMConverterPath, "-version");
            builder.redirectErrorStream(true);
            final Process process = builder.start();

            try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {

                String line;
                final StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                final String result = new String(sb);
                // check the version message
                if (result.contains("Magick")) {
                    LOG.info("ImageMagick detected : {}", StringUtils.abbreviate(result, 100));
                    return true;
                } else {
                    LOG.info("ImageMagick not found");
                }
            }
        } catch (final IOException e) {
            LOG.error("unable to check ImageMagick from System", e);
        }
        return false;
    }

    protected boolean isConfigured() {
        return isConfigured;
    }

    private enum IMOperations {

        THUMBNAIL("-thumbnail"),
        RESIZE("-resize");

        private final String value;

        private IMOperations(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
