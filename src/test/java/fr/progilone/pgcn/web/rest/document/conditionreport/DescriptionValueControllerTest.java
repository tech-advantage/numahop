package fr.progilone.pgcn.web.rest.document.conditionreport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionValueService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class DescriptionValueControllerTest {

    @Mock
    private DescriptionValueService descBindingValueService;

    private MockMvc restMockMvc;

    @BeforeEach
    public void setUp() {
        final DescriptionValueController controller = new DescriptionValueController(descBindingValueService);
        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, DescriptionProperty.class, TestConverterFactory.getConverter(DescriptionProperty.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @Test
    public void testCreate() throws Exception {
        final DescriptionValue value = new DescriptionValue();
        value.setIdentifier("VALUE-TEST");
        when(descBindingValueService.save(any(DescriptionValue.class))).thenReturn(value);

        // 201
        this.restMockMvc.perform(post("/api/rest/condreport_desc_value").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(value)))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(value.getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final String identifier = "VALUE-TEST";

        // 200
        this.restMockMvc.perform(delete("/api/rest/condreport_desc_value/{id}", identifier).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(descBindingValueService).delete(identifier);
    }

    @Test
    public void testFindByProperty() throws Exception {
        final DescriptionValue value = new DescriptionValue();
        value.setIdentifier("VALUE-TEST");
        when(descBindingValueService.findByPropertyIdentifier("PROPERTY-001")).thenReturn(Collections.singletonList(value));

        this.restMockMvc.perform(get("/api/rest/condreport_desc_value").param("property", "PROPERTY-001").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(value.getIdentifier()));
    }

    @Test
    public void testUpdate() throws Exception {
        final DescriptionValue value = new DescriptionValue();
        value.setIdentifier("VALUE-TEST");
        when(descBindingValueService.save(any(DescriptionValue.class))).thenReturn(value);

        // 200
        this.restMockMvc.perform(post("/api/rest/condreport_desc_value/{id}", value.getIdentifier()).contentType(MediaType.APPLICATION_JSON)
                                                                                                    .content(TestUtil.convertObjectToJsonBytes(value)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(value.getIdentifier()));
    }
}
