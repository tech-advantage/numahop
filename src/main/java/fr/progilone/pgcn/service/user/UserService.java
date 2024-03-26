package fr.progilone.pgcn.service.user;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.security.PersistentToken;
import fr.progilone.pgcn.domain.user.Address;
import fr.progilone.pgcn.domain.user.Dashboard;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.repository.security.PersistentTokenRepository;
import fr.progilone.pgcn.repository.user.DashboardRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.repository.workflow.WorkflowGroupRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.exchange.template.VelocityEngineService;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.RandomUtil;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    public static final String SUPER_ADMIN_ID = "SUPER_ADMIN";

    private static final String MAIL_TEMPLATE_BODY = "_BODY_";
    private static final String MAIL_TEMPLATE_SUBJECT = "_SUBJECT_";

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final DashboardRepository dashboardRepository;
    private final FileStorageManager fm;
    private final LotRepository lotRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PersistentTokenRepository persistentTokenRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkflowGroupRepository workflowGroupRepository;
    private final UserValidationService userValidationService;
    private final VelocityEngineService templateService;

    // Stockage des fichiers importés
    @Value("${uploadPath.user}")
    private String userDir;

    public UserService(final DashboardRepository dashboardRepository,
                       final FileStorageManager fm,
                       final LotRepository lotRepository,
                       final MailService mailService,
                       final PasswordEncoder passwordEncoder,
                       final PersistentTokenRepository persistentTokenRepository,
                       final ProjectRepository projectRepository,
                       final UserRepository userRepository,
                       final WorkflowGroupRepository workflowGroupRepository,
                       final UserValidationService userValidationService,
                       final VelocityEngineService templateService) {
        this.fm = fm;
        this.lotRepository = lotRepository;
        this.projectRepository = projectRepository;
        this.templateService = templateService;
        this.userRepository = userRepository;
        this.workflowGroupRepository = workflowGroupRepository;
        this.dashboardRepository = dashboardRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.persistentTokenRepository = persistentTokenRepository;
        this.userValidationService = userValidationService;
    }

    @PostConstruct
    public void initialize() {
        fm.initializeStorage(userDir);
    }

    @Transactional
    public void changeCurrentUserPassword(final String password) {
        final User currentUser = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow();
        final String encryptedPassword = passwordEncoder.encode(password);
        currentUser.setPassword(encryptedPassword);
    }

    @Transactional
    public String changeUserPassword(final String identifier) {
        String password = RandomUtil.generatePassword();
        changeUserPassword(identifier, password);
        return password;
    }

    @Transactional
    public void changeUserPassword(final String identifier, final String password) {
        final User userFromDb = userRepository.findById(identifier).orElseThrow();
        final String encryptedPassword = passwordEncoder.encode(password);
        userFromDb.setPassword(encryptedPassword);
        userRepository.save(userFromDb);
    }

    @Transactional(readOnly = true)
    public String checkPassword(final String login, final String password) {
        final User user = userRepository.findByLogin(login);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user.getIdentifier();
        }
        return null;
    }

    @Transactional
    public boolean resetPassword(final String username) {
        final User user = userRepository.findByLogin(username);

        if (user != null && StringUtils.isNotBlank(user.getEmail())) {
            try {
                // Génération du mot de passe
                final String password = RandomUtil.generatePassword();
                // Génération du mail à l'aide du moteur de templates
                final HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("login", username);
                parameters.put("password", password);
                // Envoi du mail
                if (sendEmail(user, Name.ReinitPassword, parameters)) {
                    user.setPassword(passwordEncoder.encode(password));
                }
                return true;

            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Suppression d'un usager depuis son identifiant
     *
     * @param identifier
     *            l'identifiant de l'usager
     * @throws PgcnBusinessException
     *             si la suppression de l'usager échoue
     */
    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        // Validation de la suppression
        final User user = userRepository.getOne(identifier);
        validateDelete(user);
        // Nettoyage des projets associés
        for (final Project project : projectRepository.findAllByAssociatedUsers(user)) {
            project.getAssociatedUsers().removeIf(u -> StringUtils.equals(u.getIdentifier(), identifier));
            projectRepository.save(project);
        }
        // Appartenance à des groupes de workflow
        for (final WorkflowGroup workflowGroup : workflowGroupRepository.findAllByUsers(user)) {
            workflowGroup.getUsers().removeIf(u -> StringUtils.equals(u.getIdentifier(), identifier));
            workflowGroupRepository.save(workflowGroup);
        }
        // Suppr. signature
        deleteUserSignature(user);
        // Suppression
        userRepository.deleteById(identifier);
    }

    private void validateDelete(final User user) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Projet
        final Long projCount = projectRepository.countByProvider(user);
        if (projCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_DEL_EXITS_PROJ).setAdditionalComplement(projCount).build());
        }
        // Lot
        final Long lotCount = lotRepository.countByProvider(user);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_DEL_EXITS_LOT).setAdditionalComplement(lotCount).build());
        }

        // Groupe de workflow
        final Long workflowCount = workflowGroupRepository.countByUser(user);
        if (workflowCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_IN_WORKFLOW_GROUP).setAdditionalComplement(lotCount).build());
        }

        if (!errors.isEmpty()) {
            user.setErrors(errors);
            throw new PgcnValidationException(user, errors);
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userRepository.findByIdentifierIn(ids, null);
    }

    @Transactional(readOnly = true)
    public List<User> findAllByActive(final boolean active) {
        return userRepository.findAllByActive(active);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByLogin(final String login) {
        return userRepository.findByLogin(StringUtils.lowerCase(login));
    }

    @Transactional(readOnly = true)
    public List<User> findByLoginIn(final List<String> logins) {
        if (CollectionUtils.isEmpty(logins)) {
            return Collections.emptyList();
        }
        return userRepository.findByLoginIn(logins);
    }

    @Transactional(readOnly = true)
    public User findByIdentifier(final String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public User getOne(final String identifier) {
        return userRepository.findOneWithDependencies(identifier);
    }

    /**
     * Ramène un utilisateur et ses groupes
     */
    @Transactional(readOnly = true)
    public User getOneWithGroups(final String identifier) {
        return userRepository.findOneWithDependenciesAndGroups(identifier);
    }

    /**
     * Création d'un usager
     * La vérification des champs requis se font en amont dans les DTO
     */
    @Transactional
    public User create(final User user) throws PgcnValidationException {
        setDefaultValues(user);
        userValidationService.validate(user);

        // génération du mot de passe si non renseigné
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(RandomUtil.generatePassword());
        }
        // encodage du mot de passe
        final String password = user.getPassword();
        final String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);
        savedUser = getOne(savedUser.getIdentifier());

        // Génération du mail à l'aide du moteur de templates
        try {
            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("login", user.getLogin());
            parameters.put("password", password);

            // Envoi du mail
            sendEmail(user, Name.UserCreation, parameters);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return savedUser;
    }

    /**
     * Sauvegarde un usager
     * La vérification des champs requis se font en amont dans les DTO
     */
    @Transactional
    public User update(final User user) throws PgcnValidationException {
        setDefaultValues(user);
        userValidationService.validate(user);

        // On ne modifie pas le mot de passe via cette méthode donc on va chercher l'ancien dans la base pour le rétablir
        user.setPassword(userRepository.findPasswordByIdentifier(user.getIdentifier()));

        final User savedUser = userRepository.save(user);
        return getOne(savedUser.getIdentifier());
    }

    @Transactional(readOnly = true)
    public Page<User> search(final String search,
                             final String initiale,
                             final boolean active,
                             final boolean filterProviders,
                             final List<String> libraries,
                             final List<Category> categories,
                             final List<String> roles,
                             final Integer page,
                             final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        return userRepository.search(search, initiale, active, filterProviders, libraries, categories, roles, pageRequest);
    }

    @Transactional
    public void updateDashboard(final String currentUserId, final String dashboard) {
        if (SUPER_ADMIN_ID.equals(currentUserId)) {
            Dashboard d = dashboardRepository.findById(SUPER_ADMIN_ID).orElse(null);
            if (d == null) {
                d = new Dashboard();
                d.setIdentifier(SUPER_ADMIN_ID);
                d = dashboardRepository.save(d);
            }
            d.setDashboard(dashboard);
        } else {
            final User user = findByIdentifier(currentUserId);
            if (user.getDashboard() == null) {
                user.setDashboard(new Dashboard());
            }
            user.getDashboard().setDashboard(dashboard);
        }
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     */
    @Scheduled(cron = "${cron.removeOldTokens}")
    @Transactional
    public void removeOldPersistentTokens() {
        LOG.info("Exécution de la tâche planifiée removeOldTokens");
        final LocalDate now = LocalDate.now();
        final List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(Date.from(now.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        for (final PersistentToken token : tokens) {
            final User user = token.getUser();
            if (user != null) {
                user.getPersistentTokens().remove(token);
            }
            persistentTokenRepository.delete(token);
        }
        LOG.info("Fin de l'exécution de la tâche planifiée removeOldTokens");
    }

    private void setDefaultValues(final User user) {
        // login en minuscules
        user.setLogin(StringUtils.lowerCase(user.getLogin()));
        // pas un superutilisateur
        user.setSuperuser(false);
    }

    @Transactional(readOnly = true)
    public List<User> findAllByActiveAndCategory(final boolean active, final Category category) {
        return userRepository.findAllByActiveAndCategory(active, category);
    }

    /**
     * Retourne tous les prestataires associés à la bibliothèque
     */
    @Transactional(readOnly = true)
    public Collection<User> findProvidersForLibrary(final String id) {
        return userRepository.findAllByCategoryAndLibraryIdentifier(Category.PROVIDER, id);
    }

    /**
     * Retourne tous les utilisateurs associés à la bibliothèque
     */
    @Transactional(readOnly = true)
    public Collection<User> findUsersForLibrary(final String id) {
        return userRepository.findAllByLibraryIdentifier(id);
    }

    @Transactional(readOnly = true)
    public User duplicateUser(final String id) {
        final User original = findByIdentifier(id);
        final User duplicated = new User();
        copyAttributes(original, duplicated);
        return duplicated;
    }

    /**
     * Duplication d'un utilisateur.
     * On ne recopie ni le login, ni le mot de passe.
     *
     * @param source
     * @param destination
     */
    private void copyAttributes(final User source, final User destination) {
        destination.setActive(source.isActive());
        destination.setCategory(source.getCategory());
        destination.setEmail(source.getEmail());
        destination.setFirstname(source.getFirstname());
        destination.setFunction(source.getFunction());
        destination.setLibrary(source.getLibrary());
        destination.setPhoneNumber(source.getPhoneNumber());
        destination.setSurname(source.getSurname());
        destination.setRole(source.getRole());

        // PROVIDER fields only
        if (Category.PROVIDER.equals(source.getCategory())) {
            final Address oldAddress = source.getAddress();
            final Address newAddress = new Address();

            if (oldAddress != null) {
                newAddress.setLabel(oldAddress.getLabel());
                newAddress.setAddress1(oldAddress.getAddress1());
                newAddress.setAddress2(oldAddress.getAddress2());
                newAddress.setAddress3(oldAddress.getAddress3());
                newAddress.setComplement(oldAddress.getComplement());
                newAddress.setCity(oldAddress.getCity());
                newAddress.setPostcode(oldAddress.getPostcode());
                newAddress.setCountry(oldAddress.getCountry());
                destination.setAddress(newAddress);
            }
            destination.setCompanyName(source.getCompanyName());
        }
    }

    /**
     * Envoi d'un email à l'utilisateur, généré à partir d'un template Velocity.
     *
     * @param user
     * @param templateName
     * @param parameters
     * @param reset
     * @return
     * @throws IOException
     */
    private boolean sendEmail(final User user, final Name templateName, final HashMap<String, Object> parameters) throws IOException {
        if (!user.isActive()) {
            LOG.debug("L'utilisateur {} est désactivé; aucun email n'est envoyé", user.getLogin());
            return false;
        }
        final String mailTemplate = templateService.generateDocument(templateName, user.getLibrary(), parameters);

        final String mailSubject, mailBody;
        final int idxSubject = mailTemplate.indexOf(MAIL_TEMPLATE_SUBJECT);
        final int idxBody = mailTemplate.indexOf(MAIL_TEMPLATE_BODY);

        if (0 <= idxSubject && idxSubject <= idxBody) {
            mailSubject = mailTemplate.substring(idxSubject + MAIL_TEMPLATE_SUBJECT.length(), idxBody).trim();
            mailBody = mailTemplate.substring(idxBody + MAIL_TEMPLATE_BODY.length()).trim();
        } else {
            mailSubject = "";
            mailBody = mailTemplate;
        }

        // Envoi du mail
        final String[] to = {user.getEmail()};
        return mailService.sendEmail(null, to, mailSubject, mailBody, false, false);
    }

    /**
     * Suppression de la signature de l'utilisateur
     */
    public void deleteUserSignature(final User user) {
        final File userFile = getUserSignature(user);
        if (userFile != null) {
            FileUtils.deleteQuietly(userFile);
        }
        final File thumbnailFile = getUserThumbnail(user);
        if (thumbnailFile != null) {
            FileUtils.deleteQuietly(thumbnailFile);
        }
    }

    /**
     * Signature de l'utilisateur
     *
     * @return null si aucun fichier n'est trouvé
     */
    @Transactional(readOnly = true)
    public File getUserSignature(final User user) {
        final File logoFile;
        if (user.getLibrary() != null) {
            logoFile = fm.getUploadFile(userDir,
                                        user.getLibrary().getIdentifier(),
                                        null,
                                        ViewsFormatConfiguration.FileFormat.MASTER.label() + "."
                                              + user.getIdentifier());
        } else {
            logoFile = fm.getUploadFile(userDir,
                                        null,
                                        null,
                                        ViewsFormatConfiguration.FileFormat.MASTER.label() + "."
                                              + user.getIdentifier());
        }
        return fm.retrieveFile(logoFile);
    }

    /**
     * Signature de l'utilisateur (aperçu)
     *
     * @return null si aucun fichier n'est trouvé
     */
    @Transactional(readOnly = true)
    public File getUserThumbnail(final User user) {
        final File thumbnailFile;
        if (user.getLibrary() != null) {
            thumbnailFile = fm.getUploadFile(userDir,
                                             user.getLibrary().getIdentifier(),
                                             null,
                                             ViewsFormatConfiguration.FileFormat.THUMB.label() + "."
                                                   + user.getIdentifier());
        } else {
            thumbnailFile = fm.getUploadFile(userDir,
                                             null,
                                             null,
                                             ViewsFormatConfiguration.FileFormat.THUMB.label() + "."
                                                   + user.getIdentifier());
        }
        return fm.retrieveFile(thumbnailFile);
    }

    /**
     * Téléversement de la signature de l'utilisateur
     */
    @Transactional
    public void uploadSignature(final User user, final MultipartFile file) {
        if (file != null && file.getSize() > 0) {
            LOG.debug("Téléversement de la signature {} de l'utilisateur {}", file.getOriginalFilename(), user.getIdentifier());
            uploadImage(user, file, ViewsFormatConfiguration.FileFormat.MASTER);
            uploadImage(user, file, ViewsFormatConfiguration.FileFormat.THUMB);
        }
    }

    private void uploadImage(final User user, final MultipartFile file, final ViewsFormatConfiguration.FileFormat format) {
        try (InputStream in = file.getInputStream()) {
            fm.createThumbnail(in,
                               file.getContentType(),
                               format,
                               userDir,
                               null,
                               format.label() + "."
                                     + user.getIdentifier());
            LOG.debug("Le logo de l'utilisateur {} ({}) a été importé: {} (format {})", user.getLogin(), user.getIdentifier(), file.getOriginalFilename(), format);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUsersGroupByLibrary(final List<String> libraries) {
        final List<Object[]> results = userRepository.getUsersGroupByLibrary(libraries);

        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("libraryIdentifier", res[0]);
            resMap.put("libraryName", res[1]);
            resMap.put("nbUsers", res[2]);
            return resMap;
        }).collect(Collectors.toList());
    }
}
