package fr.progilone.pgcn;

import java.io.File;
import java.util.stream.Stream;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compilation des rapports jrxml
 *
 * Created by Sebastien on 21/09/2016.
 */
public class JasperCompile {

    private static final Logger LOG = LoggerFactory.getLogger(JasperCompile.class);

    private static final String REPORT_SRC_PATH = "src/main/resources/reporting/sources";
    private static final String REPORT_JASPER_PATH = "src/main/resources/reporting";

    /**
     * Cette méthode génère les fichiers jasper compilés dans le répertoire REPORT_JASPER_PATH
     * à partir des sources jrxml présentes dans le répertoire REPORT_SRC_PATH
     */
    @Disabled
    @Test
    public void compileAllReports() {
        LOG.info("Compilation des rapports à partir du répertoire {}", REPORT_SRC_PATH);
        final File[] reportSources = getReportSources(REPORT_SRC_PATH);

        Stream.of(reportSources).parallel().forEach(jrxml -> {
            try {
                final String targetName = StringUtils.replacePattern(jrxml.getName(), "\\.jrxml$", ".jasper");
                final File targetPath = new File(REPORT_JASPER_PATH, targetName);

                LOG.info("Compilation du rapport {} vers le fichier {}", jrxml.getName(), targetPath.getAbsolutePath());
                JasperCompileManager.compileReportToFile(jrxml.getAbsolutePath(), targetPath.getAbsolutePath());
            } catch (final Throwable e) {
                LOG.error("La compilation du rapport {} a échoué sur l'erreur {}", jrxml.getAbsolutePath(), e.getMessage());
            }
        });
        LOG.info("La compilation de {} rapport(s) s'est terminée avec succès", reportSources.length);
    }

    private File[] getReportSources(final String path) {
        final File f = new File(path);
        final File[] reports = f.listFiles(f1 -> f1.getName().endsWith(".jrxml"));
        return reports != null ? reports
                               : new File[] {};
    }
}
