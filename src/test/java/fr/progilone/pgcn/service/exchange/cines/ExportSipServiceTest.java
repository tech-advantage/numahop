package fr.progilone.pgcn.service.exchange.cines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameter.LibraryParameterType;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines.LibraryParameterValueCinesType;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.exception.ExportCinesException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.service.document.common.LanguageCodeService;
import fr.progilone.pgcn.service.exchange.cines.GenerateDocUnitUtil.GenerateDocUnitUtilEnum;
import fr.progilone.pgcn.service.library.LibraryParameterService;
import fr.progilone.pgcn.service.storage.FileStorageManager;

/**
 * Created by Sébastien on 28/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportSipServiceTest {

    private static final String DEFAULT_PUBLISHER = "Default publisher";

    private static final String DEFAULT_CREATOR = "Default creator";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private ExportSipService service;
    @Mock
    private LanguageCodeService languageCodeService;
    @Mock
    private CinesLanguageCodeService cinesLanguageCodeService;
    @Mock
    private LibraryParameterService libraryParameterService;
    @Mock
    private DocUnitRepository docUnitRepository;
    @Mock
    private FileStorageManager fm;

    private static final String CHECKSUM_METS = "acfde56g45er2rzf9864785";
    private static final String XSD_VALIDATION_PATH = "src/test/resources/xsd/sip.xsd";

    @Before
    public void setUp() {
        service = new ExportSipService(languageCodeService, cinesLanguageCodeService, libraryParameterService, docUnitRepository, fm);
        ReflectionTestUtils.setField(service, "sipSchema", XSD_VALIDATION_PATH);

        when(languageCodeService.getIso6393TForLanguage(anyString())).then(new ReturnsArgumentAt(0));
    }

    @Ignore
    @Test
    public void testWriteMetadata() throws JAXBException, IOException, SAXException, PgcnTechnicalException, ExportCinesException {
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {

            final BibliographicRecordDcDTO dcDto = getRecordDto();

            final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.COMPLIANT);
            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            handleLanguageCode();
            service.writeMetadata(bufOut, dcDto, docUnit, sums, CHECKSUM_METS, false);
            bufOut.flush();

            final String xmlResult = out.toString();
            basicChecks(xmlResult);
            bufOut.close();
        }
    }

    @Ignore
    @Test
    public void testWriteInvalidMetadata() throws IOException, PgcnTechnicalException, JAXBException, SAXException {
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {
            final BibliographicRecordDcDTO dcDto = new BibliographicRecordDcDTO();

            final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.NON_COMPLIANT_FULL);
            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();

            service.writeMetadata(bufOut, dcDto, docUnit, sums, CHECKSUM_METS, false);
            fail("testWriteInvalidMetadata failed");

        } catch (final ExportCinesException e) {
            // Ok
            assertEquals("Champ \"Title\" manquant", e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testWriteEmptyServiceVersant() throws JAXBException, IOException, SAXException, PgcnTechnicalException, ExportCinesException {
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {
            final BibliographicRecordDcDTO dcDto = getRecordDto();

            final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.COMPLIANT);
            final Library lib = docUnit.getLibrary();
            lib.setCinesService("");
            docUnit.setLibrary(lib);

            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            exception.expect(PgcnTechnicalException.class);
            service.writeMetadata(bufOut, dcDto, docUnit, sums, CHECKSUM_METS, false);
            bufOut.close();
        }
    }
    @Ignore
    @Test
    public void assertDefaultValuesAreSet() throws JAXBException, IOException, SAXException, PgcnTechnicalException, ExportCinesException {
        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {
            final BibliographicRecordDcDTO dcDto = getRecordDto();
            dcDto.getPublisher().clear();
            dcDto.getCreator().clear();

            final DocUnit docUnit = GenerateDocUnitUtil.getDocUnit(GenerateDocUnitUtilEnum.NON_COMPLIANT_AUTHOR_PUBLISHER);

            final List<CheckSummedStoredFile> sums = GenerateDocUnitUtil.getCheckSummedList();
            // Fake
            handleLanguageCode();
            handleLibraryParameter();

            // Call
            service.writeMetadata(bufOut, dcDto, docUnit, sums, CHECKSUM_METS, false);
            bufOut.flush();

            // Check
            final String xmlResult = out.toString();
            basicChecks(xmlResult);
            assertTrue(xmlResult.contains("<publisher>" + DEFAULT_PUBLISHER + "</publisher>"));
            assertTrue(xmlResult.contains("<creator>" + DEFAULT_CREATOR + "</creator>"));
            bufOut.close();
        }
    }

    private void basicChecks(final String xmlResult) {
        assertTrue(xmlResult.contains("<DocDC>"));
        assertTrue(xmlResult.contains("<DocMeta>"));
        assertTrue(xmlResult.contains("<serviceVersant>service versant</serviceVersant>"));
        assertTrue(xmlResult.contains("<nomFichier>DESC/mets.xml</nomFichier>"));
        assertTrue(xmlResult.contains(CHECKSUM_METS));
    }

    private void handleLanguageCode() {
        when(languageCodeService.getIso6393TForLanguage(any(String.class))).thenReturn("fra");
    }

    private void handleLibraryParameter() {
        final LibraryParameter libParam = new LibraryParameter();
        libParam.setType(LibraryParameterType.CINES_EXPORT);
        final LibraryParameterValueCines paramCinesCreator = new LibraryParameterValueCines();
        paramCinesCreator.setType(LibraryParameterValueCinesType.CREATOR_DEFAULT_VALUE);
        paramCinesCreator.setValue(DEFAULT_CREATOR);
        final LibraryParameterValueCines paramCinesPublisher = new LibraryParameterValueCines();
        paramCinesPublisher.setType(LibraryParameterValueCinesType.PUBLISHER_DEFAULT_VALUE);
        paramCinesPublisher.setValue(DEFAULT_PUBLISHER);
        libParam.addValue(paramCinesCreator);
        libParam.addValue(paramCinesPublisher);
        when(libraryParameterService.findCinesParameterForLibrary(any(Library.class))).thenReturn(libParam);
    }

    private BibliographicRecordDcDTO getRecordDto() {
        final BibliographicRecordDcDTO dto = new BibliographicRecordDcDTO();
        dto.getTitle().add("title");
        dto.getCreator().add("creator");
        dto.getSubject().add("subject");
        dto.getDescription().add("description");
        dto.getLanguage().add("lan");
        dto.getPublisher().add("publisher");
        dto.getDate().add("date");
        dto.getType().add("type");
        dto.getFormat().add("format");
        dto.getRights().add("rights");

        return dto;
    }
}
