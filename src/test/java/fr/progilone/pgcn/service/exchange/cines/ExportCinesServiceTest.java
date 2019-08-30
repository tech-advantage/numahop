package fr.progilone.pgcn.service.exchange.cines;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.ExportCinesException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.SftpConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.mapper.UIExportDataMapper;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsCinesReportService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.library.LibraryParameterService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

/**
 * Created by Sébastien on 27/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportCinesServiceTest {

    private static final String WORKING_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    @Mock
    private ExportMetsService exportMetsService;
    @Mock
    private ExportSipService exportSipService;
    @Mock
    private BinaryStorageManager bm;
    @Mock
    private DocUnitService docUnitService;
    @Mock
    private UIExportDataMapper uiExportDataMapper;
    @Mock
    private UIBibliographicRecordService uiBibliographicRecordService;
    @Mock
    private SftpConfigurationService sftpConfigurationService;
    @Mock
    private CinesReportService cinesReportService;
    @Mock
    private EsCinesReportService esCinesReportService;
    @Mock
    private SftpService sftpService;
    @Mock
    private FileStorageManager fm;
    @Mock
    private LibraryService libraryService;
    @Mock
    private LibraryParameterService libraryParameterService;
    @Mock
    private TransactionService transactionService;

    private ExportCinesService service;

    @BeforeClass
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(WORKING_DIR));
    }

    @AfterClass
    public static void clean() {
        FileUtils.deleteQuietly(new File(WORKING_DIR));
    }

    @Before
    public void setUp() {
        service = new ExportCinesService(exportMetsService, exportSipService, bm, docUnitService, uiExportDataMapper, 
                                         uiBibliographicRecordService, libraryService, sftpConfigurationService, cinesReportService,
                                         esCinesReportService, sftpService, fm, libraryParameterService, transactionService);
        ReflectionTestUtils.setField(service, "workingDir", WORKING_DIR);
    }

    @Test
    public void testExportDocUnit() throws IOException, JAXBException, PgcnTechnicalException, SAXException, NoSuchAlgorithmException, ExportCinesException {
        final DocUnit docUnit = getDocUnit();
        service.exportDocUnit(docUnit, false, null, false);

        assertTrue(Files.exists(Paths.get(WORKING_DIR, docUnit.getPgcnId(), "DEPOT")));
        assertTrue(Files.exists(Paths.get(WORKING_DIR, docUnit.getPgcnId(), "DEPOT", "DESC")));
        assertTrue(Files.exists(Paths.get(WORKING_DIR, docUnit.getPgcnId(), "DEPOT", "DESC", "mets.xml")));
        assertTrue(Files.exists(Paths.get(WORKING_DIR, docUnit.getPgcnId(), "sip.xml")));
    }

    private DocUnit getDocUnit() {
        final DocPropertyType dcCreator = new DocPropertyType();
        dcCreator.setIdentifier("creator");

        final DocProperty creator = new DocProperty();
        creator.setType(dcCreator);
        creator.setValue("Bret Easton Ellis");

        final DocPropertyType dcTitle = new DocPropertyType();
        dcTitle.setIdentifier("title");
        final DocProperty title = new DocProperty();
        title.setType(dcTitle);
        title.setValue("American Psycho");

        final BibliographicRecord record1 = new BibliographicRecord();
        record1.setIdentifier("REC-001");
        record1.addProperty(creator);
        record1.addProperty(title);

        final BibliographicRecord record2 = new BibliographicRecord();
        record2.setIdentifier("REC-002");
        record2.addProperty(creator);
        record2.addProperty(title);
        
        final Library lib = new Library();
        lib.setIdentifier("");

        final DocUnit docUnit = new DocUnit();
        docUnit.setLibrary(lib);
        docUnit.setIdentifier("DOC-UNIT-001");
        docUnit.setPgcnId("ID-DOC-UNIT-001");
        docUnit.setLabel("toto fait du vélo");
        docUnit.addRecord(record1);
        docUnit.addRecord(record2);
        return docUnit;
    }
}
