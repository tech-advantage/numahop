package fr.progilone.pgcn.service.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.AutomaticCheckRuleDTO;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;
import fr.progilone.pgcn.repository.check.AutomaticCheckResultRepository;
import fr.progilone.pgcn.repository.check.AutomaticCheckTypeRepository;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckTypeMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.AutomaticCheckRuleMapper;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.ImageMagickService;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * Created by Jonathan on 10/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AutomaticCheckServiceTest {
    
    private static final String FACILE_TEST_URL = "https://facile.cines.fr/xml";
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

    private FacileCinesService facileService;
    private MetaDatasCheckService metaCheckService;
    private DigitalDocumentService digitalDocumentService;

    @Before
    public void setUp() {
        facileService = new FacileCinesService();
        ReflectionTestUtils.setField(facileService, "facileApiUrl", FACILE_TEST_URL);
        service = new AutomaticCheckService(checkTypeRepository, checkResultRepository, facileService, metaCheckService, digitalDocumentService, bm, websocketService);
    }
    
    @Test
    public void testFileNameAgainstFormat() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);
        final String format = "jpg";
        List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for(int i = 0; i< 5 ; i++) {
            files.add(new File("test" + i + ".jpg"));
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
    public void testFileNameAgainstFormatKO() {
        AutomaticCheckResult result = new AutomaticCheckResult();
        when(service.save(result)).thenReturn(result);
        final String format = "jp2";
        final List<String> fileNames = new ArrayList<>();
        final Collection<File> files = new ArrayList<>();
        for(int i = 0; i< 5 ; i++) {
            files.add(new File("test" + i + ".jpg"));
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
        assertTrue(dto!=null);
        assertEquals(dto.getType(), AutoCheckType.FILE_FORMAT);
    }
    
    @Test
    public void testCheckRuleMapper() {
        
        final AutomaticCheckRule ruleObj = buildAutoCheckRule();
        final AutomaticCheckRuleDTO dto = AutomaticCheckRuleMapper.INSTANCE.checkRuleToCheckRuleDTO(ruleObj);
        assertTrue(dto!=null && dto.getAutomaticCheckType()!=null);
        assertEquals(dto.getAutomaticCheckType().getType(), AutoCheckType.FILE_FORMAT);
        final AutomaticCheckRule obj2 = AutomaticCheckRuleMapper.INSTANCE.checkRuleDtoToCheckRule(dto);
        assertEquals(ruleObj, obj2);
    }

    
    
    /**
     * FIXME
     */
    @Test
    public void checkSequenceNumberOK() {
        
    }
    
    /**
     * FIXME
     */
    @Test
    public void checkTotalFileNumber() {
        
    }

    @Test
    public void testCheckFacile() {
        final DocUnit doc = buildDocUnit();
        final List<AutomaticCheckType> checkList = buildFacileCheckList();
        final File baseDirectory = new File(RESOURCE_FOLDER);
        
        final File file1 = new File(baseDirectory, PAGE_1);
        final File file2 = new File(baseDirectory, PAGE_2);
        when(bm.getFileForStoredFile(any(StoredFile.class), Matchers.anyString())).thenAnswer(invocation -> {
            final StoredFile sto = (StoredFile) invocation.getArguments()[0];
            switch(sto.getIdentifier()) {
                case PAGE_1: return file1;
                default: return file2;
            }
        });
        
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
