package fr.progilone.pgcn.web.rest.delivery;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.delivery.DeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.ManualDeliveryDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.delivery.DeliveryReportingService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.delivery.ui.UIDeliveryService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryControllerTest {

    @Mock
    private UIDeliveryService uiDeliveryService;
    @Mock
    private DeliveryService deliveryService;
    @Mock
    private EsDeliveryService esDeliveryService;
    @Mock
    private AccessHelper accessHelper;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    
    @Mock
    private DeliveryReportingService deliveryReportingService;
    
    @Mock
    private SampleService sampleService;

    private MockMvc restMockMvc;

    private final RequestPostProcessor admin = roles(DEL_HAB8);
    private final RequestPostProcessor presta = roles(DEL_HAB2);

    @Before
    public void setUp() throws Exception {
        final DeliveryController controller =
            new DeliveryController(uiDeliveryService, deliveryService, deliveryReportingService, esDeliveryService, accessHelper, libraryAccesssHelper, sampleService);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(uiDeliveryService.update(any(ManualDeliveryDTO.class))).then((Answer<DeliveryDTO>) invocation -> {
            final ManualDeliveryDTO delivery = invocation.getArgumentAt(0, ManualDeliveryDTO.class);
            final DeliveryDTO dto1 = new DeliveryDTO();
            dto1.setIdentifier(delivery.getIdentifier());
            return dto1;
        });
    }

    @Test
    public void testUpdateAdmin() throws Exception {
        final String identifier = "7ba77eb6-da75-4fd8-b3c6-cd1da2659032";
        final ManualDeliveryDTO dto = new ManualDeliveryDTO();
        dto.setIdentifier(identifier);

        when(accessHelper.checkDelivery(identifier)).thenReturn(false, true);

        // 403, admin
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(admin))
                        // check
                        .andExpect(status().isForbidden());

        // 200, admin
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(admin))
                        // check
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(identifier));
    }

    @Test
    public void testUpdatePresta() throws Exception {
        final String identifier = "7ba77eb6-da75-4fd8-b3c6-cd1da2659032";
        final String lotId1 = "6e55d2e0-3f10-4410-85b0-75d2a24de93d";
        final String lotId2 = "68e95a86-932e-4538-907f-fc4d5279790b";

        final ManualDeliveryDTO dto = new ManualDeliveryDTO();
        dto.setIdentifier(identifier);
        dto.setPayment(Delivery.DeliveryPayment.UNPAID.name());
        dto.setMethod(Delivery.DeliveryMethod.FTP.name());
        dto.setLot(new SimpleLotDTO());
        dto.getLot().setIdentifier(lotId1);

        final Delivery dbDelivery = new Delivery();
        dbDelivery.setIdentifier(identifier);
        dbDelivery.setPayment(Delivery.DeliveryPayment.UNPAID);
        dbDelivery.setMethod(Delivery.DeliveryMethod.FTP);
        dbDelivery.setLot(new Lot());
        dbDelivery.getLot().setIdentifier(lotId1);

        when(accessHelper.checkDelivery(identifier)).thenReturn(false, true);
        when(deliveryService.getOne(identifier)).thenReturn(dbDelivery);

        // 403, presta, checkDelivery ko
        final ResultActions perform = this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                       .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                                                       .with(presta));
        perform
            // check
            .andExpect(status().isForbidden());

        // 403, presta, hasProtectedChanges payment ko
        dto.setPayment(Delivery.DeliveryPayment.PAID.name());
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(presta))
                        // check
                        .andExpect(status().isForbidden());

        // 403, presta, hasProtectedChanges method ko
        dto.setPayment(Delivery.DeliveryPayment.UNPAID.name());
        dto.setMethod(Delivery.DeliveryMethod.DISK.name());
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(presta))
                        // check
                        .andExpect(status().isForbidden());

        // 403, presta, hasProtectedChanges lot ko
        dto.setMethod(Delivery.DeliveryMethod.FTP.name());
        dto.getLot().setIdentifier(lotId2);
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(presta))
                        // check
                        .andExpect(status().isForbidden());

        // 200, presta
        dto.getLot().setIdentifier(lotId1);
        this.restMockMvc.perform(post("/api/rest/delivery/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                         .content(TestUtil.convertObjectToJsonBytes(dto))
                                                                         .with(presta))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(identifier));
    }
}
