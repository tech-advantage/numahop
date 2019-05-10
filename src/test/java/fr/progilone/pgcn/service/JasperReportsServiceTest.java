package fr.progilone.pgcn.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test de génération des rapports
 * <p>
 * Created by Sebastien on 21/09/2016.
 */
public class JasperReportsServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(JasperReportsServiceTest.class);

    private static final String IMAGE_PATH = "images";

    private JasperReportsService service;

    @Before
    public void setUp() {
        service = new JasperReportsService();
        ReflectionTestUtils.setField(service, "imagePath", IMAGE_PATH);
    }

    @Ignore
    @Test
    public void generateTestReport() {
        final File exportFile = getExportFile(JasperReportsService.REPORT_TEST, "pdf");
        LOG.debug("Génération du rapport {}", exportFile.getAbsolutePath());

        try (final OutputStream out = new FileOutputStream(exportFile)) {
            service.exportReportToStream(JasperReportsService.REPORT_TEST,
                                         JasperReportsService.ExportType.PDF,
                                         new HashMap<>(),
                                         Arrays.asList(new TestObject("voiture"),
                                                       new TestObject("cochon"),
                                                       new TestObject("ski"),
                                                       new TestObject("tartiflette"),
                                                       new TestObject("dvd")),
                                         out,
                                         "fakeLibraryId");
        } catch (final IOException e) {
            Assert.fail("generateBudgetReport s'est terminé sur l'erreur: " + e.getMessage());
        }
    }

    private File getExportFile(final String prefix, final String extension) {
        final String fileName = prefix + "_" + System.currentTimeMillis() + "." + extension;
        return FileUtils.getFile(FileUtils.getTempDirectory(), fileName);
    }

    public static final class TestObject {

        private String label;

        public TestObject(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }
    }
}
