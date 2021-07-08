package fr.progilone.pgcn.service.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.ImageUtils;

@RunWith(MockitoJUnitRunner.class)
public class ImageMagickServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMagickServiceTest.class);

    private static final String SRC_FILE = "src/test/resources/storage/test.jpg";
    private static final String PATH_TO_IM_CONVERT = "C://Program Files/ImageMagick/convert.exe";
    private static final String PATH_TO_IM_IDENTIFY = "C://Program Files/ImageMagick/identify.exe";


    @InjectMocks
    private ImageMagickService service;


    @Before
    public void setUp() {
        service.initialize(PATH_TO_IM_CONVERT, PATH_TO_IM_IDENTIFY);
    }

    /**
     * Test thumbnail generation
     * 
     * @throws IOException
     * @throws PgcnTechnicalException
     */
    @Ignore
    @Test
    public void generateThumbnail() throws IOException, PgcnTechnicalException {

//        final StoredFileFormat f = new StoredFileFormat();
//        f.setWidth(2835L);
//        f.setHeight(1964L);
//        when(storedFileFormatRepository
//             .getOneByLabel(StoredFileFormat.LABEL_PRINT))
//                .thenReturn(f);

        if (service.isConfigured()) {
            
            final File sourceFile = new File(SRC_FILE);
            if (sourceFile == null || !sourceFile.exists()) {
                fail("Unable to load " + SRC_FILE);
            }
            final File destTmpFile = File.createTempFile("create_", ".tmp");
            destTmpFile.deleteOnExit();
            assertTrue(destTmpFile.length() == 0L);

         // Define format
            final ViewsFormatConfiguration.FileFormat formatThumb = ViewsFormatConfiguration.FileFormat.THUMB; 
            final ViewsFormatConfiguration conf = new ViewsFormatConfiguration();
            conf.setThumbHeight(100L);
            conf.setThumbWidth(100L);
            service.generateThumbnail(sourceFile, destTmpFile, formatThumb, conf, new Long[] {192L, 192L});
            assertTrue(destTmpFile.length() > 0L);
        } else {
            fail("Image Magick not configured");
        }
    }

    @Ignore
    @Test
    public void getImagesFromPdf() {

        if (service.isConfigured()) {

            final String PDF_FILE = "C://Temp/manu/test.pdf";
            final File sourceFile = new File(PDF_FILE);
            if (sourceFile == null || !sourceFile.exists()) {
                fail("Unable to load " + PDF_FILE);
            }

            final List<File> extracted = service.extractImgFromPdf(sourceFile, "C://Temp/manu/*.pdf");
            assertTrue(!extracted.isEmpty());
        }
    }

    @Ignore
    @Test
    public void countPdfNumberPages() {

        final String PDF_FILE = "C://Temp/manu/test.pdf";
        final File pdfFile = new File(PDF_FILE);
        if (pdfFile == null || !pdfFile.exists()) {
            fail("Unable to load " + PDF_FILE);
        }
        try {

            final PdfReader reader = new PdfReader("C://Temp/manu/test.pdf");
            assertTrue(reader.getNumberOfPages() > 0);
        } catch (final Exception e) {
            // pffffff...
        }

    }

    /**
     * Test IM getting metadatas
     * 
     * @throws IOException
     * @throws PgcnTechnicalException
     */
    @Ignore
    @Test
    public void getMetadatas() throws IOException, PgcnTechnicalException {
        if (service.isConfigured()) {
            final File sourceFile = new File(SRC_FILE);
            if (sourceFile == null || !sourceFile.exists()) {
                fail("Unable to load " + SRC_FILE);
            }
            final Optional<Map<String, String>> metas = service.getMetadatasOfFile(sourceFile, false);
            assertTrue(metas.isPresent());
            assertTrue(StringUtils.equals(metas.get().get("Colorspace"), "sRGB"));
            assertEquals(metas.get().get("Filesize"), "777835B");
        } else {
            fail("Image Magick not configured");
        }
    }

    @Ignore
    @Test
    public void compareIMtoImgScalr() throws IOException {
        if (service.isConfigured()) {
            final File sourceFile = new File(SRC_FILE);
            if (sourceFile == null || !sourceFile.exists()) {
                fail("Unable to load " + SRC_FILE);
            }
            final List<File> destIMFiles = new ArrayList<>();
            final List<File> destImScalrFiles = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                final File destTmpFileIM = File.createTempFile("create_", ".tmp");
                destIMFiles.add(destTmpFileIM);
                destTmpFileIM.deleteOnExit();
                final File destTmpFileImgScalr = File.createTempFile("create_", ".tmp");
                destImScalrFiles.add(destTmpFileImgScalr);
                destTmpFileImgScalr.deleteOnExit();
            }
            // Define format
            final ViewsFormatConfiguration.FileFormat formatView = ViewsFormatConfiguration.FileFormat.VIEW; 
            final ViewsFormatConfiguration conf = new ViewsFormatConfiguration();
            conf.setViewHeight(100L);
            conf.setViewWidth(100L);

            // bench
            final long startIM = System.nanoTime();
            destIMFiles.stream().forEach(file -> {
                try {
                    service.generateThumbnail(sourceFile, file, formatView, conf, new Long[] {100L, 100L});
                } catch (final PgcnTechnicalException e) {
                    fail();
                }
            });
            final long endIM = System.nanoTime();
            LOG.info("Time elapsed : {} s", TimeUnit.NANOSECONDS.toSeconds(endIM - startIM));

            final long startScalr = System.nanoTime();
            destIMFiles.stream().forEach(
                                         file -> ImageUtils.createThumbnail(
                                                                            sourceFile,
                                                                            file,
                                                                            (int) conf.getWidthByFormat(formatView),
                                                                            (int) conf.getHeightByFormat(formatView)));
            final long endScalr = System.nanoTime();
            LOG.info("Time elapsed : {} s", TimeUnit.NANOSECONDS.toSeconds(endScalr - startScalr));
            assertTrue(endIM - startIM > 0L);
        } else {
            fail("Image Magick not configured");
        }
    }
}
