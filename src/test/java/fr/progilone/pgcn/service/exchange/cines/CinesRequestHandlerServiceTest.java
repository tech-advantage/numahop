package fr.progilone.pgcn.service.exchange.cines;


import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.mail.MailboxService;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

@RunWith(MockitoJUnitRunner.class)
public class CinesRequestHandlerServiceTest {
    
    
    private String[] instanceLibraries = {"lib_test"};
    private static final String WORKING_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";
    
    private String aipFile = "aip.xml";
    
    
    @Mock
    private  CinesReportService cinesReportService;
    @Mock
    private  ExportCinesService exportCinesService;
    @Mock
    private  MailboxService mailboxService;
    @Mock
    private  MailboxConfigurationService mailboxConfigurationService;
    @Mock
    private  FileStorageManager fm;
    @Mock
    private  DocUnitService docUnitService;
    @Mock
    private TransactionService transactionService;
    
    private CinesRequestHandlerService service;
    
    
    @Before
    public void setUp() {
        service = new CinesRequestHandlerService(exportCinesService, cinesReportService, mailboxService, mailboxConfigurationService, fm, docUnitService, transactionService);
        ReflectionTestUtils.setField(service, "instanceLibraries", instanceLibraries);
        ReflectionTestUtils.setField(service, "cinesUpdatingEnabled", true);
        ReflectionTestUtils.setField(service, "workingDir", WORKING_DIR);
        ReflectionTestUtils.setField(service, "cacheDir", WORKING_DIR + "/cache");
        
    }
     
    /**
     * Utile pour tester la validite d'un fichier AIP.
     * pas d'interet pour le build
     */
    @Ignore
    @Test
    public void unmarshallingAipTest() {
        
        final File aip = new File("C:/Temp", aipFile);
        
        fr.progilone.pgcn.domain.jaxb.aip.PacType pac = null;
        assertTrue(aip.exists() && aip.canRead());
        
        final JAXBContext context;
        try {

            context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.aip.ObjectFactory.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final Object ob = unmarshaller.unmarshal(aip);
            pac = (fr.progilone.pgcn.domain.jaxb.aip.PacType) ob;
            
        } catch (final JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertTrue(pac != null);
        assertTrue("sc_0000129768_00000001704685".equals(pac.getDocMeta().getIdentifiantDocProducteur()));
        
    }

}
