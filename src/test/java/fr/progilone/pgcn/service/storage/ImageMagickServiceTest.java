package fr.progilone.pgcn.service.storage;

import static fr.progilone.pgcn.service.storage.BinaryStorageManager.Metadatas;
import static org.junit.jupiter.api.Assertions.*;

import com.lowagie.text.pdf.PdfReader;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.util.ImageUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ImageMagickServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMagickServiceTest.class);

    private static final String SRC_FILE = "src/test/resources/storage/test.jpg";
    private static final String PATH_TO_IM_CONVERT = "C://Program Files/ImageMagick/convert.exe";
    private static final String PATH_TO_IM_IDENTIFY = "C://Program Files/ImageMagick/identify.exe";

    @InjectMocks
    private ImageMagickService service;

    @BeforeEach
    public void setUp() {
        service.initialize(PATH_TO_IM_CONVERT, PATH_TO_IM_IDENTIFY);
        ReflectionTestUtils.setField(service, "quoteDelim", "\"");
    }

    /**
     * Test thumbnail generation
     *
     * @throws IOException
     * @throws PgcnTechnicalException
     */
    @Disabled
    @Test
    public void generateThumbnail() throws IOException, PgcnTechnicalException {

        // final StoredFileFormat f = new StoredFileFormat();
        // f.setWidth(2835L);
        // f.setHeight(1964L);
        // when(storedFileFormatRepository
        // .getOneByLabel(StoredFileFormat.LABEL_PRINT))
        // .thenReturn(f);

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
            service.generateThumbnail(sourceFile,
                                      destTmpFile,
                                      formatThumb,
                                      conf,
                                      new Long[] {192L,
                                                  192L});
            assertTrue(destTmpFile.length() > 0L);
        } else {
            fail("Image Magick not configured");
        }
    }

    @Disabled
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

    @Disabled
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
    @Disabled
    @Test
    public void getMetadatas() throws IOException, PgcnTechnicalException {
        if (service.isConfigured()) {
            final File sourceFile = new File(SRC_FILE);
            if (!sourceFile.exists()) {
                fail("Unable to load " + SRC_FILE);
            }
            final Optional<Metadatas> metas = service.getMetadatasOfFile(sourceFile, false);
            assertTrue(metas.isPresent());
            assertTrue(StringUtils.equals(metas.get().getColorSpace(), "sRGB"));
            assertEquals("777835B", metas.get().getFilesize());
        } else {
            fail("Image Magick not configured");
        }
    }

    @Disabled
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
                    service.generateThumbnail(sourceFile,
                                              file,
                                              formatView,
                                              conf,
                                              new Long[] {100L,
                                                          100L});
                } catch (final PgcnTechnicalException e) {
                    fail();
                }
            });
            final long endIM = System.nanoTime();
            LOG.info("Time elapsed : {} s", TimeUnit.NANOSECONDS.toSeconds(endIM - startIM));

            final long startScalr = System.nanoTime();
            destIMFiles.stream().forEach(file -> ImageUtils.createThumbnail(sourceFile, file, (int) conf.getWidthByFormat(formatView), (int) conf.getHeightByFormat(formatView)));
            final long endScalr = System.nanoTime();
            LOG.info("Time elapsed : {} s", TimeUnit.NANOSECONDS.toSeconds(endScalr - startScalr));
            assertTrue(endIM - startIM > 0L);
        } else {
            fail("Image Magick not configured");
        }
    }

    @Test
    public void testAlphanumericSort() {
        final List<File> files = new ArrayList<>();

        final File file1 = new File("src/files/GB_000040_001_0200.jpg");
        files.add(file1);
        final File file2 = new File("src/files/GB_000040_001_1000.jpg");
        files.add(file2);
        final File file3 = new File("src/files/GB_000040_001_0020.jpg");
        files.add(file3);
        final File file4 = new File("src/files/GB_000040_001_1034.jpg");
        files.add(file4);
        final File file5 = new File("src/files/GB_000040_001_0001.jpg");
        files.add(file5);
        final File file6 = new File("src/files/GB_000040_001_0999.jpg");
        files.add(file6);
        final File file7 = new File("src/files/GB_000040_001_0010.jpg");
        files.add(file7);
        final File file8 = new File("src/files/GB_000040_001_0100.jpg");
        files.add(file8);

        final Pattern p = Pattern.compile("\\d+");
        files.sort((f1, f2) -> {
            final String name1 = f1.getName();
            final String name2 = f2.getName();
            Matcher m = p.matcher(name1);
            Integer number1 = null;
            if (!m.find()) {
                return name1.compareTo(name2);
            } else {
                Integer number2 = null;
                while (m.find()) {
                    number1 = Integer.parseInt(m.group());
                }
                m = p.matcher(name2);
                if (!m.find()) {
                    return name1.compareTo(name2);
                } else {
                    while (m.find()) {
                        number2 = Integer.parseInt(m.group());
                    }
                    int comparison = 0;
                    if (number2 != null) {
                        comparison = number1.compareTo(number2);
                    }
                    if (comparison != 0) {
                        return comparison;
                    } else {
                        return name1.compareTo(name2);
                    }
                }
            }
        });

        assertEquals(files.get(0), file5);
        assertEquals(files.get(1), file7);
        assertEquals(files.get(2), file3);
        assertEquals(files.get(3), file8);
        assertEquals(files.get(4), file1);
        assertEquals(files.get(5), file6);
        assertEquals(files.get(6), file2);
        assertEquals(files.get(7), file4);
    }
}
