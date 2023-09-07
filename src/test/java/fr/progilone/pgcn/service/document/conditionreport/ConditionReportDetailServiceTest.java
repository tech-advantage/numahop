package fr.progilone.pgcn.service.document.conditionreport;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.CONDREPORT_DETAIL_DESC_EMPTY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportDetailRepository;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.util.CatchAndReturnArgumentAt;
import fr.progilone.pgcn.util.TestUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ConditionReportDetailServiceTest {

    private static final String USER_ID = "90cdcf40-ff39-4d8d-aad5-249c29a94b3a";

    @Mock
    private ConditionReportDetailRepository conditionReportDetailRepository;
    @Mock
    private PropertyConfigurationService propertyConfigurationService;
    @Mock
    private UserService userService;

    private ConditionReportDetailService service;

    @BeforeEach
    public void setUp() {
        service = new ConditionReportDetailService(conditionReportDetailRepository, propertyConfigurationService, userService);

        final CustomUserDetails customUserDetails = new CustomUserDetails(USER_ID, null, null, null, null, null, false, User.Category.OTHER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(customUserDetails, "3b03c8c5-c552-450e-a91d-7bb850fd8186");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void testCreate1() {
        final ConditionReport report = new ConditionReport();
        report.setIdentifier("961993c6-1233-49f0-abd3-f28c494a151c");
        when(conditionReportDetailRepository.save(any(ConditionReportDetail.class))).then(new ReturnsArgumentAt(0));

        final ConditionReportDetail actual = service.create(ConditionReportDetail.Type.LIBRARY_LEAVING, report);

        assertNotNull(actual.getDate());
        assertEquals(ConditionReportDetail.Type.LIBRARY_LEAVING, actual.getType());
        assertSame(report, actual.getReport());
    }

    @Test
    public void testCreate2() {
        final String fromDetailId = "cf4d980a-7b52-49e7-a55f-8ebaa327ce38";
        final String detailId = "1adbb228-a1a4-4ca6-ae99-bb4e58efcecb";

        final Description binding = new Description();
        binding.setProperty(new DescriptionProperty());
        binding.setValue(new DescriptionValue());
        binding.setComment("binding comment");

        final ConditionReport report = new ConditionReport();
        report.setIdentifier("a5c504ee-c7ce-4a93-88a6-248d990801ba");

        final ConditionReportDetail fromDetail = new ConditionReportDetail();
        fromDetail.setIdentifier(fromDetailId);
        fromDetail.setAdditionnalDesc("setAdditionnalDesc");
        fromDetail.setBindingDesc("setBindingDesc");
        fromDetail.addDescription(binding);
        fromDetail.setBodyDesc("setBodyDesc");
        fromDetail.setDim1(1);
        fromDetail.setDim2(2);
        fromDetail.setDim3(3);
        fromDetail.setLibWriterFunction("setLibWriterFunction");
        fromDetail.setLibWriterName("setLibWriterName");
        fromDetail.setNbViewAdditionnal(4);
        fromDetail.setNbViewBinding(5);
        fromDetail.setNbViewBody(6);
        fromDetail.setProvWriterFunction("setProvWriterFunction");
        fromDetail.setProvWriterName("setProvWriterName");
        fromDetail.setReport(report);

        final User ahopkins = new User();
        ahopkins.setFirstname("Anthony");
        ahopkins.setSurname("Hopkins");
        ahopkins.setFunction("Actor");

        final CatchAndReturnArgumentAt<ConditionReportDetail> saveAnswer = new CatchAndReturnArgumentAt<>(0, detailId);

        // detail null
        ConditionReportDetail actual = service.create(ConditionReportDetail.Type.DIGITALIZATION, "unknown_id");
        assertNull(actual);

        when(conditionReportDetailRepository.save(any(ConditionReportDetail.class))).then(saveAnswer);
        when(conditionReportDetailRepository.findByIdentifier(fromDetailId)).thenReturn(fromDetail);
        when(conditionReportDetailRepository.findByIdentifier(detailId)).then(invocation -> saveAnswer.getDomainObject());
        when(conditionReportDetailRepository.getMaxPositionByConditionReportIdentifier(report.getIdentifier())).thenReturn(3);
        when(userService.findByIdentifier(USER_ID)).thenReturn(ahopkins);

        // detail ok
        actual = service.create(ConditionReportDetail.Type.DIGITALIZATION, fromDetail.getIdentifier());

        assertNotNull(actual.getDate());
        assertEquals(ConditionReportDetail.Type.DIGITALIZATION, actual.getType());
        assertEquals(fromDetail.getReport(), actual.getReport());
        assertEquals(4, actual.getPosition());
        assertEquals(fromDetail.getAdditionnalDesc(), actual.getAdditionnalDesc());
        assertEquals(fromDetail.getBindingDesc(), actual.getBindingDesc());
        assertEquals(fromDetail.getBodyDesc(), actual.getBodyDesc());
        assertEquals(fromDetail.getDim1(), actual.getDim1());
        assertEquals(fromDetail.getDim2(), actual.getDim2());
        assertEquals(fromDetail.getDim3(), actual.getDim3());
        assertEquals(ahopkins.getLogin(), actual.getLibWriterName());
        assertEquals(ahopkins.getFunction(), actual.getLibWriterFunction());

        assertEquals(fromDetail.getNbViewAdditionnal(), actual.getNbViewAdditionnal());
        assertEquals(fromDetail.getNbViewBinding(), actual.getNbViewBinding());
        assertEquals(fromDetail.getNbViewBody(), actual.getNbViewBody());
        // TYPE == OTHER => provWriter.. n'est plus modifié
        assertNull(actual.getProvWriterFunction());
        assertNull(actual.getProvWriterName());

        assertEquals(1, actual.getDescriptions().size());
        final Description actualBinding = actual.getDescriptions().iterator().next();
        assertEquals(binding.getComment(), actualBinding.getComment());
        assertSame(binding.getProperty(), actualBinding.getProperty());
        assertSame(binding.getValue(), actualBinding.getValue());
    }

    @Test
    public void testFindByIdentifier() {
        final ConditionReportDetail detail = getConditionReportDetail();
        when(conditionReportDetailRepository.findByIdentifier(detail.getIdentifier())).thenReturn(detail);

        final ConditionReportDetail actual = service.findByIdentifier(detail.getIdentifier());
        assertSame(detail, actual);
    }

    @Test
    public void testFindByConditionReport() {
        final ConditionReportDetail detail = getConditionReportDetail();
        final List<ConditionReportDetail> details = Collections.singletonList(detail);
        when(conditionReportDetailRepository.findByConditionReportIdentifier(detail.getIdentifier())).thenReturn(details);

        final List<ConditionReportDetail> actual = service.findByConditionReport(detail.getIdentifier());
        assertSame(details, actual);
    }

    @Test
    public void testFindDocUnitByIdentifier() {
        final String reportId = "34ce6c28-3d14-4e2b-8368-6409080af41d";
        final DocUnit docUnit = new DocUnit();
        when(conditionReportDetailRepository.findDocUnitByIdentifier(reportId)).thenReturn(docUnit);

        final DocUnit actual = service.findDocUnitByIdentifier(reportId);
        assertSame(docUnit, actual);
    }

    @Test
    public void testDelete() {
        final ConditionReportDetail otherDetail = new ConditionReportDetail();
        otherDetail.setPosition(14);

        final ConditionReportDetail detail = getConditionReportDetail();
        when(conditionReportDetailRepository.findById(detail.getIdentifier())).thenReturn(Optional.of(detail));
        when(conditionReportDetailRepository.findByConditionReportIdentifier(detail.getReport().getIdentifier())).thenReturn(Collections.singletonList(otherDetail));

        // CONDREPORT_DETAIL_MANDATORY
        try {
            detail.setType(ConditionReportDetail.Type.LIBRARY_LEAVING);
            service.delete(detail.getIdentifier());
            fail("testDelete should have failed");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.CONDREPORT_DETAIL_MANDATORY);
            verify(conditionReportDetailRepository, never()).saveAll(anyList());
        }

        // Ok
        try {
            detail.setType(ConditionReportDetail.Type.DIGITALIZATION);
            service.delete(detail.getIdentifier());

            verify(conditionReportDetailRepository).delete(detail);
            verify(conditionReportDetailRepository).saveAll(anyList());
            assertEquals(0, otherDetail.getPosition());

        } catch (final PgcnValidationException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testSave() {
        final ConditionReportDetail detail = getConditionReportDetail();
        final Library library = new Library();
        library.setIdentifier("b86330d4-4739-45ae-809d-b6ee9eb24a9c");
        final DocUnit docUnit = new DocUnit();
        docUnit.setLibrary(library);

        final DescriptionProperty prop1 = new DescriptionProperty();
        final DescriptionProperty prop2 = new DescriptionProperty();

        final PropertyConfiguration conf1 = new PropertyConfiguration();
        conf1.setDescProperty(prop1);
        conf1.setRequired(true);
        final PropertyConfiguration conf2 = new PropertyConfiguration();
        conf2.setDescProperty(prop2);
        conf2.setRequired(true);

        when(conditionReportDetailRepository.findByIdentifier(anyString())).thenReturn(detail);
        when(conditionReportDetailRepository.save(detail)).thenReturn(detail);
        when(conditionReportDetailRepository.findDocUnitByIdentifier(detail.getIdentifier())).thenReturn(docUnit);
        when(propertyConfigurationService.findByLibrary(docUnit.getLibrary())).thenReturn(Arrays.asList(conf1, conf2));

        // CONDREPORT_DETAIL_EMPTY
        try {
            service.save(detail);
            fail("testSave should have failed");

        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, PgcnErrorCode.CONDREPORT_DETAIL_EMPTY, CONDREPORT_DETAIL_DESC_EMPTY);
        }

        // Ok
        try {
            final Description desc1 = new Description();
            desc1.setProperty(prop1);
            desc1.setValue(new DescriptionValue());
            detail.addDescription(desc1);

            final Description desc2 = new Description();
            desc2.setProperty(prop2);
            desc2.setValue(new DescriptionValue());
            detail.addDescription(desc2);

            final ConditionReportDetail actual = service.save(detail);
            assertSame(detail, actual);

        } catch (final PgcnValidationException e) {
            fail("unexpected error " + e.getMessage());

        }
    }

    private ConditionReportDetail getConditionReportDetail() {
        final ConditionReportDetail detail = new ConditionReportDetail();
        detail.setIdentifier("6b06aa95-579f-42b3-aca6-6df9f3a2a0f6");

        final ConditionReport report = new ConditionReport();
        report.setIdentifier("1c9e58ec-4125-4da4-80fb-e88e84792172");
        detail.setReport(report);

        return detail;
    }
}
