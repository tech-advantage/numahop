package fr.progilone.pgcn.service.storage;

import static fr.progilone.pgcn.service.storage.BinaryStorageManager.Metadatas;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExifToolService {

    private static final Logger LOG = LoggerFactory.getLogger(ExifToolService.class);

    private static final String META_DATAS_SEPARATOR = " #META_DATAS_SEPARATOR# ";
    private static final String META_DATAS_SEPARATOR_PATTERN_QUOTE = Pattern.quote(META_DATAS_SEPARATOR.trim()); // Le .trim() est important : sans
                                                                                                                 // lui on "perd" les valeurs vides si
                                                                                                                 // elles sont en fin de chaîne
    private static final String META_TAGS_SEPARATOR = " #META_TAGS_SEPARATOR# ";
    private static final String META_TAGS_SEPARATOR_PATTERN_QUOTE = Pattern.quote(META_TAGS_SEPARATOR.trim());

    @Value("${exifTool.quot_char}")
    protected String quoteDelim;

    private static final String DEFAULT_ARGS = "$FileSize" + META_DATAS_SEPARATOR
                                               + "$FileType"
                                               + META_DATAS_SEPARATOR
                                               + "$ProfileDescription"
                                               + META_DATAS_SEPARATOR
                                               + "$BitsPerComponent"
                                               + META_DATAS_SEPARATOR
                                               + "$XResolution"
                                               + META_DATAS_SEPARATOR
                                               + "$YResolution"
                                               + META_DATAS_SEPARATOR
                                               + "$DisplayXResolution"
                                               + META_DATAS_SEPARATOR
                                               + "$DisplayYResolution"
                                               + META_DATAS_SEPARATOR
                                               + "$DisplayXResolutionUnit"
                                               + META_DATAS_SEPARATOR
                                               + "$ImageHeight"
                                               + META_DATAS_SEPARATOR
                                               + "$ImageWidth";

    protected String exifProcessPath;

    private boolean isConfigured;

    public void initialize(final String exifProcessPath) {
        this.exifProcessPath = exifProcessPath;
        this.isConfigured = getConfigurationState();
    }

    /**
     * Collecte les metadatas du fichier image.
     * [Utilisé pour JPEG 2000 == JP2]
     *
     * @param img
     * @return
     * @throws PgcnTechnicalException
     */
    public Optional<Metadatas> extractMetadatas(final File img, final List<String> tags) throws PgcnTechnicalException {

        if (!this.isConfigured) {
            throw new PgcnTechnicalException("Le service de conversion Exif Tool n'est pas configuré, impossible de collecter les metadatas.");
        }
        if (img == null) {
            LOG.info("Can't get metadatas of null file");
            return Optional.empty();
        }
        final StringBuilder args = new StringBuilder();
        args.append(quoteDelim);
        args.append(DEFAULT_ARGS);
        if (CollectionUtils.isNotEmpty(tags)) {
            args.append(META_DATAS_SEPARATOR);
            args.append(StringUtils.join(tags.stream().map(t -> "$" + t).iterator(), META_DATAS_SEPARATOR));
        }
        args.append(quoteDelim);

        final ProcessBuilder builder = new ProcessBuilder(exifProcessPath,
                                                          "-m",
                                                          "-sep",
                                                          quoteDelim + META_TAGS_SEPARATOR
                                                                  + quoteDelim,
                                                          "-p",
                                                          args.toString(),
                                                          img.getAbsolutePath());

        builder.redirectErrorStream(true);
        try {

            final Process process = builder.start();
            if (process.waitFor() == 0) {
                final Metadatas metas = collectMetadatas(process.getInputStream(), tags);
                LOG.info("[ExifTool] metadatas collected - {}", img.getName());
                return Optional.of(metas);
            } else {
                if (process.isAlive()) {
                    process.destroyForcibly().waitFor();
                }
                if (process.isAlive()) {
                    LOG.info("[ExifTool] Problem when collecting metadatas : {}",
                             process.isAlive() ? "processus still running..."
                                               : process.exitValue());
                    final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (!errors.isEmpty()) {
                        LOG.error("[ExifTool] Error during metadatas collect : {}", errors);
                    }
                }
            }
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
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
    private Metadatas collectMetadatas(final InputStream is, final List<String> tags) throws IOException {

        final Metadatas.Builder metasBuilder = new Metadatas.Builder();
        IOUtils.readLines(is, StandardCharsets.UTF_8).forEach(ligne -> {
            if (StringUtils.isNotBlank(ligne)) {
                final String[] tab = ligne.split(META_DATAS_SEPARATOR_PATTERN_QUOTE);
                if (tab.length != 11 + CollectionUtils.size(tags)) {
                    return;
                }
                final String format = StringUtils.trim(tab[1]);
                final String colorSpace = StringUtils.trim(tab[2]);

                metasBuilder.setFilesize(StringUtils.trim(tab[0]));
                metasBuilder.setFormat(format);
                if (StringUtils.containsIgnoreCase(colorSpace, "Adobe")) {
                    metasBuilder.setColorSpace("Adobe RGB");
                } else {
                    metasBuilder.setColorSpace("sRGB");
                }
                if (StringUtils.equalsIgnoreCase("JP2", format)) {
                    metasBuilder.setCompression("JPEG2000");
                }
                final String depth = StringUtils.trim(tab[3]);
                if (StringUtils.isNotBlank(depth)) {
                    metasBuilder.setQuality(depth.substring(0, 1));
                } else {
                    metasBuilder.setQuality("non renseigné");
                }

                // Resolution
                if (!StringUtils.trim(tab[4]).isEmpty() || !StringUtils.trim(tab[5]).isEmpty()) {
                    metasBuilder.setResolution(StringUtils.trim(tab[4]) + "x"
                                               + StringUtils.trim(tab[5]));

                    // Display Resolution / Display Resolution Unit
                } else if ((!StringUtils.trim(tab[6]).isEmpty() || !StringUtils.trim(tab[7]).isEmpty()) && !StringUtils.trim(tab[8]).isEmpty()) {
                    metasBuilder.setResolution(getResolution(tab[6], tab[8]) + "x"
                                               + getResolution(tab[7], tab[8]));
                }

                metasBuilder.setHeight(StringUtils.trim(tab[9]));
                metasBuilder.setWidth(StringUtils.trim(tab[10]));

                for (int i = 0; i < CollectionUtils.size(tags); i++) {
                    final String value = tab[11 + i];
                    if (StringUtils.isNotBlank(value)) {
                        final String tagKey = tags.get(i);
                        for (final String tagValue : value.split(META_TAGS_SEPARATOR_PATTERN_QUOTE)) {
                            metasBuilder.addTag(tagKey, StringUtils.trim(tagValue));
                        }
                    }
                }
            }
        });
        return metasBuilder.build();
    }

    /**
     * Copie les metadonnees d'un fichier source sur un fichier destination déjà existant
     *
     * @param source
     * @param destination
     * @throws PgcnTechnicalException
     */
    public void copyAllMetadatas(final File source, final File destination) throws PgcnTechnicalException {
        final ProcessBuilder builder = new ProcessBuilder(exifProcessPath,
                                                          "-tagsFromFile",
                                                          source.getAbsolutePath(),
                                                          "-all:all",
                                                          "-overwrite_original",
                                                          destination.getAbsolutePath());
        try {
            final Process process = builder.start();
            if (process.waitFor() == 0) {
                LOG.info("[ExifTool] metadatas written to {}", destination.getName());
            } else {
                if (process.isAlive()) {
                    process.destroyForcibly().waitFor();
                }
                if (process.isAlive()) {
                    LOG.info("[ExifTool] Problem when writing metadatas : {}",
                             process.isAlive() ? "processus still running..."
                                               : process.exitValue());
                    final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (!errors.isEmpty()) {
                        LOG.error("[ExifTool] Error during metadatas writing : {}", errors);
                    }
                }
            }
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Vérification de la bonne configuration
     */
    @SuppressWarnings("findsecbugs:COMMAND_INJECTION")
    protected boolean getConfigurationState() {
        try {
            final ProcessBuilder builder = new ProcessBuilder(exifProcessPath, "-v", "-ver");
            builder.redirectErrorStream(true);
            final Process process = builder.start();

            try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr);) {

                String line;
                final StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                final String result = new String(sb);
                // check the version message
                if (result.contains("ExifTool")) {
                    LOG.info("ExifTool detected : {}", StringUtils.abbreviate(result, 100));
                    return true;
                } else {
                    LOG.info("ExifTool not found");
                }
            }
        } catch (final IOException e) {
            LOG.error("unable to check ExifTool from System: {}", e.getMessage());
            LOG.trace(e.getMessage(), e);
        }
        return false;
    }

    /**
     * tableau de conversion pour la métadonnée Display Resolution en pouce
     * L'unité est présente dans la métadonnée Display X Resolution Unit
     */
    private Map<String, Double> unitMapResolutionConversion() {
        final Map<String, Double> conversion = new HashMap<>();
        conversion.put("km", 0.00001);
        conversion.put("100 m", 0.0001);
        conversion.put("10 m", 0.001);
        conversion.put("m", 0.01);
        conversion.put("10 cm", 0.1);
        conversion.put("cm", 1.0);
        conversion.put("mm", 10.0);
        conversion.put("0.1 mm", 100.0);
        conversion.put("0.01 mm", 1000.0);
        conversion.put("um", 10000.0);
        return conversion;
    }

    /**
     * Calcul de la resolution
     * 2.54 conversion pouce / cm
     *
     * @param resolution
     *            métadonnée Display Resolution
     * @param resolutionUnit
     *            métadonnée Display Resolution Unit
     */
    private String getResolution(final String resolution, final String resolutionUnit) {
        final Double conversion = unitMapResolutionConversion().get(StringUtils.trim(resolutionUnit));
        if (conversion != null) {
            return String.valueOf(conversion * 2.54
                                  * Double.parseDouble(StringUtils.trim(resolution)));
        }
        return "";
    }

    protected boolean isConfigured() {
        return isConfigured;
    }

}
