package fr.progilone.pgcn.service.exchange.template;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.TPL_DUPLICATE;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.TPL_LIBRARY_MANDATORY;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.TPL_NAME_MANDATORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.exchange.template.Engine;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.exchange.template.TemplateRepository;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.util.TestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceTest {

    private static final String TPL_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    @Mock
    private FileStorageManager fm;
    @Mock
    private TemplateRepository templateRepository;

    private TemplateService service;

    @Before
    public void setUp() {
        service = new TemplateService(fm, templateRepository);
        ReflectionTestUtils.setField(service, "templateDir", TPL_DIR);
    }

    @AfterClass
    public static void clean() {
        FileUtils.deleteQuietly(new File(TPL_DIR));
    }

    @Test
    public void testFindTemplate1() {
        final List<Template> templates = new ArrayList<>();
        when(templateRepository.findAll()).thenReturn(templates);
        final List<Template> actual = service.findTemplate();
        assertSame(templates, actual);
    }

    @Test
    public void testFindTemplate2() {
        final List<Template> templates = new ArrayList<>();
        final Engine engine = Engine.Velocity;
        final Library library = new Library();

        when(templateRepository.findByLibrary(library)).thenReturn(templates);
        final List<Template> actual = service.findTemplate(library);
        assertSame(templates, actual);
    }

    @Test
    public void testFindTemplate3() {
        final List<Template> templates = new ArrayList<>();
        final Name name = Name.ReinitPassword;
        final Engine engine = Engine.Velocity;
        final Library library = new Library();
        library.setIdentifier("4663935f-4be4-4563-8204-eaf552585d18");

        when(templateRepository.findByNameAndLibraryIdentifier(name, library.getIdentifier())).thenReturn(templates);
        final List<Template> actual = service.findTemplate(name, library.getIdentifier());
        assertSame(templates, actual);
    }

    @Test
    public void testFindByIdentifier() {
        final String identifier = "42fc5db3-988e-4507-ad18-3e48f944c742";
        final Template template = new Template();

        when(templateRepository.findByIdentifier(identifier)).thenReturn(template);
        final Template actual = service.findByIdentifier(identifier);
        assertSame(template, actual);
    }

    @Test
    public void testDelete() throws IOException {
        final Template template = new Template();
        template.setIdentifier("42fc5db3-988e-4507-ad18-3e48f944c742");
        template.setOriginalFilename("testDelete");

        final File file = new File(TPL_DIR, "testDelete");
        FileUtils.writeStringToFile(file, "testDelete", StandardCharsets.UTF_8);

        when(templateRepository.findOne(template.getIdentifier())).thenReturn(template);
        when(fm.retrieveFile(anyString(), any(AbstractDomainObject.class))).thenReturn(file);

        service.delete(template.getIdentifier());

        assertFalse(file.exists());
        verify(templateRepository).delete(template);
    }

    @Test
    public void testSave() {
        final Library library = new Library();
        library.setIdentifier("6b84a31c-0654-4b37-ba0b-e1d4cb4b5fac");

        final Template template = new Template();
        final String identifier = "05d4fdf5-1dd8-4feb-95fa-eba3271c5ed1";

        when(templateRepository.save(template)).thenReturn(template);
        when(templateRepository.countByNameAndLibraryIdentifier(Name.ReinitPassword, library.getIdentifier())).thenReturn(1L, 0L);
        when(templateRepository.countByNameAndLibraryIdentifierAndIdentifierNot(Name.ReinitPassword, library.getIdentifier(), identifier)).thenReturn(
            1L,
            0L);

        // Validation KO, création
        try {
            service.save(template);
            fail("testSave should have failed");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, TPL_LIBRARY_MANDATORY, TPL_NAME_MANDATORY);
        }

        // Validation KO, création + doublon
        try {
            template.setLibrary(library);
            template.setName(Name.ReinitPassword);

            service.save(template);
            fail("testSave should have failed");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, TPL_DUPLICATE);
        }

        // Validation OK, création + doublon
        try {
            final Template actual = service.save(template);
            assertSame(template, actual);

        } catch (final PgcnValidationException e) {
            fail("unexpected failure");
        }

        // Validation KO, mise à jour + doublon
        try {
            template.setIdentifier(identifier);
            service.save(template);
            fail("testSave should have failed");
        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, TPL_DUPLICATE);
        }

        // Validation OK, mise à jour + doublon
        try {
            final Template actual = service.save(template);
            assertSame(template, actual);

        } catch (final PgcnValidationException e) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testDownload() throws IOException {
        final Template template = new Template();
        template.setIdentifier("42fc5db3-988e-4507-ad18-3e48f944c742");
        template.setOriginalFilename("testDownload");

        final String data = "Cras accumsan urna condimentum suscipit efficitur.";
        final File file = new File(TPL_DIR, "testDownload");
        FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);

        when(fm.retrieveFile(anyString(), any(AbstractDomainObject.class))).thenReturn(file);

        final InputStream actual = service.getContentStream(template);
        assertNotNull(actual);
        assertEquals(data, IOUtils.toString(actual, StandardCharsets.UTF_8));
    }

    @Test
    public void testUpload() throws IOException {
        final Template template = new Template();
        template.setIdentifier("42fc5db3-988e-4507-ad18-3e48f944c742");

        final String data = "Cras accumsan urna condimentum suscipit efficitur.";
        final MultipartFile file = new MockMultipartFile("testUpload", "testUpload", "text/plain", data.getBytes(StandardCharsets.UTF_8));
        final File impFile = new File("testUpload");

        when(templateRepository.findOne(template.getIdentifier())).thenReturn(template);
        when(templateRepository.save(template)).thenReturn(template);
        when(fm.copyInputStreamToFile(any(InputStream.class), any(File.class), anyString(), anyBoolean(), anyBoolean())).thenReturn(impFile);

        // upload du fichier
        final Template actualUploaded = service.save(template, file);
        assertNotNull(actualUploaded);
        assertEquals(file.getOriginalFilename(), template.getOriginalFilename());
        assertEquals(file.getSize(), template.getFileSize().longValue());
    }

}
