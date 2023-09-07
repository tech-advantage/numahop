package fr.progilone.pgcn.service.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileStorageManagerTest {

    public static final String TEST_FILE = "test.txt";
    private static final String TEST_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    private static final String DATAS = "Donec efficitur elit gravida arcu tristique, vel ullamcorper nunc finibus. "
                                        + "Ut id quam ultricies ipsum porta volutpat sit amet nec odio.";

    @Mock
    private ImageDispatcherService imageDispatcherService;

    @Mock
    private DefaultFileFormats defautFileFormats;

    @Mock
    private UserRepository userRepository;

    private FileStorageManager service;

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(TEST_DIR));
    }

    @AfterAll
    public static void clean() {
        FileUtils.deleteQuietly(new File(TEST_DIR));
    }

    @BeforeEach
    public void setUp() {
        service = new FileStorageManager(imageDispatcherService, defautFileFormats, userRepository);
    }

    @Test
    public void testCreateFileFromInputStream() throws IOException {

        final String TEST_DIR_LIB = TEST_DIR;

        final ByteArrayInputStream in = new ByteArrayInputStream(DATAS.getBytes(StandardCharsets.UTF_8));
        service.copyInputStreamToFile(in, new File(TEST_DIR), TEST_FILE, true, false);

        assertTrue(Files.exists(Paths.get(TEST_DIR_LIB, TEST_FILE)));
        final String actualRead = FileUtils.readFileToString(new File(TEST_DIR_LIB, TEST_FILE), StandardCharsets.UTF_8);
        assertEquals(DATAS, actualRead);
    }

    @Test
    public void testCreateFileFromInputStreamWithMoreDirs() throws IOException {
        final User user = getUserWithLib();
        final String TEST_DIR_LIB = TEST_DIR + "/fakeLib/dir1/dir2";
        when(userRepository.findOneWithLibrary(isNull())).thenReturn(user);

        final ByteArrayInputStream in = new ByteArrayInputStream(DATAS.getBytes(StandardCharsets.UTF_8));
        service.copyInputStreamToFileWithOtherDirs(in, new File(TEST_DIR), Arrays.asList("dir1", "dir2"), TEST_FILE, true, true);

        assertTrue(Files.exists(Paths.get(TEST_DIR_LIB, TEST_FILE)));
        final String actualRead = FileUtils.readFileToString(new File(TEST_DIR_LIB, TEST_FILE), StandardCharsets.UTF_8);
        assertEquals(DATAS, actualRead);
    }

    @Test
    public void testRetrieveFile() throws IOException {
        final String data = "Morbi aliquet, massa eget elementum mattis, elit ex fermentum elit, et lobortis velit nulla nec turpis. "
                            + "Integer mollis quam neque, nec convallis risus euismod eget.";
        FileUtils.writeStringToFile(new File(TEST_DIR, TEST_FILE), data, StandardCharsets.UTF_8);

        File actual = service.retrieveFile(new File(TEST_DIR), "unknown_file.txt");
        assertNull(actual);

        actual = service.retrieveFile((File) null, null);
        assertNull(actual);

        actual = service.retrieveFile(new File(TEST_DIR), TEST_FILE);
        assertNotNull(actual);
    }

    private User getUserWithLib() {
        final User user = new User();
        final Library lib = new Library();
        lib.setIdentifier("fakeLib");
        user.setLibrary(lib);
        return user;
    }
}
