package fr.progilone.pgcn.web.rest.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionPropertyService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DescriptionPropertyControllerTest {

    @Mock
    private DescriptionPropertyService descBindingPropertyService;

    private MockMvc restMockMvc;

    @Before
    public void setUp() {
        final DescriptionPropertyController controller = new DescriptionPropertyController(descBindingPropertyService);
        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, DescriptionProperty.class, TestConverterFactory.getConverter(DescriptionProperty.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @Test
    public void testCreate() throws Exception {
        final DescriptionProperty value = new DescriptionProperty();
        value.setIdentifier("VALUE-TEST");
        when(descBindingPropertyService.save(any(DescriptionProperty.class))).thenReturn(value);

        // 201
        this.restMockMvc.perform(post("/api/rest/condreport_desc_prop").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                       .content(TestUtil.convertObjectToJsonBytes(value)))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(value.getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final String identifier = "VALUE-TEST";

        // 200
        this.restMockMvc.perform(delete("/api/rest/condreport_desc_prop/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk());
        verify(descBindingPropertyService).delete(identifier);
    }

    @Test
    public void testFindByProperty() throws Exception {
        final DescriptionProperty property = new DescriptionProperty();
        property.setIdentifier("PROP-TEST");
        when(descBindingPropertyService.findAll()).thenReturn(Collections.singletonList(property));

        this.restMockMvc.perform(get("/api/rest/condreport_desc_prop").accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(property.getIdentifier()));
    }

    @Test
    public void testUpdate() throws Exception {
        final DescriptionProperty value = new DescriptionProperty();
        value.setIdentifier("VALUE-TEST");
        when(descBindingPropertyService.save(any(DescriptionProperty.class))).thenReturn(value);

        // 200
        this.restMockMvc.perform(post("/api/rest/condreport_desc_prop/{id}", value.getIdentifier()).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                   .content(TestUtil.convertObjectToJsonBytes(value)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(value.getIdentifier()));
    }
}
