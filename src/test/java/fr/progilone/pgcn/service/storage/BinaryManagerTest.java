package fr.progilone.pgcn.service.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.service.administration.viewsformat.ViewsFormatConfigurationService;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import fr.progilone.pgcn.service.util.DeliveryProgressService;
import fr.progilone.pgcn.service.util.ImageUtils;

@RunWith(MockitoJUnitRunner.class)
public class BinaryManagerTest {

    @InjectMocks
    private BinaryStorageManager bm;

    @Mock
    private BinaryRepository binaryRepository;

    @Mock
    private ImageDispatcherService imageDispatcherService;

    @Mock
    private ImageMagickService imageMagickService;
    
    @Mock
    private DeliveryProgressService delivProgressService;
    
    @Mock
    private ViewsFormatConfigurationService formatConfigurationService;
    
    @Mock
    private UserRepository userRepository;
    
    @Autowired
    DefaultFileFormats defaultFileFormats;
    

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final String masterTestFilePage1Path = "src/test/resources/storage/test.jpg";
    private static final String masterTestFilePage2Path = "src/test/resources/storage/test2.jpg";
    private static final String ID_DD = "5c8f60456aefba845c8875e46";
    private static final String ID_PAGE1 = "ba45c8f60456a672e003a875e469d0eb";
    private static final String ID_PAGE1_DELETE = "a3fcd4-3298e-784f12c-003a875e46-9d0eb";
    private static final String ID_PAGE1_DELETE2 = "48ecd4-3298e-784f12c-00c3675e46-a8cf4";
    private static final String ID_PAGE2 = "efba845c8f546045e003a846649d0eb";
    
    private static final String FAKE_LIB_ID = "libId";;
    

    @Before
    public void setUp() {
        try {
            bm.initialize("C://Temp/bmTestPGCN/", 3, "MD5", new String[]{FAKE_LIB_ID});
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void assertFilesLength() {
    	final File filePage1 = new File(masterTestFilePage1Path);
        if (filePage1 == null || !filePage1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }
        final File filePage2 = new File(masterTestFilePage2Path);
        if (filePage2 == null || !filePage2.exists()) {
            fail("Unable to load " + masterTestFilePage2Path);
        }
        assertNotEquals(filePage2.length(), filePage1.length());
    }

    /**
     * Test core process
     */
    @Test
    public void storeAndRetrieve() {
    	// create main digital doc
    	final DigitalDocument dd = new DigitalDocument();
    	dd.setIdentifier(ID_DD);

    	// for each page (1 min, n max)
    	final DocPage page1 = createDocPage(ID_PAGE1, dd);
    	final File filePage1 = new File(masterTestFilePage1Path);
        if (filePage1 == null || !filePage1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }
        final DocPage page2 = createDocPage(ID_PAGE2, dd);
        final File filePage2 = new File(masterTestFilePage2Path);
        if (filePage2 == null || !filePage2.exists()) {
            fail("Unable to load " + masterTestFilePage2Path);
        }
        dd.addPage(page1);
    	dd.addPage(page2);

        // Store
    	//  Handle repository
    	final Map<String, StoredFile> generated = new HashMap<>();
    	when(binaryRepository.save(any(StoredFile.class))).thenAnswer(i -> {
    		final StoredFile response = (StoredFile) i.getArguments()[0];
    		if(response.getIdentifier() != null) return generated.get(response.getIdentifier());
    		response.setIdentifier(randomIdentifier());
    		generated.put(response.getIdentifier(), response);
    		return response;
    	});
    	when(binaryRepository.findOne(any(String.class))).thenAnswer(i -> {
    		final String identifier = (String) i.getArguments()[0];
    		return generated.get(identifier);
    	});
    	when(imageDispatcherService.createThumbnailDerived(any(String.class), any(File.class), any(File.class), 
    	                                                   any(ViewsFormatConfiguration.FileFormat.class), any(ViewsFormatConfiguration.class), any(Long[].class)))
			    .then(
    			i -> {
				    ImageUtils.createThumbnail(
						    (File) i.getArguments()[1],
						    (File) i.getArguments()[2],
						    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getWidthByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3]),
						    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getHeightByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3])
						    );
				    return 0;
			    }
	    );
    	
    	try {
            when(imageMagickService.getMetadatasOfFile(any(File.class), Matchers.eq(false))).thenReturn(Optional.empty());
        } catch (final PgcnTechnicalException e) {
            // nothing
        }
    	when(formatConfigurationService.getOneByLot(any(String.class))).thenReturn(getValidFormatConfiguration());
    	
    	final StoredFile storedPage1 = bm.createFromFileForPage(page1, filePage1, StoredFileType.MASTER, ViewsFormatConfiguration.FileFormat.MASTER, Optional.empty(), FAKE_LIB_ID);
    	bm.createFromFileForPage(page2, filePage2, StoredFileType.MASTER, ViewsFormatConfiguration.FileFormat.MASTER, Optional.empty(), FAKE_LIB_ID);

    	// Retrieve
    	//  Check from storedFile
    	final File retrievedMaster1 = bm.getFileForStoredFile(storedPage1, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for master 1", retrievedMaster1);
    	assertEquals(retrievedMaster1.length(), filePage1.length());

    	// Check with control
    	final Binary binaryMaster1 = bm.getBinary(storedPage1, FAKE_LIB_ID);
    	assertEquals(binaryMaster1.getLength(), filePage1.length());

    	// Check From Digital Document
    	dd.getPages().stream().forEach(page -> {
    		page.getFiles().stream().forEach(file -> {
    			// Master check
    			if(StoredFileType.MASTER.equals(file.getType())) {
    				if(file.getFilename().contains("2")) {
    					final File retrievedMaster2Test2 = bm.getFileForStoredFile(file, FAKE_LIB_ID);
    			    	assertNotNull("Stored file can't be find for master 2 in test 2", retrievedMaster2Test2);
    			    	assertEquals(retrievedMaster2Test2.length(), filePage2.length());
    				} else {
    					final File retrievedMaster1Test2 = bm.getFileForStoredFile(file, FAKE_LIB_ID);
    			    	assertNotNull("Stored file can't be find for master 1 in test 2", retrievedMaster1Test2);
    			    	assertEquals(retrievedMaster1Test2.length(), filePage1.length());
    				}
    			}
     		});
    	});
    }

    /**
     * Check multiple derived handle (based on format)
     */
    @Test
    public void storeAndRetriveMultipleDerived() {
    	// create main digital doc
    	final DigitalDocument dd = new DigitalDocument();
    	dd.setIdentifier(ID_DD);
    	
    	// format de fichiers
    	final ViewsFormatConfiguration.FileFormat format1 = ViewsFormatConfiguration.FileFormat.VIEW;
    	final ViewsFormatConfiguration.FileFormat format2 = ViewsFormatConfiguration.FileFormat.VIEW;

    	final DocPage page1 = createDocPage(ID_PAGE1, dd);
    	final File fileDerived1 = new File(masterTestFilePage1Path);
        if (fileDerived1 == null || !fileDerived1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }
        final File fileDerived2 = new File(masterTestFilePage2Path);
        if (fileDerived2 == null || !fileDerived2.exists()) {
            fail("Unable to load " + masterTestFilePage2Path);
        }

        dd.addPage(page1);

        //  Handle repository
    	final Map<String, StoredFile> generated = new HashMap<>();
        when(binaryRepository.save(any(StoredFile.class))).thenAnswer(i -> {
    		final StoredFile response = (StoredFile) i.getArguments()[0];
    		if(response.getIdentifier() != null) return generated.get(response.getIdentifier());
    		response.setIdentifier(randomIdentifier());
    		generated.put(response.getIdentifier(), response);
    		return response;
    	});
    	when(binaryRepository.findOne(any(String.class))).thenAnswer(i -> {
    		final String identifier = (String) i.getArguments()[0];
    		return generated.get(identifier);
    	});
        when(imageDispatcherService.createThumbnailDerived(any(String.class), any(File.class), any(File.class), 
                                                           any(ViewsFormatConfiguration.FileFormat.class), any(ViewsFormatConfiguration.class), any(Long[].class)))
                .then(
                        i -> {
                            ImageUtils.createThumbnail(
                                    (File) i.getArguments()[1],
                                    (File) i.getArguments()[2],
                                    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getWidthByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3]),
                                    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getHeightByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3])
                            );
                            return 0;
                        }
                );
        when(formatConfigurationService.getOneByLot(any(String.class))).thenReturn(getValidFormatConfiguration());
        try {
            when(imageMagickService.getMetadatasOfFile(any(File.class), Matchers.eq(false))).thenReturn(Optional.empty());
        } catch (final PgcnTechnicalException e) {
            // nothing
        }
    	final StoredFile storedPage1Derived1 = bm.createFromFileForPage(page1, fileDerived1, StoredFileType.DERIVED, format1, Optional.empty(), FAKE_LIB_ID);
    	final StoredFile storedPage1Derived2 = bm.createFromFileForPage(page1, fileDerived2, StoredFileType.DERIVED, format2, Optional.empty(), FAKE_LIB_ID);

    	//  Check from storedFile
    	final File retrievedDerived1 = bm.getFileForStoredFile(storedPage1Derived1, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for derived 1", retrievedDerived1);
    	assertEquals(retrievedDerived1.length(), fileDerived1.length());

    	final File retrievedDerived2 = bm.getFileForStoredFile(storedPage1Derived2, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for derived 2", retrievedDerived2);
    	assertEquals(retrievedDerived2.length(), fileDerived2.length());
    }

    @Test
    public void generateDerivedThumbnailForMaster() throws IOException, InterruptedException, ExecutionException {

    	// create main digital doc
    	final DigitalDocument dd = new DigitalDocument();
    	dd.setIdentifier(ID_DD);

    	final DocPage page1 = createDocPage(ID_PAGE1, dd);
    	final File filePage1 = new File(masterTestFilePage1Path);
        if (filePage1 == null || !filePage1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }
        dd.addPage(page1);

        // Store
    	//  Handle repository
    	final Map<String, StoredFile> generated = new HashMap<>();
    	when(binaryRepository.save(any(StoredFile.class))).thenAnswer(i -> {
    		final StoredFile response = (StoredFile) i.getArguments()[0];
    		if(response.getIdentifier() != null) return generated.get(response.getIdentifier());
    		response.setIdentifier(randomIdentifier());
    		generated.put(response.getIdentifier(), response);
    		return response;
    	});
    	when(binaryRepository.findOne(any(String.class))).thenAnswer(i -> {
    		final String identifier = (String) i.getArguments()[0];
    		return generated.get(identifier);
    	});
        when(imageDispatcherService.createThumbnailDerived(any(String.class), any(File.class), any(File.class), 
                                                           any(ViewsFormatConfiguration.FileFormat.class), any(ViewsFormatConfiguration.class), any(Long[].class)))
                .then(
                        i -> {
                            ImageUtils.createThumbnail(
                                    (File) i.getArguments()[1],
                                    (File) i.getArguments()[2],
                                    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getWidthByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3]),
                                    (int)((ViewsFormatConfiguration) i.getArguments()[4]).getHeightByFormat((ViewsFormatConfiguration.FileFormat)i.getArguments()[3])
                            );
                            return true;
                        }
                );
        when(formatConfigurationService.getOneByLot(any(String.class))).thenReturn(getValidFormatConfiguration());
        try {
            when(imageMagickService.getMetadatasOfFile(any(File.class), Matchers.eq(false))).thenReturn(Optional.empty());
        } catch (final PgcnTechnicalException e) {
            // nothing
        }
    	final StoredFile master = bm.createFromFileForPage(page1, filePage1, StoredFileType.MASTER, 
    	                                                   ViewsFormatConfiguration.FileFormat.MASTER, Optional.empty(), FAKE_LIB_ID);
    	//  Check from storedFile
    	final File retrievedMaster1 = bm.getFileForStoredFile(master, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for master 1", retrievedMaster1);
    	assertEquals(retrievedMaster1.length(), filePage1.length());
    	
    	// ajout de la config de format de fichier au master
    	master.setFormatConfiguration(getValidFormatConfiguration());

    	// Generate Derived files
    	final File derivedTmpFile = File.createTempFile("create_", ".tmp");
    	derivedTmpFile.deleteOnExit();
    	assertEquals(0L, derivedTmpFile.length());
        final File derivedTmpFile2 = File.createTempFile("create_", ".tmp");
        derivedTmpFile2.deleteOnExit();
        assertEquals(0L, derivedTmpFile2.length());

        CompletableFuture<Void> runAsync = CompletableFuture
        		.runAsync(() -> bm.generateDerivedThumbnailForMaster(master, page1, ViewsFormatConfiguration.FileFormat.THUMB, Collections.emptyMap(), 0, null, FAKE_LIB_ID), executorService);
        runAsync.get();
        runAsync = CompletableFuture
        		.runAsync(() -> bm.generateDerivedThumbnailForMaster(master, page1, ViewsFormatConfiguration.FileFormat.VIEW, Collections.emptyMap(), 0, null, FAKE_LIB_ID), executorService);
        runAsync.get();
        
        // Check results
        assertTrue(generated.size() == 3);
        final List<StoredFile> deriveds = generated.values().stream().filter(file -> StoredFileType.DERIVED.equals(file.getType())).collect(Collectors.toList());
        assertTrue(deriveds != null && deriveds.size() == 2);
        deriveds.forEach(derivedStoredFile -> {
        	final File retrievedFile = bm.getFileForStoredFile(derivedStoredFile, FAKE_LIB_ID);
        	assertNotNull("Stored file can't be find for derived", retrievedFile);
        	try {
				final BufferedImage bi = ImageIO.read(retrievedFile);
				
				final Long heightSf = derivedStoredFile.getFormatConfiguration().getHeightByFormat(derivedStoredFile.getFileFormat());
				assertTrue(heightSf.intValue() >= bi.getHeight());
				final Long widthSf = derivedStoredFile.getFormatConfiguration().getWidthByFormat(derivedStoredFile.getFileFormat());
				assertTrue(widthSf.intValue() >= bi.getWidth());
			} catch (final IOException e) {
				fail("Image check failed");
			}
        });
    }

    /**
     * Test delete file from storedFile
     */
    @Test
    public void deleteFileFromStoredFile() {
    	// create main digital doc
    	final DigitalDocument dd = new DigitalDocument();
    	dd.setIdentifier(ID_DD);

    	final DocPage page1 = createDocPage(ID_PAGE1_DELETE, dd);
    	final File fileDerived1 = new File(masterTestFilePage1Path);
        if (fileDerived1 == null || !fileDerived1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }

        dd.addPage(page1);

        //  Handle repository
    	final Map<String, StoredFile> generated = new HashMap<>();
        when(binaryRepository.save(any(StoredFile.class))).thenAnswer(i -> {
    		final StoredFile response = (StoredFile) i.getArguments()[0];
    		if(response.getIdentifier() != null) return generated.get(response.getIdentifier());
    		response.setIdentifier(randomIdentifier());
    		generated.put(response.getIdentifier(), response);
    		return response;
    	});
    	when(binaryRepository.findOne(any(String.class))).thenAnswer(i -> {
    		final String identifier = (String) i.getArguments()[0];
    		return generated.get(identifier);
    	});
    	when(formatConfigurationService.getOneByLot(any(String.class))).thenReturn(getValidFormatConfiguration());
    	try {
            when(imageMagickService.getMetadatasOfFile(any(File.class), Matchers.eq(false))).thenReturn(Optional.empty());
        } catch (final PgcnTechnicalException e) {
            // nothing
        }
    	final StoredFile storedPage1 = bm.createFromFileForPage(page1, fileDerived1, StoredFileType.DERIVED, 
    	                                                        ViewsFormatConfiguration.FileFormat.VIEW, Optional.empty(), FAKE_LIB_ID);

    	//  Check from storedFile
    	final File retrieved = bm.getFileForStoredFile(storedPage1, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for derived 1", retrieved);
    	assertEquals(retrieved.length(), fileDerived1.length());

    	// Delete and check
    	bm.deleteFileFromStoredFile(storedPage1, FAKE_LIB_ID);
    	final File retrievedMissing = bm.getFileForStoredFile(storedPage1, FAKE_LIB_ID);
    	assertFalse("Stored file should be deleted", retrievedMissing.exists());
    }

    /**
     * Check multiple derived handle (based on format)
     * @throws IOException
     */
    @Test
    public void deleteAllFilesFromPage() throws IOException {
    	// create main digital doc
    	final DigitalDocument dd = new DigitalDocument();
    	dd.setIdentifier(ID_DD);

    	final DocPage page1 = createDocPage(ID_PAGE1_DELETE2, dd);
    	final File fileDerived1 = new File(masterTestFilePage1Path);
        if (fileDerived1 == null || !fileDerived1.exists()) {
            fail("Unable to load " + masterTestFilePage1Path);
        }
        final File fileDerived2 = new File(masterTestFilePage2Path);
        if (fileDerived2 == null || !fileDerived2.exists()) {
            fail("Unable to load " + masterTestFilePage2Path);
        }

        dd.addPage(page1);

        //  Handle repository
    	final Map<String, StoredFile> generated = new HashMap<>();
        when(binaryRepository.save(any(StoredFile.class))).thenAnswer(i -> {
    		final StoredFile response = (StoredFile) i.getArguments()[0];
    		if(response.getIdentifier() != null) return generated.get(response.getIdentifier());
    		response.setIdentifier(randomIdentifier());
    		generated.put(response.getIdentifier(), response);
    		return response;
    	});
    	when(binaryRepository.findOne(any(String.class))).thenAnswer(i -> {
    		final String identifier = (String) i.getArguments()[0];
    		return generated.get(identifier);
    	});
    	when(formatConfigurationService.getOneByLot(any(String.class))).thenReturn(getValidFormatConfiguration());
    	try {
            when(imageMagickService.getMetadatasOfFile(any(File.class), Matchers.eq(false))).thenReturn(Optional.empty());
        } catch (final PgcnTechnicalException e) {
            // nothing
        }
    	final StoredFile storedPage1Derived1 = bm.createFromFileForPage(page1, fileDerived1, StoredFileType.DERIVED, 
    	                                                                ViewsFormatConfiguration.FileFormat.VIEW, Optional.empty(), FAKE_LIB_ID);
    	final StoredFile storedPage1Derived2 = bm.createFromFileForPage(page1, fileDerived2, StoredFileType.DERIVED, 
    	                                                                ViewsFormatConfiguration.FileFormat.VIEW, Optional.empty(), FAKE_LIB_ID);

    	//  Check from storedFile
    	final File retrievedDerived1 = bm.getFileForStoredFile(storedPage1Derived1, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for derived 1", retrievedDerived1);
    	assertEquals(retrievedDerived1.length(), fileDerived1.length());

    	final File retrievedDerived2 = bm.getFileForStoredFile(storedPage1Derived2, FAKE_LIB_ID);
    	assertNotNull("Stored file can't be find for derived 2", retrievedDerived2);
    	assertEquals(retrievedDerived2.length(), fileDerived2.length());

    	// Delete and check
    	bm.deleteAllFilesFromPage(page1, FAKE_LIB_ID);
    	final File retrievedMissing = bm.getFileForStoredFile(storedPage1Derived1, FAKE_LIB_ID);
    	assertFalse("Stored file should be deleted", retrievedMissing.exists());
    	final File retrievedMissing2 = bm.getFileForStoredFile(storedPage1Derived2, FAKE_LIB_ID);
    	assertFalse("Stored file should be deleted", retrievedMissing2.exists());
    }

    private String randomIdentifier() {
    	return UUID.randomUUID().toString();
    }
    
    private ViewsFormatConfiguration getValidFormatConfiguration() {
        final ViewsFormatConfiguration conf = new ViewsFormatConfiguration();
        conf.setDefaultFormats(defaultFileFormats);
        conf.setThumbHeight(100L);
        conf.setThumbWidth(100L);
        conf.setViewHeight(1200L);
        conf.setViewWidth(800L);
        conf.setPrintHeight(2400L);
        conf.setPrintWidth(1600L);
        return conf; 
    }
    
    private DocPage createDocPage(final String identifier, final DigitalDocument dd) {
        final DocPage page = new DocPage();
        page.setIdentifier(identifier);
        final Lot lot = new Lot();
        lot.setIdentifier(randomIdentifier());
        final DocUnit du = new DocUnit();
        du.setLot(lot);
        dd.setDocUnit(du);
        page.setDigitalDocument(dd);
        return page;
    }
}
