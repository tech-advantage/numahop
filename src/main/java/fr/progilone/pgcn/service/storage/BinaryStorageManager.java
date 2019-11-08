package fr.progilone.pgcn.service.storage;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile.StoredFileType;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.administration.viewsformat.ViewsFormatConfigurationService;
import fr.progilone.pgcn.service.util.DeliveryProgressService;

@Service
public class BinaryStorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryStorageManager.class);

    public static final int MIN_BUF_SIZE = 8 * 1024; // 8 kB

    public static final int MAX_BUF_SIZE = 64 * 1024; // 64 kB

    public static final String DEFAULT_PATH = "binaries"; // default not mean to be used

    public static final String FILE_PATH = "files";

    public static final String MASTER_PATH = "master";

    public static final String DERIVED_PATH = "derived";

    public static final String DATA = "data";

    public static final String TMP = "tmp";

    public static final String EXTENSION_JPG = ".jpg";
    
    protected Map<String, Map<String,File>> storageInfos = new HashMap<>();

    protected File storageDir;

    protected File tmpDir;

    protected File filesStorageDir;

    protected int depth;

    private final BinaryRepository binaryRepository;
    private final ImageDispatcherService imageDispatcherService;
    private final ImageMagickService imageMagickService;
    private final ExifToolService exifToolService;
    private final DeliveryProgressService deliveryProgressService;
    private final ViewsFormatConfigurationService formatConfigurationService;
    private final UserRepository userRepository;
 

    @Autowired
    public BinaryStorageManager(final BinaryRepository binaryRepository,
                                final ImageDispatcherService imageDispatcherService,
                                final ImageMagickService imageMagickService,
                                final ExifToolService exifToolService,
                                final DeliveryProgressService deliveryProgressService,
                                final ViewsFormatConfigurationService formatConfigurationService,
                                final UserRepository userRepository) {
        this.binaryRepository = binaryRepository;
        this.imageDispatcherService = imageDispatcherService;
        this.imageMagickService = imageMagickService;
        this.exifToolService = exifToolService;
        this.deliveryProgressService = deliveryProgressService;
        this.formatConfigurationService = formatConfigurationService;
        this.userRepository = userRepository;
    }

    public void initialize(final String binaries, final int depth, final String digest, final String[] instanceLibraries) throws IOException {
        
        final String path;
        if (binaries == null || binaries.trim().length() == 0) {
            path = (FileUtils.getUserDirectoryPath() + "/pgcn/" + DEFAULT_PATH).trim();
            LOG.warn("Default path is used for binaries : {}", path);
        } else {
            path = binaries.trim();
        }

        final File base;
        if (path.startsWith("/") || path.startsWith("\\") || path.contains("://") || path.contains(":\\")) {
            // absolute
            base = new File(path);
        } else {
            // relative
            final File home = FileUtils.getUserDirectory();
            base = new File(home, path);
        }

        this.depth = depth;
        
        // 1 disk space per library 
        Arrays.asList(instanceLibraries).forEach(lib -> {
            final Map<String, File> files = new HashMap<>();
            final File libBase = new File(base, lib);
            storageDir = new File(libBase, DATA);
            tmpDir = new File(libBase, TMP);
            filesStorageDir = new File(storageDir, FILE_PATH);
            files.put("storageDir", storageDir);
            files.put("tmpDir", tmpDir);
            files.put("filesStorageDir", filesStorageDir);
            
            storageInfos.put(lib, files);
            // create directories if necessary
            storageDir.mkdirs();
            tmpDir.mkdirs();
            filesStorageDir.mkdirs();
            
            LOG.info("LIBRARY {} : Binaries storage using {} for data and {} for temporary data. Binary global store: {}", lib, storageDir, tmpDir, libBase);
        });
    }

    /**
     * Persist a storedFile
     *
     * @param storedFile
     * @return the persisted storedFile
     * @throws PgcnException
     */
    @Transactional
    public StoredFile create(final StoredFile storedFile) throws PgcnException {
        StoredFile savedStoredFile = binaryRepository.save(storedFile);
        savedStoredFile = binaryRepository.findOne(savedStoredFile.getIdentifier());

        LOG.debug("Created binary stored file: {}", savedStoredFile);
        return savedStoredFile;
    }


    @Transactional
    public StoredFile createFromFileForPage(final DocPage page,
                                            final File file,
                                            final StoredFileType type,
                                            final ViewsFormatConfiguration.FileFormat format,
                                            final Optional<Map<Integer, String>> ocrByPage,
                                            final String libraryId) throws PgcnException {

        return createFromFileForPage(page, file, type, format, ocrByPage, null, libraryId);
    }
    
    public Optional<Map<String, String>> getMetadatas(final File file) throws PgcnTechnicalException {
        Optional<Map<String, String>> metas = Optional.empty();
        final String format = FilenameUtils.getExtension(file.getName()).toUpperCase();
        switch (format.toUpperCase()) {
            case "JPEG":
            case "JPG":
            case "TIF":
            case "TIFF":
            case "GIF":
            case "SVG":
            case "PNG":
            case "PDF":
                metas = imageMagickService.getMetadatasOfFile(file, format.toUpperCase().equals("PDF"));
                break;
            case "JP2":
                metas = exifToolService.extractMetadatas(file);
                break;
            default:
                break;
        }
        return metas;
    }

    /**
     * Create a storedFile <br/>
     * ** store with digest the file <br/>
     * ** process to persist the storedFile <br/>
     * ** return it
     *
     * @param page
     * @param file
     * @param type
     *         MASTER ou DERIVED
     * @return
     * @throws PgcnException
     */
    @Transactional
    public StoredFile createFromFileForPage(final DocPage page,
                                            final File file,
                                            final StoredFileType type,
                                            final ViewsFormatConfiguration.FileFormat format,
                                            final Optional<Map<Integer, String>> ocrByPage,
                                            final Map<File, Optional<Map<String,String>>> fileMetadatas,
                                            final String libraryId) throws PgcnException {


        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        if (page == null || file == null) {
            errors.add(builder.reinit().setMessage("Page and File are required").build());
            throw new PgcnException(errors);
        }
        LOG.debug("Handled FileName : {}", file.getName());

        final StoredFile storedFile = new StoredFile();
        // Set base info
        storedFile.setFilename(file.getName());
        storedFile.setLength(file.length());
        storedFile.setPage(page);
        storedFile.setType(type);

        final ViewsFormatConfiguration conf = formatConfigurationService.getOneByLot(page.getDigitalDocument().getDocUnit().getLot().getIdentifier());
        storedFile.setFileFormat(format);
        storedFile.setFormatConfiguration(conf);

        // Handle digest : tied to parent
        storedFile.setPageDigest(identifierToDigest(page.getIdentifier()));

        // store & save the binary
        try {
            final String mimeType = new Tika().detect(file);
            storedFile.setMimetype(mimeType);

            if (! ViewsFormatConfiguration.FileFormat.MASTER.equals(format)) {
                // DERIVES
                // set dimensions
                final Optional<Dimension> dim = getImgDimension(file, Optional.of(mimeType));
                if (dim.isPresent()) {
                    storedFile.setWidth((long) dim.get().getWidth());
                    storedFile.setHeight((long) dim.get().getHeight());
                }
            } else {
                // MASTERS
                Optional<Map<String, String>> meta;
                // get metadatas of masters.
                if (fileMetadatas == null) {
                    try {
                        meta = getMetadatas(file);
                    } catch (final PgcnTechnicalException e) {
                        LOG.error("Can't collect metadatas of file: {} - ", file.getName(), e);
                        errors.add(builder.reinit().setMessage("Can't collect metadatas of file").build());
                        throw new PgcnException(errors);
                    }
                } else {
                    // get metadatas of masters from big map.
                    meta = fileMetadatas.entrySet().stream()
                                .filter((entry)->StringUtils.equals(entry.getKey().getName(), file.getName()))
                                .map(entry->entry.getValue().get())
                                .findFirst();
                }

                if (meta.isPresent()) {
                    setMetadatas(meta.get(), storedFile);
                }
                // text d'OCR s'il existe.
                if ( ocrByPage.isPresent()) {
                    if ( ocrByPage.get().containsKey(page.getNumber())
                            && StringUtils.isNotBlank(ocrByPage.get().get(page.getNumber())) ) {
                        storedFile.setTextOcr(ocrByPage.get().get(page.getNumber()));
                    }
                }
            }

            final StoredFile savedStoredFile = create(storedFile);
            storeWithDigest(new FileInputStream(file), savedStoredFile, libraryId);
            LOG.info("{} saved sucessfully", savedStoredFile.getFilename());

            return savedStoredFile;

        } catch (final IOException e) {
            LOG.error("Can't store the file :", e);
            errors.add(builder.reinit().setMessage("Unable to store the file").build());
            throw new PgcnException(errors);
        }
    }

    /**
     * Renseigne les metadonnees du storedfile de type master.
     *
     * @param metas
     * @param storedFile
     */
    private void setMetadatas(final Map<String, String> metas, final StoredFile storedFile) {
        // Compression | Quality | Geometry | Colorspace
        final Long dims[] = getDimensionsFromMeta(metas);
        if (dims != null) {
            storedFile.setWidth(dims[0]);
            storedFile.setHeight(dims[1]);
        }
        if (StringUtils.equalsIgnoreCase("None", metas.get("Compression")) ) {
            storedFile.setCompressionType("Non compressé");
        } else if (StringUtils.equalsIgnoreCase("Undefined", metas.get("Compression"))) {
            storedFile.setCompressionType("Non renseigné");
        } else {
            storedFile.setCompressionType(metas.get("Compression"));
        }
        if (NumberUtils.isParsable(metas.get("Quality"))) {
            storedFile.setCompressionRate(Integer.valueOf(metas.get("Quality")));  // en fait, bit depth (8) 
        }
        storedFile.setResolution(getResolutionFromMeta(metas));
        storedFile.setColorspace(metas.get("Colorspace"));
    }

    /**
     * Retourne les dimensions sous forme [widthL, heightL].
     *
     * @param metas
     * @return
     */
    private Long[] getDimensionsFromMeta(final Map<String, String> metas) {

        String width = "0";
        String height = "0";

        if (StringUtils.isNotBlank(metas.get("width")) && NumberUtils.isParsable(metas.get("width"))
                && StringUtils.isNotBlank(metas.get("height")) && NumberUtils.isParsable(metas.get("height")) ) {
            width = metas.get("width");
            height = metas.get("height");
        }
        
        final Long dims[] = {Long.valueOf(width), Long.valueOf(height)};
        return dims;
    }

    /**
     * Retourne la résolution indiquée ds les métadonnées.
     *
     * @param metas
     * @return
     */
    private Integer getResolutionFromMeta(final Map<String, String> metas) {

        final String[] vals = StringUtils.split(StringUtils.trimToEmpty(metas.get("Resolution")), "x", 2);
        Integer fileRes = 0;
        if (vals != null && vals.length > 0 && StringUtils.isNotBlank(vals[0])) {
            try {
                final Long rounded =  Math.round(Double.valueOf(vals[0]));
                fileRes = rounded.intValue();
            } catch(final NumberFormatException e) {
                LOG.error("Invalid resolution in metadatas");
                fileRes = 0;
            }
        }
        return fileRes;
    }

    @Transactional
    @Async
    public void generateDerivedThumbnailForMaster(final StoredFile master,
                                                  final DocPage page,
                                                  final ViewsFormatConfiguration.FileFormat format,
                                                  final Map<File, Optional<Map<String,String>>> fileMetadatas,
                                                  final double progress, final Delivery delivery, final String libraryId) throws PgcnException {

        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        if (page == null || master == null || format == null) {
            errors.add(builder.reinit().setMessage("Page, master and format are required").build());
            throw new PgcnException(errors);
        }
        LOG.debug("Generate Derived of type {}", format.label());
        final int progression = Double.valueOf(progress).intValue();

        final StoredFile generated = new StoredFile();
        // Set base info
        generated.setFilename(master.getFilename());
        generated.setPage(page);
        generated.setType(StoredFileType.DERIVED);
        generated.setFileFormat(format);
        generated.setFormatConfiguration(master.getFormatConfiguration());

        // Handle digest : tied to parent
        generated.setPageDigest(identifierToDigest(page.getIdentifier()));

        // Get master File and dimensions.
        final File masterFile = getFileForStoredFile(master, libraryId);
        final Optional<Map<String, String>> meta = fileMetadatas.entrySet().stream()
                    .filter((entry)->StringUtils.equals(entry.getKey().getName(), master.getFilename()))
                    .map(entry->entry.getValue().get())
                    .findFirst();
        final Long[] masterDims = meta.isPresent()?getDimensionsFromMeta(meta.get()):new Long[] {0L, 0L};

        // generate, store & save the binary
        try {
            // Temp file for generation
            final File thumbnailTmp = File.createTempFile("create_", EXTENSION_JPG, getTmpDir(libraryId));

            final boolean generationResult = imageDispatcherService.createThumbnailDerived(master.getMimetype(), masterFile, thumbnailTmp, format, master.getFormatConfiguration(), masterDims);
            if (generationResult && thumbnailTmp.length() > 0L) {
                // store the file and set required parameters
                generated.setLength(thumbnailTmp.length());

                final String mimeType = new Tika().detect(thumbnailTmp);
                generated.setMimetype(mimeType);
                // set dimensions
                final Optional<Dimension> dim = getImgDimension(thumbnailTmp, Optional.of(mimeType));
                if (dim.isPresent()) {
                    generated.setWidth((long) dim.get().getWidth());
                    generated.setHeight((long) dim.get().getHeight());
                }

                final StoredFile savedStoredFile = create(generated);
                storeWithDigest(new FileInputStream(thumbnailTmp), savedStoredFile, libraryId);
                LOG.info("{} saved sucessfully", savedStoredFile.getFilename());

                if (progression > 0d && StringUtils.equalsIgnoreCase("Zoom", format.label())) {
                    deliveryProgressService.deliveryProgress(delivery, page.getDigitalDocument().getDigitalId(), "INFO", "DELIVERING", progression, "", true);
                }

            } else {
                LOG.info("Generation failed. No file has been saved for format : {}", format.toString());
            }
        } catch (final IOException e) {
            LOG.error("Can't store the file: ", e);
            errors.add(builder.reinit().setMessage("Unable to store the file").build());
            throw new PgcnException(errors);
        }

    }

    @Transactional
    public void generateDerivedThumbnailForPage(final DocPage page,
                                                final StoredFile master,
                                                final ViewsFormatConfiguration.FileFormat format,
                                                final Map<File, Optional<Map<String,String>>> fileMetadatas,
                                                final double progress, final Delivery delivery, final String libraryId) throws PgcnException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        if (page == null) {
            errors.add(builder.reinit().setMessage("Page is required").build());
            throw new PgcnException(errors);
        }
        if (master == null) {
            errors.add(builder.reinit().setMessage("Master not found").build());
            throw new PgcnException(errors);
        }
        generateDerivedThumbnailForMaster(master, page, format, fileMetadatas, progress, delivery, libraryId);
    }


    /**
     * Retourne les dimensions de l'image.
     *
     * @param file
     * @param mimeType
     * @return
     */
    public Optional<Dimension> getImgDimension(final File file, final Optional<String> mimeType) {

        // On evite ImageIO.read car + couteux (lecture de tout le fichier)...
        Dimension dim = null;
        String typeMime;
        if (mimeType.isPresent()) {
            typeMime = mimeType.get();
        } else {
            try {
                typeMime = new Tika().detect(file);
            } catch (final IOException e) {
                LOG.error("Can't detect mimeType of file {}", file);
                return Optional.empty();
            }
        }
        // ne gere pas le jp2, mais comme on ne fait pas les masters...
        if (StringUtils.equalsIgnoreCase("image/jp2", typeMime)) {
            return Optional.empty();
        }

        final Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType(typeMime);
        if (it.hasNext()) {
            final ImageReader reader = it.next();
            try {
                final ImageInputStream stream = new FileImageInputStream(file);
                reader.setInput(stream);
                final int width = reader.getWidth(reader.getMinIndex());
                final int height = reader.getHeight(reader.getMinIndex());
                dim = new Dimension(width, height);
            } catch (final IOException e) {
                LOG.error(e.getMessage());
            } finally {
                reader.dispose();
            }
        } else {
            LOG.warn("No reader found for given mimeType: {}", mimeType);
        }
        return Optional.of(dim);
    }


    /**
     * Retrieve a master or a derived for given storedFile.
     *
     * @param storedFile
     * @return
     */
    public File getFileForStoredFile(final StoredFile storedFile) {
        return getFileForStoredFile(storedFile, false, getUserLibraryId());
    }
    
    public File getFileForStoredFile(final StoredFile storedFile, final String libraryId) {
        return getFileForStoredFile(storedFile, false, libraryId);
    }

    private File getFileForStoredFile(final StoredFile storedFile, final boolean createDir, final String libraryId) {
        
        final String digest = storedFile.getPageDigest();
        if (digest.length() < 2 * depth) {
            return null;
        }
        final StringBuilder buf = new StringBuilder(3 * depth - 1);
        for (int i = 0; i < depth; i++) {
            if (i != 0) {
                buf.append(File.separatorChar);
            }
            buf.append(digest.substring(2 * i, 2 * i + 2));
        }
        
        final File dir = new File(getFilesStorageDir(libraryId), buf.toString());
        final File digestDir = new File(dir, digest);
        if (createDir) {
            digestDir.mkdirs();
            // create derived path
            final File derived = new File(digestDir, DERIVED_PATH);
            derived.mkdir();
            // create master path
            final File master = new File(digestDir, MASTER_PATH);
            master.mkdir();
        }
        String path = "";
        switch (storedFile.getType()) {
            case DERIVED:
                path = DERIVED_PATH;
                break;
            case MASTER:
                path = MASTER_PATH;
                break;
            default:
                break;
        }
        return FileUtils.getFile(digestDir, path, storedFile.getDigest());
    }
    
    public File getTmpDir(final String library) {
        final Map<String, File> infos = storageInfos.get(library);
        if (infos != null) {
            return infos.get("tmpDir");
        } else throw new RuntimeException("Bibliotheque non autorisee!");
    }
    
    public File getStorageDir(final String library) {
        final Map<String, File> infos = storageInfos.get(library);
        if (infos != null) {
            return infos.get("storageDir");
        } else throw new RuntimeException("Bibliotheque non autorisee!");    
    }
    
    private File getFilesStorageDir(final String library) {
        final Map<String, File> infos = storageInfos.get(library);
        if (infos != null) {
            return infos.get("filesStorageDir");
        } else throw new RuntimeException("Bibliotheque non autorisee!");    
    }
    
    private String getUserLibraryId() {
        final User user = userRepository.findOneWithLibrary(SecurityUtils.getCurrentUserId());
        return user.getLibrary().getIdentifier();
    }

    public void deleteAllFilesFromPage(final DocPage page, final String libraryId) throws IOException {
        final String digest = identifierToDigest(page.getIdentifier());
        if (digest == null || digest.length() < 2 * depth) {
            return;
        }
        final StringBuilder buf = new StringBuilder(3 * depth - 1);
        for (int i = 0; i < depth; i++) {
            if (i != 0) {
                buf.append(File.separatorChar);
            }
            buf.append(digest.substring(2 * i, 2 * i + 2));
        }
        
        final File dir = new File(getFilesStorageDir(libraryId), buf.toString());
        final File digestDir = new File(dir, digest);
        FileUtils.deleteDirectory(digestDir);
    }

    public void deleteFileFromStoredFile(final StoredFile storedFile, final String libraryId) {
        final String digest = storedFile.getPageDigest();
        if (digest.length() < 2 * depth) {
            return;
        }
        final StringBuilder buf = new StringBuilder(3 * depth - 1);
        for (int i = 0; i < depth; i++) {
            if (i != 0) {
                buf.append(File.separatorChar);
            }
            buf.append(digest.substring(2 * i, 2 * i + 2));
        }
        final File dir = new File(getFilesStorageDir(libraryId), buf.toString());
        final File digestDir = new File(dir, digest);
        String path = "";
        switch (storedFile.getType()) {
            case DERIVED:
                path = DERIVED_PATH;
                break;
            case MASTER:
                path = MASTER_PATH;
                break;
            default:
                break;
        }
        final File fileDir = new File(digestDir, path);
        final File digestedFile = new File(fileDir, storedFile.getDigest());
        FileUtils.deleteQuietly(digestedFile);
    }

    public Binary getBinary(final StoredFile storedFile, final String libraryId) {
        final File file = getFileForStoredFile(storedFile, libraryId);
        if (file == null) {
            // invalid digest or storedFile
            return null;
        }
        if (!file.exists()) {
            LOG.warn("cannot fetch content at {} (file does not exist), check your configuration", file.getPath());
            return null;
        }
        return getBinaryScrambler().getUnscrambledBinary(file, storedFile.getDigest());
    }

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    protected static String toHexString(final byte[] data) {
        final StringBuilder buf = new StringBuilder(2 * data.length);
        for (final byte b : data) {
            buf.append(HEX_DIGITS[(0xF0 & b) >> 4]);
            buf.append(HEX_DIGITS[0x0F & b]);
        }
        return buf.toString();
    }

    protected static String identifierToDigest(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return StringUtils.lowerCase(identifier.replaceAll("[^A-Za-z0-9]+", ""));
    }

    protected void storeWithDigest(final InputStream in, final StoredFile storedFile, final String libraryId) throws IOException {
        
        final File tmp = File.createTempFile("create_", ".tmp", getTmpDir(libraryId));
        /*
         * First, write the input stream to a temporary file, while computing a
         * digest.
         */
        try (final OutputStream out = new FileOutputStream(tmp)) {
            store(in, out);
            /*
             * Move the tmp file to its destination.
             */
            final File file = getFileForStoredFile(storedFile, true, libraryId);
            atomicMove(tmp, file);
        } finally {
            in.close();
            FileUtils.deleteQuietly(tmp);
        }
    }

    protected void store(final InputStream in, final OutputStream out) throws IOException {

        int size = in.available();
        if (size == 0) {
            size = MAX_BUF_SIZE;
        } else if (size < MIN_BUF_SIZE) {
            size = MIN_BUF_SIZE;
        } else if (size > MAX_BUF_SIZE) {
            size = MAX_BUF_SIZE;
        }
        final byte[] buf = new byte[size];

        /*
         * Scramble, copy and digest.
         */
        final BinaryScrambler scrambler = getBinaryScrambler();
        int n;
        while ((n = in.read(buf)) != -1) {
            scrambler.scrambleBuffer(buf, 0, n);
            out.write(buf, 0, n);
        }
        out.flush();
    }

    /**
     * Does an atomic move of the tmp (or source) file to the final file.
     * <p>
     * Tries to work well with NFS mounts and different filesystems.
     */
    protected void atomicMove(final File source, final File dest) throws IOException {
        if (dest.exists()) {
            // The file with the proper digest is already there so don't do
            // anything. This is to avoid "Stale NFS File Handle" problems
            // which would occur if we tried to overwrite it anyway.
            // Note that this doesn't try to protect from the case where
            // two identical files are uploaded at the same time.
            // Update date for the GC.
            dest.setLastModified(source.lastModified());
            return;
        }
        if (!source.renameTo(dest)) {
            // Failed to rename, probably a different filesystem.
            // Do *NOT* use Apache Commons IO's FileUtils.moveFile()
            // because it rewrites the destination file so is not atomic.
            // Do a copy through a tmp file on the same filesystem then
            // atomic rename.
            final File tmp = File.createTempFile(dest.getName(), ".tmp", dest.getParentFile());
            try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(tmp)) {
                IOUtils.copy(in, out);
            } finally {
                // then do the atomic rename
                tmp.renameTo(dest);
                // finally remove the original source
                FileUtils.deleteQuietly(source);
            }
        }
        if (!dest.exists()) {
            throw new IOException("Could not create file: " + dest);
        }
    }

    /**
     * Sets the last modification date to now on a file
     *
     * @param file
     *         the file
     */
    public static void touch(final File file) {
        final long time = System.currentTimeMillis();
        if (file.setLastModified(time)) {
            return;
        }
        if (!file.canWrite()) {
            return;
        }
        try {
            try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(raf.length());
            }
        } catch (final IOException e) {
            LOG.debug("Cannot set last modified for file: " + file, e);
        }
    }

    /**
     * Gestion des extensions possibles
     *
     * @author jbrunet
     */
    public enum FileExtension {
        PNG(".png"),
        JPG(".jpg");

        private String extension;

        FileExtension(final String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return this.extension;
        }
    }
    
    @Transactional
    public void deleteOrphanFiles(final String libraryId) {
        
        final File binStorage = storageInfos.get(libraryId).get("filesStorageDir"); 
        final Path dest =  binStorage.toPath();
        
        LOG.warn("DELETE ORPHANS - filesstoragedir = {}", binStorage.getAbsolutePath());
        
        try (final Stream<Path> stream = Files.walk(dest, FileVisitOption.FOLLOW_LINKS)) {
            
            final List<File> dirDigests = stream.filter(p -> MASTER_PATH.equals(p.getFileName().toString()))
                    .map(Path::getParent)
                    .map(Path::toFile)
                    .filter(f -> f.isDirectory())
                    .filter(f -> binaryRepository.countByPageDigest(f.getName()) == 0)
                    .collect(Collectors.toList());
            LOG.warn("dossiers parents de master à supprimer : {}", dirDigests.size());
            
            dirDigests.stream()
                        .forEach(f -> {
                            LOG.debug("orphan file found : {}", f.getName());
                            FileUtils.deleteQuietly(f);
                        });            
                  
            stream.close();
        } catch (IOException | SecurityException e) {
            LOG.error("Erreur lors de la suppression des fichiers binaires dans {}", dest.toAbsolutePath().toString(), e);
        }
        
    }

    /**
     * SCRAMBLER
    */
    protected BinaryScrambler getBinaryScrambler() {
        return NullBinaryScrambler.INSTANCE;
    }

    /**
     * A {@link BinaryScrambler} that does nothing.
     */
    public static class NullBinaryScrambler implements BinaryScrambler {
        public static final BinaryScrambler INSTANCE = new NullBinaryScrambler();

        @Override
        public void scrambleBuffer(final byte[] buf, final int off, final int n) {
        }

        @Override
        public void unscrambleBuffer(final byte[] buf, final int off, final int n) {
        }

        @Override
        public Binary getUnscrambledBinary(final File file, final String digest) {
            return new Binary(file, digest);
        }

        @Override
        public void skip(final long n) {
        }

        @Override
        public void reset() {
        }
    }

    /**
     * A {@link Binary} that is unscrambled on read using a {@link BinaryScrambler}.
     */
    public static class ScrambledBinary extends Binary {

        private final File file;
        protected final BinaryScrambler scrambler;

        public ScrambledBinary(final File file, final String digest, final BinaryScrambler scrambler) {
            super(file, digest);
            this.file = file;
            this.scrambler = scrambler;
        }

        @Override
        public InputStream getStream() throws IOException {
            return new ScrambledFileInputStream(file, scrambler);
        }

    }

    /**
     * A {@link FileInputStream} that is unscrambled on read using a {@link BinaryScrambler}.
     */
    public static class ScrambledFileInputStream extends InputStream {

        protected final InputStream is;
        protected final BinaryScrambler scrambler;
        protected final byte[] onebyte = new byte[1];

        protected ScrambledFileInputStream(final File file, final BinaryScrambler scrambler) throws IOException {
            is = new FileInputStream(file);
            this.scrambler = scrambler;
            scrambler.reset();
        }

        @Override
        public int read() throws IOException {
            int b = is.read();
            if (b != -1) {
                onebyte[0] = (byte) b;
                scrambler.unscrambleBuffer(onebyte, 0, 1);
                b = onebyte[0];
            }
            return b;
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int n = is.read(b, off, len);
            if (n != -1) {
                scrambler.unscrambleBuffer(b, off, n);
            }
            return n;
        }

        @Override
        public long skip(long n) throws IOException {
            n = is.skip(n);
            scrambler.skip(n);
            return n;
        }

        @Override
        public int available() throws IOException {
            return is.available();
        }

        @Override
        public void close() throws IOException {
            is.close();
        }
    }
}
