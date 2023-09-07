package fr.progilone.pgcn.service.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.progilone.pgcn.repository.document.PhysicalDocumentRepository;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.delivery.mapper.PrefixedDocumentsMapper;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocPageService;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.SlipService;
import fr.progilone.pgcn.service.document.ui.UIDigitalDocumentService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.ImageMagickService;
import fr.progilone.pgcn.service.storage.TesseractService;
import fr.progilone.pgcn.service.util.DeliveryProgressService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeliveryAsyncServiceTest {

    private static final String RESOURCE_FOLDER = "src/test/resources/delivery";

    @Mock
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Mock
    private DeliveryService deliveryService;
    @Mock
    private EsDeliveryService esDeliveryService;
    @Mock
    private PhysicalDocumentRepository physicalDocumentRepository;
    @Mock
    private DocPageService docPageService;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private AutomaticCheckService autoCheckService;
    @Mock
    private BinaryStorageManager bm;
    @Mock
    private TransactionService transactionService;
    @Mock
    private PrefixedDocumentsMapper mapper;
    @Mock
    private MetaDatasCheckService metaDatasCheckService;
    @Mock
    private LotService lotService;
    @Mock
    private DeliveryReportingService reportService;
    @Mock
    private ImageMagickService imService;
    @Mock
    private SampleService sampleService;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private BinaryRepository binaryRepository;
    @Mock
    private DeliveryProgressService deliveryProgressService;
    @Mock
    private SlipService slipService;
    @Mock
    private DocPropertyTypeService docPropertyTypeService;
    @Mock
    private BibliographicRecordService bibliographicRecordService;
    @Mock
    private TesseractService tesseractService;
    @Mock
    private DocUnitService docUnitService;
    @Mock
    private UIDigitalDocumentService uidigitalDocumentService;
    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    private DeliveryAsyncService service;

    @BeforeEach
    public void setUp() {
        service = new DeliveryAsyncService(deliveryService,
                                           esDeliveryService,
                                           physicalDocumentRepository,
                                           docPageService,
                                           digitalDocumentService,
                                           transactionService,
                                           autoCheckService,
                                           bm,
                                           mapper,
                                           metaDatasCheckService,
                                           lotService,
                                           reportService,
                                           imService,
                                           sampleService,
                                           workflowService,
                                           binaryRepository,
                                           deliveryProgressService,
                                           slipService,
                                           docPropertyTypeService,
                                           bibliographicRecordService,
                                           tesseractService,
                                           docUnitService,
                                           uidigitalDocumentService,
                                           imageMetadataRepository);
    }

    @Test
    public void getFormatFilterTest() {
        final List<File> files = new ArrayList<>();
        final String expectedFormat = "TIFF";
        final File baseDirectory = new File(RESOURCE_FOLDER + "/format");

        final File file1 = new File(baseDirectory, "IHE14001000.tiff");
        final File file2 = new File(baseDirectory, "IHE14001000.TIFF");
        final File file3 = new File(baseDirectory, "IHE14001000.png");
        final File file4 = new File(baseDirectory, "IHE14001000.pdf");

        files.addAll(FileUtils.listFiles(baseDirectory, service.getFormatFilter(expectedFormat), TrueFileFilter.TRUE));
        assertEquals(2, files.size());
    }

    @Test
    public void getPrefixFilterTest() {
        final List<File> files = new ArrayList<>();
        final String prefix = "BSG_120587445";
        final String seqSeparator = "_";
        final File baseDirectory = new File(RESOURCE_FOLDER + "/prefix");

        final File file1 = new File(baseDirectory, "BSG_120587445_001.tiff");
        final File file2 = new File(baseDirectory, "BSG_120587445_002.TIFF");
        final File file3 = new File(baseDirectory, "BSG_120587445_003.png");
        final File file4 = new File(baseDirectory, "BSG_120587445_004.pdf");

        files.addAll(FileUtils.listFiles(baseDirectory, service.getPrefixFilter(prefix, seqSeparator, false, false), TrueFileFilter.TRUE));
        assertEquals(4, files.size());
    }
}
