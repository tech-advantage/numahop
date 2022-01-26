package fr.progilone.pgcn.service.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

@Service
public class ExifToolService {
    
    
    private static final Logger LOG = LoggerFactory.getLogger(ExifToolService.class);

    public static final String META_DATAS_SEPARATOR = " # "; 
    
    /* Filtres sur les metadatas */
    /* Metadatas dont on a besoin */
    private static final String[] METAS_TO_CHECK = {"Filesize", "Format", "Colorspace",
                                                    "Compression", "Quality", "Resolution", 
                                                    "height", "width"};
    
    @Value("${exifTool.quot_char}")
    protected String quoteDelim;
       
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
    public Optional<Map<String, String>> extractMetadatas(final File img) throws PgcnTechnicalException {
        
        if (!this.isConfigured) {
            throw new PgcnTechnicalException("Le service de conversion Exif Tool n'est pas configuré, impossible de collecter les metadatas.");
        }
        if (img == null) {
            LOG.info("Can't get metadatas of null file");
            return Optional.empty();
        }
        Map<String, String> metas;
        final String args = quoteDelim + "$FileSize # $FileType # $ProfileDescription # $BitsPerComponent # " +
                "$XResolution # $YResolution # $DisplayXResolution # $DisplayYResolution # $DisplayXResolutionUnit # " +
                "$ImageHeight # $ImageWidth" + quoteDelim;
        
        final ProcessBuilder builder =
                                     new ProcessBuilder(exifProcessPath,
                                                        "-m",
                                                        "-p",
                                                        args,
                                                        img.getAbsolutePath());

        builder.redirectErrorStream(true);
        try {

            final Process process = builder.start();
            metas = collectMetadatas(process.getInputStream());
            if (process.waitFor() == 0) {
                LOG.info("[ExifTool] metadatas collected - {}", img.getName());
                return Optional.of(metas);
            } else {
                if (process.isAlive()) {
                    process.destroyForcibly().waitFor();
                }
                if (MapUtils.isNotEmpty(metas)) {
                    return Optional.of(metas);
                }
                if (process.isAlive()) {
                    LOG.info("[ExifTool] Problem when collecting metadatas : {}", process.isAlive() ? "processus still running..." : process.exitValue());
                    final List<String> errors = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (!errors.isEmpty()) {
                        LOG.error("[ExifTool] Error during metadatas collect : {}", errors);
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
    private Map<String, String> collectMetadatas(final InputStream is) throws IOException {
        
        final Map<String, String> metas = new HashMap<>();
        IOUtils.readLines(is, StandardCharsets.UTF_8)
               .forEach(ligne -> {
                   if (! StringUtils.containsIgnoreCase(ligne, "warning")) {
                       final String[] tab = StringUtils.split(ligne, "#", 11);
                       if (tab.length != 11) {
                           return;
                       }
                       final String format = StringUtils.trim(tab[1]);
                       final String colorSpace = StringUtils.trim(tab[2]);
                       
                       metas.put(StringUtils.trim(METAS_TO_CHECK[0]), StringUtils.trim(tab[0]));  // filesize
                       metas.put(StringUtils.trim(METAS_TO_CHECK[1]), format);  // format
                       if (StringUtils.containsIgnoreCase(colorSpace, "Adobe")) {
                           metas.put(StringUtils.trim(METAS_TO_CHECK[2]), "Adobe RGB");  // colorspace
                       } else {
                           metas.put(StringUtils.trim(METAS_TO_CHECK[2]), "sRGB");  // colorspace 
                       }
                       if (StringUtils.equalsIgnoreCase("JP2", format)) {
                           metas.put(StringUtils.trim(METAS_TO_CHECK[3]), "JPEG2000");  // type compression
                       }
                       final String depth = StringUtils.trim(tab[3]);
                       if (StringUtils.isNotBlank(depth)) {
                           metas.put(StringUtils.trim(METAS_TO_CHECK[4]), depth.substring(0, 1));  // quality => Bit Depth
                       } else {
                           metas.put(StringUtils.trim(METAS_TO_CHECK[4]), "non renseigné");  // quality => Bit Depth
                       }
                       
                       // Resolution
                       if(!StringUtils.trim(tab[4]).isEmpty() || !StringUtils.trim(tab[5]).isEmpty()){
                           metas.put(StringUtils.trim(METAS_TO_CHECK[5]), StringUtils.trim(tab[4]) + "x" + StringUtils.trim(tab[5]));  // resolution
                       
                       // Display Resolution / Display Resolution Unit
                       } else if((!StringUtils.trim(tab[6]).isEmpty() || !StringUtils.trim(tab[7]).isEmpty()) && !StringUtils.trim(tab[8]).isEmpty()){
                           metas.put(StringUtils.trim(METAS_TO_CHECK[5]), StringUtils.trim(getResolution(tab[6], tab[8])) + "x" + StringUtils.trim(getResolution(tab[7], tab[8])));
                       }
                       
                       metas.put(StringUtils.trim(METAS_TO_CHECK[6]), StringUtils.trim(tab[9]));  // height
                       metas.put(StringUtils.trim(METAS_TO_CHECK[7]), StringUtils.trim(tab[10]));  // width
                   }
               });
        return metas;
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

            try (final InputStream is = process.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader br = new BufferedReader(isr);) {

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
    private Map<String, Double> unitMapResolutionConversion(){
        Map<String, Double> conversion = new HashMap<>();
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
     * @param resolution métadonnée Display Resolution
     * @param resolutionUnit métadonnée Display Resolution Unit
     */
    private String getResolution(final String resolution,final String resolutionUnit){
        Double conversion = unitMapResolutionConversion().get(StringUtils.trim(resolutionUnit));
        if(conversion != null){
            return String.valueOf(conversion * 2.54 * Double.parseDouble(resolution));
        }
        return "";
    }
    

    protected boolean isConfigured() {
        return isConfigured;
    }

}
