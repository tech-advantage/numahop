package fr.progilone.pgcn.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

/**
 * Service de génération de rapports JasperReports
 */
@Service
public class JasperReportsService {

    private static final Logger LOG = LoggerFactory.getLogger(JasperReportsService.class);
    
    public static final String ENCODING_UTF8 = "utf-8";

    /**
     * Rapports (sans l'extension jasper)
     */
    public static final String REPORT_TEST = "test";
    
    // Bordereau de livraison
    public static final String REPORT_DELIV_SLIP = "deliverySlip";
    // Bordereau de controle
    public static final String REPORT_CHECK_SLIP = "checkSlip";
    // Bordereau d'envoi du train (de constat d'etat) 
    public static final String REPORT_CONDREPORT_SLIP = "condReportSlip";

    /**
     * Types d'export
     */
    public enum ExportType {
        PDF,
        XLS
    }

    /**
     * Répertoire contenant les images inclues dans les rapports
     */
    @Value("${report.imagePath}")
    private String imagePath;
    
    /**
     * Repertoire contenant les logos des bibliotheques.
     */
    @Value("${uploadPath.library}")
    private String libraryDir;

    /**
     * Cette méthode génère le rapport <i>report</i> dans le format <i>exportType</i>
     * à partir d'une collection de beans, et d'une Map de paramètres.
     * Le rapport est écrit dans le flux <i>outputStream</i>
     * <p>
     * Pour ce faire, les API jasperreports sont utilisées.
     * On part du rapport compilé (<i>.jasper</i>), auquel sont passés une source de données basées sur la collection de beans
     * et les divers paramètres, pour obtenir un fichier <i>.jasperprint</i>
     * Ce <i>.jasperprint</i> sert ensuite à générer le rapport dans son format final (PDF, XLS, ...),
     * tel qu'il sera écrit dans le flux de sortie.
     * <p>
     * ((jrxml -> jasper) + datasource + parameters) -> jasperprint -> pdf, xls, ...
     *
     * @param report
     * @param exportType
     * @param parameters
     * @param beanCollection
     * @param outputStream
     * @throws PgcnException
     */
    public void exportReportToStream(final String report,
                                     final ExportType exportType,
                                     final Map<String, Object> parameters,
                                     final Collection<?> beanCollection,
                                     final OutputStream outputStream,
                                     final String libraryId) throws PgcnException {

        try (InputStream reportStream = this.getClass().getResourceAsStream("/reporting/" + report + ".jasper")) {
            // Rapport
            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            // Emplacement des images des rapports
            parameters.put("P_IMAGE_PATH", libraryDir + "/" + libraryId);
            parameters.put("P_REPORT_PATH", "reporting");

            // pas de pagination avec excel
            if (exportType == ExportType.XLS) {
                parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            }

            // DataSouce
            final JRDataSource source = new JRBeanCollectionDataSource(beanCollection);

            // JasperPrint
            final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

            // Export report
            switch (exportType) {
                case PDF:
                    exportReportToPdfStream(jasperPrint, outputStream);
                    break;
                case XLS:
                    exportReportToXlsStream(jasperPrint, outputStream);
                    break;
            }

        } catch (final JRException | IOException e) {
            LOG.error(e.getMessage(), e);

            final PgcnError error = new PgcnError.Builder().setCode(PgcnErrorCode.REPORT_GENERATION)
                                                     .setMessage("Une erreur est survenue lors de la génération du rapport "
                                                                 + report
                                                                 + ": "
                                                                 + e.getMessage())
                                                     .build();
            throw new PgcnException(error);
        }
    }

    /**
     * Génération du rapport au format XLS
     *
     * @param jasperPrint
     * @param outputStream
     * @throws JRException
     */
    private void exportReportToXlsStream(final JasperPrint jasperPrint, final OutputStream outputStream) throws JRException {
        final JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        final SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setWhitePageBackground(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }

    /**
     * Génération du rapport au format PDF
     *
     * @param jasperPrint
     * @param outputStream
     * @throws JRException
     */
    private void exportReportToPdfStream(final JasperPrint jasperPrint, final OutputStream outputStream) throws JRException {
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

}
