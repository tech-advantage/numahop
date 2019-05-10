package fr.progilone.pgcn.service.storage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

@RunWith(MockitoJUnitRunner.class)
public class TesseractServiceTest {

    private static final String TEST_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";
    private static final String TESSDATA_PATH = "src/test/resources/tessdata/";
    
    private static final String SRC_FILES_TO_JOIN = "src/test/resources/storage/test3pagesPdf.txt";
    
    private static final String PATH_TO_TESS_PROCESS = "C://Program Files (x86)/Tesseract-OCR/tesseract.exe";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Mock
    private AltoService altoService;
    
    private TesseractService service;

    
    @BeforeClass
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(TEST_DIR));
    }

    @AfterClass
    public static void clean() {
        FileUtils.deleteQuietly(new File(TEST_DIR));
    }
    

    @Before
    public void setUp() {     
        service = new TesseractService(altoService);
        service.initialize(PATH_TO_TESS_PROCESS);
        ReflectionTestUtils.setField(service, "tessDataPath", TESSDATA_PATH);
        service.init();
    }
    
    @Ignore
    @Test
    public void buildPdfTest() throws InterruptedException, ExecutionException {
        try {
            service.buildPdf(new File(SRC_FILES_TO_JOIN), TEST_DIR, "prefix", TEST_DIR+"/generated", "fra", new ArrayList<>(), false, "").get();
        } catch (final PgcnTechnicalException e) {
            // tesseract non configuré
        } catch (InterruptedException | ExecutionException e) {
            // erreur du process
        }
        
        final File pdf = new File(TEST_DIR+"/generated.pdf");
        assertTrue(pdf.exists() && pdf.isFile() && pdf.canRead()); 
    }

    
}
