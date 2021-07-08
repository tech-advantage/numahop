package fr.progilone.pgcn.service.document.ui;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.service.LockService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.mapper.UIDocUnitMapper;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.cines.CinesRequestHandlerService;
import fr.progilone.pgcn.service.exchange.cines.ExportMetsService;
import fr.progilone.pgcn.service.exchange.cines.ui.UICinesReportService;
import fr.progilone.pgcn.service.exchange.ead.ExportEadService;
import fr.progilone.pgcn.service.exchange.internetarchive.ui.UIInternetArchiveReportService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.exportftpconfiguration.ExportFTPConfigurationService;
import fr.progilone.pgcn.service.storage.AltoService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

@RunWith(MockitoJUnitRunner.class)
public class UIDocUnitServiceTest {

    @Mock
    private DocUnitService docUnitService;
    @Mock
    private ExportEadService exportEadService;
    @Mock
    private UIDocUnitMapper uiDocUnitMapper;
    @Mock
    private  UICinesReportService uiCinesReportService;
    @Mock
    private  UIInternetArchiveReportService uiIAReportService;
    @Mock
    private  ExportMetsService exportMetsService;
    @Mock
    private  BinaryStorageManager bm;
    @Mock
    private  UIBibliographicRecordService uiBibliographicRecordService;
    @Mock
    private  LockService lockService;
    @Mock
    private  WorkflowService workflowService;
    @Mock
    private  EsDocUnitService esDocUnitService;
    @Mock
    private  CinesRequestHandlerService cinesRequestHandlerService;
    @Mock
    private  ExportFTPConfigurationService exportFTPConfigurationService;
    @Mock
    private  SftpService sftpService;
    @Mock
    private  CryptoService cryptoService;
    @Mock
    private  LotRepository lotRepository;
    @Mock
    private  DeliveryService deliveryService;
    @Mock
    private  DigitalDocumentService digitalDocumentService;
    @Mock
    private AltoService altoService;
    
    private UIDocUnitService service;
    
    @Before
    public void setUp() {
        service = new UIDocUnitService(docUnitService, exportEadService, uiDocUnitMapper,
                                       uiCinesReportService, uiIAReportService, exportMetsService,
                                       bm, uiBibliographicRecordService, lockService,
                                       workflowService, esDocUnitService, cinesRequestHandlerService,
                                       exportFTPConfigurationService, sftpService, cryptoService,
                                       lotRepository,
                                       deliveryService,
                                       digitalDocumentService,
                                       altoService);
        
    }
    
    @Test
    public void isLotRenumTest() {
        
        final Lot lot = getInitialLot();
        
        assertFalse(service.isLotRenum(Collections.emptySet(), lot));
        
        final DocUnit renumDoc = new DocUnit();
        renumDoc.setIdentifier("id_1");
        renumDoc.setLot(lot);
        final Set<DocUnit> renumDus = new HashSet<>();
        renumDus.add(renumDoc);
        
        assertFalse(service.isLotRenum(renumDus, lot));
        
        final Lot lot2 = new Lot();
        lot2.setIdentifier("another");
        final DocUnit renumDoc2 = new DocUnit();
        renumDoc2.setIdentifier("id_2");
        renumDoc2.setLot(lot2);
        
        renumDus.add(renumDoc2);
        
        assertTrue(service.isLotRenum(renumDus, lot));
        
    }
    
    private Lot getInitialLot()  {
        
        final Lot lot = new Lot();
        lot.setIdentifier("id_lot_1");
        
        final DocUnit doc1 = new DocUnit();
        doc1.setIdentifier("id_doc_unit_1");
        doc1.setLot(lot);
        
        final DocUnit doc2 = new DocUnit();
        doc2.setIdentifier("id_doc_unit_2");
        doc2.setLot(lot);
        
        final Set<DocUnit> docUnits = new HashSet<>();
        docUnits.add(doc1);
        docUnits.add(doc2);
        
        lot.setDocUnits(docUnits);
        
        return lot;
    }
}
