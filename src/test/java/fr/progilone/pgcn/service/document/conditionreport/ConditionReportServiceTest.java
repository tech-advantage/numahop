package fr.progilone.pgcn.service.document.conditionreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturingMatcher;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.dto.document.SimpleListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDTO;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepositoryCustom;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.service.exchange.template.OdtEngineService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.util.CatchAndReturnArgumentAt;
import fr.progilone.pgcn.util.TestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ConditionReportServiceTest {

    private static final String USER_LIBRARY_ID = "70c32f28-0014-4a30-90ce-f494fe613e81";

    @Mock
    private ConditionReportRepository conditionReportRepository;
    @Mock
    private ConditionReportDetailService conditionReportDetailService;
    @Mock
    private ConditionReportAttachmentService conditionReportAttachmentService;
    @Mock
    private DocUnitRepository docUnitRepository;
    @Mock
    private EsConditionReportService esConditionReportService;
    @Mock
    private LibraryService libraryService;
    @Mock
    private OdtEngineService odtEngineService;
    @Mock
    private UserService userService;
    @Mock
    private DocUnitWorkflowRepository docUnitWorkflowRepository;

    private ConditionReportService service;

    @Mock
    private JasperReportsService jasperReportService;
    @Mock
    private ConditionReportSlipConfigurationService conditionReportSlipConfigurationService;

    @Before
    public void setUp() {
        service = new ConditionReportService(conditionReportRepository,
                                             conditionReportDetailService,
                                             conditionReportAttachmentService,
                                             docUnitRepository,
                                             esConditionReportService,
                                             libraryService,
                                             odtEngineService,
                                             userService,
                                             jasperReportService,
                                             conditionReportSlipConfigurationService,
                                             docUnitWorkflowRepository);

        final CustomUserDetails customUserDetails =
            new CustomUserDetails("90cdcf40-ff39-4d8d-aad5-249c29a94b3a", "mickey", null, null, USER_LIBRARY_ID, null, false, User.Category.OTHER);
        final TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(customUserDetails, "3b03c8c5-c552-450e-a91d-7bb850fd8186");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void testCreate() {
        final String docUnitId = "70aaf390-fa10-4f0a-ac3d-464f0accfd5c";
        final String libraryId = "396272f0-5caa-405b-aae3-73f194f157ed";
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";

        final Library library = new Library();
        library.setIdentifier(libraryId);

        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier(docUnitId);
        docUnit.setLibrary(library);

        final CatchAndReturnArgumentAt<ConditionReport> saveAnswer = new CatchAndReturnArgumentAt<>(0, reportId);

        when(docUnitRepository.findOne(docUnitId)).thenReturn(null, docUnit);
        when(conditionReportRepository.findByDocUnit(docUnitId)).thenReturn(new ConditionReport(), (ConditionReport) null);
        when(conditionReportRepository.save(any(ConditionReport.class))).then(saveAnswer);
        when(conditionReportRepository.findByIdentifier(reportId)).then(invocation -> saveAnswer.getDomainObject());

        // #1 docUnit null
        try {
            final ConditionReport actual = service.create(docUnitId);
            assertNull(actual);

        } catch (final PgcnValidationException e) {
            fail("fail on error " + e.getMessage());
        }

        // #2 report existe déjà
        try {
            service.create(docUnitId);
            fail("no CONDREPORT_DUPLICATE ?");

        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.CONDREPORT_DUPLICATE);
        }

        // #3 docUnit not null
        try {
            final ConditionReport actual = service.create(docUnitId);

            assertNotNull(actual);
            assertEquals(docUnitId, actual.getDocUnit().getIdentifier());

            verify(conditionReportDetailService).create(ConditionReportDetail.Type.LIBRARY_LEAVING, actual);

        } catch (final PgcnValidationException e) {
            fail("fail on error " + e.getMessage());
        }
    }

    @Test
    public void testGetNewConditionReport() {
        final DocUnit docUnit = new DocUnit();

        // Prestataire du projet
        final User pjProvider = new User();
        pjProvider.setEmail("pjProvider email");
        pjProvider.setFirstname("pjProvider firstname");
        pjProvider.setSurname("pjProvider surname");
        pjProvider.setPhoneNumber("pjProvider phone");

        final Project project = new Project();
        project.setProvider(pjProvider);

        docUnit.setProject(project);

        ConditionReport actual = service.getNewConditionReport(docUnit);

        assertNotNull(actual);
        assertSame(docUnit, actual.getDocUnit());
        assertEquals(pjProvider.getEmail(), actual.getProviderEmail());
        assertEquals(pjProvider.getFullName(), actual.getProviderName());
        assertEquals(pjProvider.getPhoneNumber(), actual.getProviderPhone());

        // Prestataire du lot
        final User lotProvider = new User();
        lotProvider.setEmail("lotProvider email");
        lotProvider.setFirstname("lotProvider firstname");
        lotProvider.setSurname("lotProvider surname");
        lotProvider.setPhoneNumber("lotProvider phone");

        final Lot lot = new Lot();
        lot.setProvider(lotProvider);

        docUnit.setLot(lot);

        actual = service.getNewConditionReport(docUnit);

        assertNotNull(actual);
        assertSame(docUnit, actual.getDocUnit());
        assertEquals(lotProvider.getEmail(), actual.getProviderEmail());
        assertEquals(lotProvider.getFullName(), actual.getProviderName());
        assertEquals(lotProvider.getPhoneNumber(), actual.getProviderPhone());
    }

    @Test
    public void testFindByDocUnit() {
        final String docUnitId = "70aaf390-fa10-4f0a-ac3d-464f0accfd5c";
        final ConditionReport report = new ConditionReport();

        when(conditionReportRepository.findByDocUnit(docUnitId)).thenReturn(report);

        final ConditionReport actual = service.findByDocUnit(docUnitId);
        assertSame(report, actual);
    }

    @Test
    public void testFindByIdentifier() {
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";
        final ConditionReport report = new ConditionReport();

        when(conditionReportRepository.findByIdentifier(reportId)).thenReturn(report);

        final ConditionReport actual = service.findByIdentifier(reportId);
        assertSame(report, actual);
    }

    @Test
    public void testFindDocUnitByIdentifier() {
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";
        final DocUnit docUnit = new DocUnit();

        when(conditionReportRepository.findDocUnitByIdentifier(reportId)).thenReturn(docUnit);

        final DocUnit actual = service.findDocUnitByIdentifier(reportId);
        assertSame(docUnit, actual);
    }

    @Test
    public void testDelete() {
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";
        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);

        when(conditionReportRepository.findOne(reportId)).thenReturn(report);

        service.delete(reportId);

        verify(conditionReportAttachmentService).getAttachmentDir(report);
        verify(conditionReportRepository).delete(report);
    }

    @Test
    public void testDeleteByDocUnitIdentifier() {
        final String docUnitId = "0e9d2469-08fa-4eb4-a13e-a84b8073fcdd";
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";
        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);

        when(conditionReportRepository.findByDocUnitIdentifier(docUnitId)).thenReturn(report);

        service.deleteByDocUnitIdentifier(docUnitId);

        verify(conditionReportAttachmentService).getAttachmentDir(report);
        verify(conditionReportRepository).delete(report);
    }

    @Test
    public void testSave() {
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";
        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);

        when(conditionReportRepository.save(report)).thenReturn(report);
        when(conditionReportRepository.findByIdentifier(reportId)).thenReturn(report);

        final ConditionReport actual = service.save(report);
        assertSame(report, actual);
    }

    @Test
    public void testSearch() {
        final List<String> libraries = new ArrayList<>();
        final ConditionReportRepositoryCustom.DimensionFilter dimensions =
            new ConditionReportRepositoryCustom.DimensionFilter(ConditionReportRepositoryCustom.DimensionFilter.Operator.EQ, 1, 2, 3);
        final LocalDate from = LocalDate.now().minusMonths(1);
        final LocalDate to = LocalDate.now();
        final List<String> descriptions = Arrays.asList("a=TORTUE_NINJA", "a=COURGE_DU_NEBRASKA", "y=28_JOURS_PLUS_TARD");
        final Integer page = 0;
        final Integer size = 10;
        final List<String> sorts = Collections.singletonList("docUnit.pgcnId");

        final Page<String> ids = new PageImpl<>(Arrays.asList("3c2cffc6-8301-4433-80b4-b97a09e530c8", "af02ba13-83e5-4e12-b347-30fc655de2b6"));
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("ef77234f-8062-4291-976f-478d0ab3461e");
        final ConditionReport report = new ConditionReport();
        report.setIdentifier("3c2cffc6-8301-4433-80b4-b97a09e530c8");
        report.setDocUnit(docUnit);
        final List<ConditionReport> reports = Collections.singletonList(report);

        final CapturingMatcher<Map<String, List<String>>> descMatcher = new CapturingMatcher<>();

        when(conditionReportRepository.search(eq(libraries),
                                              eq(from),
                                              eq(to),
                                              eq(dimensions),
                                              argThat(descMatcher),
                                              any(),
                                              eq(false),
                                              any(Pageable.class))).thenReturn(ids);
        when(conditionReportRepository.findByIdentifierIn(ids.getContent())).thenReturn(reports);
        when(docUnitRepository.findByIdentifierInWithProj(anyListOf(String.class))).thenReturn(Collections.singletonList(docUnit));

        final Page<ConditionReportService.SearchResult> actual =
            service.search(libraries, dimensions, from, to, descriptions, null, false, page, size, sorts);

        final List<ConditionReportDTO> actualReports =
            actual.getContent().stream().map(ConditionReportService.SearchResult::getReport).collect(Collectors.toList());
        final List<SimpleListDocUnitDTO> actualDocs =
            actual.getContent().stream().map(ConditionReportService.SearchResult::getDocUnit).collect(Collectors.toList());

        // résultat
        TestUtil.checkCollectionContainsSameElements(reports.stream().map(ConditionReport::getIdentifier).collect(Collectors.toList()),
                                                     actualReports.stream().map(ConditionReportDTO::getIdentifier).collect(Collectors.toList()));
        assertEquals(page.intValue(), actual.getNumber());
        assertEquals(size.intValue(), actual.getSize());
        assertEquals(reports.size(), actual.getTotalElements());

        assertEquals(1, actualDocs.size());
        assertEquals(docUnit.getIdentifier(), actualDocs.get(0).getIdentifier());

        // descriptions
        final Map<String, List<String>> actualBindings = descMatcher.getLastValue();
        assertEquals(2, actualBindings.size());
        TestUtil.checkCollectionContainsSameElements(Arrays.asList("TORTUE_NINJA", "COURGE_DU_NEBRASKA"), actualBindings.get("a"));
        TestUtil.checkCollectionContainsSameElements(Collections.singletonList("28_JOURS_PLUS_TARD"), actualBindings.get("y"));
    }

    @Test
    public void testExportDocument() throws PgcnTechnicalException {
        final String docUnitId = "70aaf390-fa10-4f0a-ac3d-464f0accfd5c";
        final String libraryId = "396272f0-5caa-405b-aae3-73f194f157ed";
        final String reportId = "6b06aa95-579f-42b3-aca6-6df9f3a2a0f6";

        final Library library = new Library();
        library.setIdentifier(libraryId);

        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier(docUnitId);
        docUnit.setLibrary(library);

        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);
        report.setDocUnit(docUnit);

        when(conditionReportRepository.findByIdentifier(reportId)).thenReturn(report);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        // ODT
        service.exportDocument(reportId, out, ConditionReportService.ConvertType.ODT);

        verify(odtEngineService).generateDocumentODT(eq(Name.ConditionReport),
                                                     eq(report.getDocUnit().getLibrary()),
                                                     anyMapOf(String.class, Object.class),
                                                     anyMapOf(String.class, IImageProvider.class),
                                                     same(out));

        // PDF
        service.exportDocument(reportId, out, ConditionReportService.ConvertType.PDF);

        verify(odtEngineService).generateDocumentPDF(eq(Name.ConditionReport),
                                                     eq(report.getDocUnit().getLibrary()),
                                                     anyMapOf(String.class, Object.class),
                                                     anyMapOf(String.class, IImageProvider.class),
                                                     same(out),
                                                     isNull(FieldsMetadata.class));
    }

}
