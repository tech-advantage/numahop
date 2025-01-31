package fr.progilone.pgcn.service.storage;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TesseractServiceTest {

    private static final String TEST_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";
    private static final String TESSDATA_PATH = "src/test/resources/tessdata/";

    private static final String SRC_FILES_TO_JOIN = "src/test/resources/storage/test3pagesPdf.txt";

    private static final String PATH_TO_TESS_PROCESS = "C://Program Files (x86)/Tesseract-OCR/tesseract.exe";

    @Mock
    private AltoService altoService;

    private TesseractService service;

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(TEST_DIR));
    }

    @AfterAll
    public static void clean() {
        FileUtils.deleteQuietly(new File(TEST_DIR));
    }

    @BeforeEach
    public void setUp() {
        service = new TesseractService(altoService);
        service.initialize(PATH_TO_TESS_PROCESS);
        ReflectionTestUtils.setField(service, "tessDataPath", TESSDATA_PATH);
    }

    @Disabled
    @Test
    public void buildPdfTest() throws InterruptedException, ExecutionException {
        try {
            service.buildPdf(new File(SRC_FILES_TO_JOIN), TEST_DIR, "prefix", TEST_DIR + "/generated", "fra", new ArrayList<>(), false, "").get();
        } catch (final PgcnTechnicalException e) {
            // tesseract non configuré
        } catch (final InterruptedException | ExecutionException e) {
            // erreur du process
        }

        final File pdf = new File(TEST_DIR + "/generated.pdf");
        assertTrue(pdf.exists() && pdf.isFile()
                   && pdf.canRead());
    }

}
