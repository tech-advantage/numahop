package fr.progilone.pgcn.service.exchange;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_LABEL_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_LIBRARY_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_RULE_FIELD_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_RULE_LABEL_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_RULE_PGCNID_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_RULE_RIGHTS_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.MAPPING_TYPE_MANDATORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.repository.exchange.MappingRepository;
import fr.progilone.pgcn.repository.exchange.MappingRuleRepository;
import fr.progilone.pgcn.service.exchange.mapper.MappingMapper;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.util.CatchAndReturnArgumentAt;
import fr.progilone.pgcn.util.TestUtil;

/**
 * Created by Sebastien on 23/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingServiceTest {

    @Mock
    private ImportReportRepository importReportRepository;
    @Mock
    private MappingRepository mappingRepository;
    @Mock
    private MappingRuleRepository mappingRuleRepository;

    private MappingService service;

    @Before
    public void setUp() {
        final MappingMapper mapper = MappingMapper.INSTANCE;
        ReflectionTestUtils.setField(mapper, "simpleLibraryMapper", SimpleLibraryMapper.INSTANCE);
        service = new MappingService(importReportRepository, mappingRepository, mappingRuleRepository);
    }

    @Test
    public void testFindAllUsable() {
        final Set<Mapping> mappings = new HashSet<>();
        final String identifier = "M001";
        mappings.add(getMapping(identifier));

        when(mappingRepository.findAllUsableWithRules()).thenReturn(mappings);

        final Set<MappingDTO> actual = service.findAllUsable();
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindByType() {
        final Set<Mapping> mappings = new HashSet<>();
        final String identifier = "M001";
        mappings.add(getMapping(identifier));

        when(mappingRepository.findByTypeWithRules(Mapping.Type.MARC)).thenReturn(mappings);

        final Set<MappingDTO> actual = service.findByType(Mapping.Type.MARC);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindByLibrary() {
        final Library library = new Library();
        final Set<Mapping> mappings = new HashSet<>();
        final String identifier = "M002";
        mappings.add(getMapping(identifier));

        when(mappingRepository.findByTypeAndLibraryWithRules(Mapping.Type.MARC, library)).thenReturn(mappings);

        final Set<MappingDTO> actual = service.findByTypeAndLibrary(Mapping.Type.MARC, library);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindByTypeAndLibrary() {
        final Library library = new Library();
        final Set<Mapping> mappings = new HashSet<>();
        final String identifier = "M002";
        mappings.add(getMapping(identifier));

        when(mappingRepository.findByLibraryWithRules(library)).thenReturn(mappings);

        final Set<MappingDTO> actual = service.findByLibrary(library);
        assertEquals(1, actual.size());
        assertEquals(identifier, actual.iterator().next().getIdentifier());
    }

    @Test
    public void testFindOne() {
        final String id = "MAPPING-001";
        final Mapping mapping = getMapping(id);

        when(mappingRepository.findOneWithRules(id)).thenReturn(mapping);

        final Mapping actual = service.findOne(id);
        assertSame(mapping, actual);
    }

    @Test
    public void testDelete() {
        final String id = "MAPPING-001";
        service.delete(id);
        verify(mappingRepository).delete(id);
    }

    @Test
    public void testSave() {
        final Mapping mapping = new Mapping();
        mapping.setIdentifier("MAPPING-001");

        final MappingRule labelRule = new MappingRule();
        labelRule.setDocUnitField("label");
        final MappingRule pgcnIdRule = new MappingRule();
        pgcnIdRule.setDocUnitField("pgcnId");
        final MappingRule rightsRule = new MappingRule();
        rightsRule.setDocUnitField("rights");
        final MappingRule typeRule = new MappingRule();
        typeRule.setDocUnitField("type");

        when(mappingRepository.save(any(Mapping.class))).then(new ReturnsArgumentAt(0));
        when(mappingRepository.findOneWithRules("MAPPING-001")).thenReturn(mapping);

        // #1: validation failed
        try {
            service.save(mapping);
            fail("test Save should have failed !");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e,
                                        MAPPING_LABEL_MANDATORY,
                                        MAPPING_LIBRARY_MANDATORY,
                                        MAPPING_TYPE_MANDATORY,
                                        MAPPING_RULE_LABEL_MANDATORY,
                                        MAPPING_RULE_PGCNID_MANDATORY,
                                        MAPPING_RULE_RIGHTS_MANDATORY);
        }

        // #2 validation ok
        try {
            mapping.setLabel("Mapping des monographies");
            final Library lib = new Library();
            lib.setIdentifier("LIB-001");
            mapping.setLibrary(lib);
            mapping.setType(Mapping.Type.MARC);

            mapping.setRules(Arrays.asList(labelRule, pgcnIdRule, rightsRule, typeRule));

            final Mapping actual = service.save(mapping);

            assertEquals(mapping.getIdentifier(), actual.getIdentifier());
            assertNotNull(actual.getLabel());
        } catch (final PgcnValidationException e) {
            fail("unexpected failure: " + e.getMessage());
        }

        // #3 règles invalides
        final DocPropertyType ppty = new DocPropertyType();
        ppty.setIdentifier("author");

        final MappingRule rule01 = new MappingRule();
        final MappingRule rule02 = new MappingRule();
        rule02.setProperty(ppty);
        mapping.setRules(Arrays.asList(labelRule, pgcnIdRule, rightsRule, typeRule, rule01, rule02));

        try {
            service.save(mapping);
            fail("test Save should have failed !");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, MAPPING_RULE_FIELD_MANDATORY);
        }

        // #4 règles ok
        try {
            rule01.setDocUnitField("author");
            final Mapping actual = service.save(mapping);

            assertEquals(mapping.getIdentifier(), actual.getIdentifier());
            assertNotNull(actual.getLabel());
        } catch (final PgcnValidationException e) {
            fail("unexpected failure: " + e.getMessage());
        }
    }

    @Test
    public void testDuplicateMapping() {
        final String mappingId = "6b23ed03-ddd3-4cab-be0c-b2c52e5aad2d";
        final String libraryId = "13a8a3ca-cd78-4222-8b84-80080232a6bf";
        final String ruleId = "854ab4e6-6133-462b-8d97-e06425e61cb2";
        final String newLibraryId = "4e90f026-a372-4702-a458-64564132db46";
        final String newMappingId = "e01ed21e-2a65-4309-9d8d-c001855e5171";

        final Library library = new Library();
        library.setIdentifier(libraryId);

        final Mapping mapping = new Mapping();
        mapping.setIdentifier(mappingId);
        mapping.setLabel("mappingLabel");
        mapping.setType(Mapping.Type.EAD);
        mapping.setLibrary(library);

        final MappingRule rule = new MappingRule();
        rule.setIdentifier(ruleId);
        rule.setDocUnitField("docUnitField");
        mapping.addRule(rule);

        final CatchAndReturnArgumentAt<Mapping> mappingCatcher = new CatchAndReturnArgumentAt<>(0, newMappingId);

        when(mappingRepository.findOneWithRules(mappingId)).thenReturn(mapping);
        when(mappingRepository.save(any(Mapping.class))).then(mappingCatcher);
        when(mappingRepository.findOneWithRules(newMappingId)).then(invocation -> mappingCatcher.getDomainObject());

        final Mapping actual = service.duplicateMapping(mappingId, newLibraryId);

        assertEquals(newMappingId, actual.getIdentifier());
        assertEquals(mapping.getLabel(), actual.getLabel());
        assertEquals(mapping.getType(), actual.getType());
        assertEquals(newLibraryId, actual.getLibrary().getIdentifier());

        assertEquals(mapping.getRules().size(), actual.getRules().size());
        final MappingRule actualRule = actual.getRules().get(0);
        assertNull(actualRule.getIdentifier());
        assertEquals(rule.getDocUnitField(), actualRule.getDocUnitField());
    }

    @Test
    public void testCountByPropertyType() {
        final DocPropertyType type = new DocPropertyType();
        type.setIdentifier("ID-PPTE");

        when(mappingRuleRepository.countByProperty(type)).thenReturn(10);

        final Integer count = service.countByPropertyType(type);

        assertEquals(10L, count.longValue());
    }

    private Mapping getMapping(final String identifier) {
        final Library library = new Library();
        library.setIdentifier("LIBRARY-001");

        final Mapping mapping = new Mapping();
        mapping.setIdentifier(identifier);
        mapping.setLabel("Chou-fleur");
        mapping.setLibrary(library);
        mapping.setType(Mapping.Type.MARC);
        return mapping;
    }
}
