package fr.progilone.pgcn.service.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import fr.progilone.pgcn.service.util.ImageUtils;

/**
 * Service de stockage des fichiers non managés
 *
 * @author jbrunet
 * Créé le 7 mars 2017
 */
@Service
public class FileStorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageManager.class);

    @Value("${instance.libraries}")
    private String[] instanceLibraries;
    
    private final ImageDispatcherService imageDispatcherService;
    private final DefaultFileFormats defautFileFormats;
    private final UserRepository userRepository;

    @Autowired
    public FileStorageManager(final ImageDispatcherService imageDispatcherService, 
                              final DefaultFileFormats defaultFileFormats,
                              final UserRepository userRepository) {
        this.imageDispatcherService = imageDispatcherService;
        this.defautFileFormats = defaultFileFormats;
        this.userRepository = userRepository;
    }

    /**
     * Initialisation d'un répertoire de stockage
     *
     * @param storageDir
     */
    public void initializeStorage(final String storageDir) {
        
        if (instanceLibraries != null) {
            // 1 disk space per library 
            Arrays.asList(instanceLibraries).forEach(lib -> {
                try {
                    FileUtils.forceMkdir(new File(storageDir, lib));
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            });
        }
    }
    
    /**
     * Renvoie l'id de la librairie du user connecté.
     * @return
     */
    public String getUserLibraryId() {
        final User user = userRepository.findOneWithLibrary(SecurityUtils.getCurrentUserId());
        if (user != null && user.getLibrary() != null) {
            return user.getLibrary().getIdentifier();
        } else {
            throw new RuntimeException("Bibliotheque non autorisee!"); 
        }
        
    }

    /**
     * Store a file, no questions asked
     *
     * @param input
     * @param directory
     * @param filename
     * @param createDir
     * @param addLibInPath
     */
    public File copyInputStreamToFile(final InputStream input, final File directory, final String filename, 
                                              final boolean createDir, final boolean addLibInPath) { 
        if (input != null && directory != null && filename != null) {
            final Path path = addLibInPath ? Paths.get(directory.getPath(), getUserLibraryId()) : Paths.get(directory.getPath());
            if (createDir) {
                try {  
                    FileUtils.forceMkdir(path.toFile());

                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            try {
                final File destination = new File(path.toFile(), filename);
                FileUtils.copyInputStreamToFile(input, destination);
                return destination;

            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }
    
    /**
     * Store a file, no questions asked.
     * idem ci-dessus, mais avec possiblilité d'intercaler des dossiers supplémentaires.
     *
     * @param input
     * @param directory
     * @param filename
     * @param createDir
     */
    public File copyInputStreamToFileWithOtherDirs(final InputStream input, final File directory, final List<String> dirsToAdd, 
                                                       final String filename, final boolean createDir, final boolean addLibInPath) {
        if (input != null 
                && directory != null 
                    && filename != null) {
            
            Path path = addLibInPath ? Paths.get(directory.getPath(), getUserLibraryId()) : Paths.get(directory.getPath());
            for (final String dir : dirsToAdd) {
                path = Paths.get(path.toString(), dir);
            }
            
            if (createDir) {
                try {  
                    FileUtils.forceMkdir(path.toFile());

                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            try {
                final File destination = new File(path.toFile(), filename);
                FileUtils.copyInputStreamToFile(input, destination);
                return destination;

            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }
    
    /**
     * Appends lines in existing file.  
     * 
     * @param linesToAdd
     * @param directory
     * @param filename
     * @return
     */
    public File appendFile(final Collection<String> linesToAdd, final File directory, final String filename) {
        if (directory != null && filename != null && !linesToAdd.isEmpty()) {
            final File destination = new File(directory, filename);
            try {
                FileUtils.writeLines(destination, StandardCharsets.UTF_8.toString(), linesToAdd, true);
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
            return destination;
        }
        return null;
    }

    /**
     * Upload d'un fichier
     * storageDir/obj.getIdentifier()
     *
     * @param source
     * @param storageDir
     * @param obj
     * @return
     */
    public File copyInputStreamToFile(final InputStream source, final String storageDir, final AbstractDomainObject obj) {
        final File destinationDir = Paths.get(storageDir).toFile();
        final String destination = Base64.getEncoder().encodeToString(obj.getIdentifier().getBytes(StandardCharsets.UTF_8));
        return copyInputStreamToFile(source, destinationDir, destination, true, true);
    }

    /**
     * Upload d'un fichier
     * storageDir/obj.getIdentifier()/name
     *
     * @param source
     * @param storageDir
     * @param obj
     * @return
     */
    public File copyInputStreamToFile(final InputStream source, final String storageDir, final AbstractDomainObject obj, final String name) {
        final Path path = Paths.get(storageDir);
        final File destinationDir = new File(path.toFile(), obj.getIdentifier());
        final String destination = Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        return copyInputStreamToFile(source, destinationDir, destination, true, true);
    }

    public File retrieveFile(final File file) {
        if (file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * Retrieve a file, no questions asked
     *
     * @param directory
     * @param filename
     * @return
     */
    public File retrieveFile(final File directory, final String filename) {
        if (directory != null && filename != null) {
            return retrieveFile(new File(directory, filename));
        }
        return null;
    }
    
    /**
     * Retrieve a file, no questions asked
     * 
     * storageDir/libraryId/obj.getIdentifier()/name
     * 
     * @param directory
     * @param dirsToAdd
     * @param filename
     * @return 
     */
    public File retrieveFileWithOtherDirs(final File directory, final List<String> dirsToAdd, final String filename) {
        
        if (directory != null && filename != null) {
            Path path = Paths.get(directory.getPath(), getUserLibraryId());
            for (final String dir : dirsToAdd) {
                path = Paths.get(path.toString(), dir);
            }
            return retrieveFile(new File(path.toFile(), filename));            
        }
        return null;
    }

    /**
     * Récupération d'un fichier lié à un {@link AbstractDomainObject}
     * storageDir/obj.getIdentifier()
     *
     * @param storageDir
     * @param obj
     * @return
     */
    public File retrieveFile(final String storageDir, final AbstractDomainObject obj) {
        if (obj == null) {
            return null;
        }
        final File root = FileUtils.getFile(storageDir, getUserLibraryId());
        final String filename = Base64.getEncoder().encodeToString(obj.getIdentifier().getBytes(StandardCharsets.UTF_8));
        return retrieveFile(root, filename);
    }

    /**
     * Récupération d'un fichier lié à un {@link AbstractDomainObject}
     * storageDir/obj.getIdentifier()/name
     *
     * @param storageDir
     * @param obj
     * @param name
     * @return
     */
    public File retrieveFile(final String storageDir, final AbstractDomainObject obj, final String name) {
        if (obj == null || name == null) {
            return null;
        }
        final File root = FileUtils.getFile(storageDir, getUserLibraryId(), obj.getIdentifier());
        final String filename = Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        return retrieveFile(root, filename);
    }

    /**
     * Génération de l'aperçu du fichier téléversé
     *
     * @param source
     * @param mimeType
     * @param format
     * @param storageDir
     * @param obj
     * @return
     */
    public File createThumbnail(final File source,
                                final String mimeType,
                                final ViewsFormatConfiguration.FileFormat format,
                                final String storageDir,
                                final AbstractDomainObject obj,
                                final String name) {

        final File destination = getUploadFile(storageDir, null, obj, name);
        if (destination == null) {
            return null;
        }

        final boolean generationResult =
            imageDispatcherService.createThumbnailDerived(mimeType, source, destination, format, null, new Long[] {defautFileFormats.getHeightByFormat(format), 
                                                                                                                   defautFileFormats.getWidthByFormat(format)});
        if (!generationResult || destination.length() == 0L) {

            LOG.debug("La génération de l'aperçu pour le fichier {} a échoué ({})", source.getAbsolutePath(), destination.getAbsolutePath());
            FileUtils.deleteQuietly(destination);
            return null;
        }
        return destination;
    }

    /**
     * Création d'un aperçu, basé sur la librarie java ImageIO
     *
     * @param source
     * @param mimeType
     * @param format
     * @param storageDir
     * @param obj
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public File createThumbnail(final InputStream source,
                                final String mimeType,
                                final ViewsFormatConfiguration.FileFormat format,
                                final String storageDir,
                                final AbstractDomainObject obj,
                                final String name) {
        final File destination = getUploadFile(storageDir, null, obj, name);
        if (destination == null) {
            return null;
        }
        
        final boolean generationResult;

        switch (mimeType) {
            case MediaType.IMAGE_GIF_VALUE:
            case MediaType.IMAGE_JPEG_VALUE:
            case MediaType.IMAGE_PNG_VALUE:
                generationResult =
                    ImageUtils.createThumbnail(source, destination, 
                                               Long.valueOf(defautFileFormats.getWidthByFormat(format)).intValue(), 
                                               Long.valueOf(defautFileFormats.getHeightByFormat(format)).intValue());
                break;
            default:
                generationResult = false;
                LOG.error("Conversion non supportée");
        }

        if (!generationResult || destination.length() == 0L) {
            LOG.debug("La génération de l'aperçu a échoué ({})", destination.getAbsolutePath());
            FileUtils.deleteQuietly(destination);
            return null;
        }
        return destination;
    }

    
    /**
     * Fichier correspondant aux paramètres
     *
     * @param storageDir
     * @param obj
     * @param name
     * @return
     */
    public File getUploadFile(final String storageDir, final String libraryId, final AbstractDomainObject obj, final String name) {
        final String destination = Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        final String librId = libraryId==null ? getUserLibraryId() : libraryId;
        
        final Path path;
        if(obj == null) {
            path = Paths.get(storageDir, librId, destination);
        } else {
            path = Paths.get(storageDir, librId, obj.getIdentifier(), destination);
        }
        return path.toFile();
    }
    
    /**
     * Génération du fichier temporaire utilisé pour le traitement asynchrone d'un MultipartFile
     *
     * @param multipartFile
     * @return
     */
    public File getImportFile(final MultipartFile multipartFile, final String targetDir) {
        return new File(targetDir,
                        String.format("%s-%s-%s", SecurityUtils.getCurrentLogin(), System.currentTimeMillis(), multipartFile.getOriginalFilename()));
    }
}
