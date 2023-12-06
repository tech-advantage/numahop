package fr.progilone.pgcn.service.check;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;
import fr.progilone.pgcn.domain.dto.check.SplitFilename;
import fr.progilone.pgcn.domain.dto.checkconfiguration.AutomaticCheckRuleDTO;
import fr.progilone.pgcn.domain.jaxb.facile.ValidatorType;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;
import fr.progilone.pgcn.repository.check.AutomaticCheckResultRepository;
import fr.progilone.pgcn.repository.check.AutomaticCheckTypeRepository;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataValuesRepository;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckTypeMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.AutomaticCheckRuleMapper;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.ImageMagickService;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Jonathan on 10/02/2017.
 */
@ExtendWith(MockitoExtension.class)
public class AutomaticCheckServiceTest {

    private static final String RESOURCE_FOLDER = "src/test/resources/facile";
    private static final String PAGE_1 = "BSG_DELTA_0001.png";
    private static final String PAGE_2 = "BSG_DELTA_0002.png";

    private AutomaticCheckService service;
    @Mock
    private AutomaticCheckTypeRepository checkTypeRepository;
    @Mock
    private AutomaticCheckResultRepository checkResultRepository;
    @Mock
    private BinaryStorageManager bm;
    @Mock
    private ImageMagickService imageMagickService;
    @Mock
    private WebsocketService websocketService;
    @Mock
    private ImageMetadataValuesRepository imageMetadataValuesRepository;
    @Mock
    private FacileCinesService facileService;
    @Mock
    private MetaDatasCheckService metaCheckService;
    @Mock
    private DigitalDocumentService digitalDocumentService;

    @BeforeEach
    public void setUp() {
        service = new AutomaticCheckService(checkTypeRepository,
                                            checkResultRepository,
                                            facileService,
                                            metaCheckService,
                                            digitalDocumentService,
                                            bm,
                                            websocketService,
                                            imageMetadataValuesRepository);
    }

    @Test
    public void testFileNameAgainstFormat() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);
        final String format = "jpg";
        List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            files.add(new File("test" + i
                               + ".jpg"));
        }
        files.add(new File("incorrect.ico"));
        files.add(new File("incorrectBis.png"));

        result = service.checkFileNamesAgainstFormat(result, files, format, fileNames, buildAutoCheckRule());
        assertEquals(AutoCheckResult.OK, result.getResult());
        assertEquals(5, fileNames.size());

        fileNames = service.findNonMaster(files, format);
        assertEquals(2, fileNames.size());

        fileNames = service.findMastersOnly(files, format);
        assertEquals(5, fileNames.size());
    }

    @Test
    public void testFileNameAgainstFormatOTHER() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);
        final String format = "jpg";
        List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();

        result = service.checkFileNamesAgainstFormat(result, files, format, fileNames, buildAutoCheckRule());
        assertEquals(AutoCheckResult.KO, result.getResult());
        assertEquals(0, fileNames.size());

        fileNames = service.findNonMaster(files, format);
        assertEquals(0, fileNames.size());

        fileNames = service.findMastersOnly(files, format);
        assertEquals(0, fileNames.size());
    }

    @Test
    public void testFileNameAgainstFormatKO() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);
        final String format = "jp2";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            files.add(new File("test" + i
                               + ".jpg"));
        }
        files.add(new File("incorrect.ico"));
        files.add(new File("incorrectBis.png"));

        result = service.checkFileNamesAgainstFormat(result, files, format, fileNames, buildAutoCheckRule());
        assertEquals(AutoCheckResult.KO, result.getResult());
    }

    @Test
    public void testCheckTypeMapper() {
        final AutomaticCheckType ct = buildAutoCheckType();
        final AutomaticCheckTypeDTO dto = AutomaticCheckTypeMapper.INSTANCE.objToDto(ct);
        assertTrue(dto != null);
        assertEquals(dto.getType(), AutoCheckType.FILE_FORMAT);
    }

    @Test
    public void testCheckRuleMapper() {

        final AutomaticCheckRule ruleObj = buildAutoCheckRule();
        final AutomaticCheckRuleDTO dto = AutomaticCheckRuleMapper.INSTANCE.checkRuleToCheckRuleDTO(ruleObj);
        assertTrue(dto != null && dto.getAutomaticCheckType() != null);
        assertEquals(dto.getAutomaticCheckType().getType(), AutoCheckType.FILE_FORMAT);
        final AutomaticCheckRule obj2 = AutomaticCheckRuleMapper.INSTANCE.checkRuleDtoToCheckRule(dto);
        assertEquals(ruleObj, obj2);
    }

    /**
     *
     */
    @Test
    public void checkSequenceNumberOK() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);

        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

        final String format = "jpg";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            files.add(new File("test_00" + i
                               + ".jpg"));
        }

        fileNames.addAll(service.findMastersOnly(files, format));

        result = service.checkSequenceNumber(result, fileNames, splitNames, buildAutoCheckRule(), false, "_", false, false, "test");
        assertEquals(AutoCheckResult.OK, result.getResult());
        assertEquals(5, fileNames.size());
    }

    /**
     *
     */
    @Test
    public void checkSequenceNumberKO() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);

        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

        final String format = "jpg";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        files.add(new File("test_001.jpg"));
        files.add(new File("test_002.jpg"));
        files.add(new File("test_004.jpg"));
        files.add(new File("test_005.jpg"));

        fileNames.addAll(service.findMastersOnly(files, format));

        result = service.checkSequenceNumber(result, fileNames, splitNames, buildAutoCheckRule(), false, "_", false, false, "test");
        assertEquals(AutoCheckResult.KO, result.getResult());
        assertEquals(4, fileNames.size());
    }

    /**
     *
     */
    @Test
    public void checkSequenceNumberWithPieceOK() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);

        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

        final String format = "jpg";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            files.add(new File("test-1_00" + i
                               + ".jpg"));
        }
        for (int i = 0; i < 2; i++) {
            files.add(new File("test-2_00" + i
                               + ".jpg"));
        }
        for (int i = 0; i < 1; i++) {
            files.add(new File("test-3_00" + i
                               + ".jpg"));
        }

        fileNames.addAll(service.findMastersOnly(files, format));

        result = service.checkSequenceNumber(result, fileNames, splitNames, buildAutoCheckRule(), false, "_", false, false, "test");
        assertEquals(AutoCheckResult.OK, result.getResult());
        assertEquals(6, fileNames.size());
    }

    /**
     *
     */
    @Test
    public void checkSequenceNumberWithPieceKO() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);

        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

        final String format = "jpg";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            files.add(new File("test-1_00" + i
                               + ".jpg"));
        }
        for (int i = 0; i < 2; i++) {
            files.add(new File("test-2_00" + i
                               + ".jpg"));
        }
        files.add(new File("test-3_001.jpg"));
        files.add(new File("test-3_003.jpg"));

        fileNames.addAll(service.findMastersOnly(files, format));

        result = service.checkSequenceNumber(result, fileNames, splitNames, buildAutoCheckRule(), false, "_", false, false, "test");
        assertEquals(AutoCheckResult.KO, result.getResult());
        assertEquals(7, fileNames.size());
    }

    @Test
    public void testCheckFacile() {
        final DocUnit doc = buildDocUnit();
        final List<AutomaticCheckType> checkList = buildFacileCheckList();
        final File baseDirectory = new File(RESOURCE_FOLDER);

        final File file1 = new File(baseDirectory, PAGE_1);
        final File file2 = new File(baseDirectory, PAGE_2);
        when(bm.getFileForStoredFile(any(StoredFile.class), any())).thenAnswer(invocation -> {
            final StoredFile sto = (StoredFile) invocation.getArguments()[0];
            switch (sto.getIdentifier()) {
                case PAGE_1:
                    return file1;
                default:
                    return file2;
            }
        });
        final ValidatorType vt = new ValidatorType();
        vt.setValid(true);
        when(facileService.checkFileAgainstFacile(file1)).thenReturn(vt);

        final List<AutomaticCheckResult> results = service.check(checkList, doc, "fake_lib");
        assertEquals(1, results.size());
        results.forEach(result -> {
            assertEquals(AutoCheckResult.OK, result.getResult());
        });

    }

    private AutomaticCheckType buildAutoCheckType() {
        final AutomaticCheckType ct = new AutomaticCheckType();
        ct.setIdentifier("idct001");
        ct.setActive(true);
        ct.setLabel("test label 001");
        ct.setType(AutoCheckType.FILE_FORMAT);
        return ct;
    }

    private AutomaticCheckRule buildAutoCheckRule() {
        final AutomaticCheckRule cr = new AutomaticCheckRule();
        cr.setAutomaticCheckType(buildAutoCheckType());
        cr.setActive(true);
        cr.setBlocking(true);
        cr.setIdentifier("idcr001");
        return cr;
    }

    private List<AutomaticCheckType> buildFacileCheckList() {
        final List<AutomaticCheckType> type = new ArrayList<>();
        final AutomaticCheckType facile = new AutomaticCheckType();
        facile.setActive(true);
        facile.setIdentifier("type_id");
        facile.setType(AutoCheckType.FACILE);
        type.add(facile);
        return type;
    }

    private DocUnit buildDocUnit() {
        final DocUnit doc = new DocUnit();
        doc.setIdentifier("doc_id");
        final DigitalDocument digital = new DigitalDocument();
        digital.setDocUnit(doc);
        digital.setIdentifier("digital_id");

        final DocPage page = new DocPage();
        page.setDigitalDocument(digital);
        page.setNumber(1);
        final StoredFile stoFile1 = new StoredFile();
        stoFile1.setIdentifier(PAGE_1);
        stoFile1.setFilename(PAGE_1);
        stoFile1.setType(StoredFileType.MASTER);
        stoFile1.setPage(page);

        final DocPage page2 = new DocPage();
        page2.setDigitalDocument(digital);
        page.setNumber(2);
        final StoredFile stoFile2 = new StoredFile();
        stoFile2.setIdentifier(PAGE_2);
        stoFile2.setFilename(PAGE_2);
        stoFile2.setType(StoredFileType.MASTER);
        stoFile2.setPage(page2);

        page2.addFile(stoFile2);
        page.addFile(stoFile1);
        digital.addPage(page);
        digital.addPage(page2);
        doc.addDigitalDocument(digital);
        return doc;
    }
}
