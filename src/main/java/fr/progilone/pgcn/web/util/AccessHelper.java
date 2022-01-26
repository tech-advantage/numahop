package fr.progilone.pgcn.web.util;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.train.TrainService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.progilone.pgcn.service.user.UserService.*;

/**
 * Outils permettant de vérifier que l'utilisateur courant ait bien accès aux ressources qu'il demande
 * <p>
 * Created by Sébastien on 20/12/2016.
 */
@Component
public class AccessHelper {

    private final BibliographicRecordService bibliographicRecordService;
    private final DeliveryService deliveryService;
    private final DigitalDocumentService digitalDocumentService;
    private final DocUnitService docUnitService;
    private final LotService lotService;
    private final ProjectService projectService;
    private final TrainService trainService;
    private final UserService userService;
    private final ConditionReportDetailService conditionReportDetailService;

    @Autowired
    public AccessHelper(final BibliographicRecordService bibliographicRecordService,
                        final DeliveryService deliveryService,
                        final DigitalDocumentService digitalDocumentService,
                        final DocUnitService docUnitService,
                        final LotService lotService,
                        final ProjectService projectService,
                        final TrainService trainService,
                        final UserService userService,
                        final ConditionReportDetailService conditionReportDetailService) {
        this.bibliographicRecordService = bibliographicRecordService;
        this.deliveryService = deliveryService;
        this.digitalDocumentService = digitalDocumentService;
        this.docUnitService = docUnitService;
        this.lotService = lotService;
        this.projectService = projectService;
        this.trainService = trainService;
        this.userService = userService;
        this.conditionReportDetailService = conditionReportDetailService;
    }

    @Transactional(readOnly = true)
    public boolean checkProject(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final Project project = projectService.findByIdentifier(identifier);
            return checkProject(project, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<Project> filterProjects(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<Project> projects = projectService.findAll(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? projects : Collections.<Project>emptyList())
                                            .orElseGet(() -> projects.stream()
                                                                     .filter(project -> checkProject(project, currentUser))
                                                                     .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkLot(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final Lot lot = lotService.findByIdentifier(identifier);
            return checkLot(lot, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<Lot> filterLots(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<Lot> lots = lotService.findAll(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? lots : Collections.<Lot>emptyList())
                                            .orElseGet(() -> lots.stream().filter(lot -> checkLot(lot, currentUser)).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkTrain(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final Train train = trainService.getOne(identifier);
            return checkTrain(train, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<Train> filterTrains(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<Train> trains = trainService.findAll(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? trains : Collections.<Train>emptyList())
                                            .orElseGet(() -> trains.stream()
                                                                   .filter(train -> checkTrain(train, currentUser))
                                                                   .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkDelivery(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final Delivery delivery = deliveryService.getOne(identifier);
            return checkDelivery(delivery, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<Delivery> filterDeliveries(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<Delivery> deliveries = identifiers != null ? deliveryService.findAll(identifiers) : deliveryService.findAll();

        return checkCurrentUser(currentUser).map(check -> check ? deliveries : Collections.<Delivery>emptyList())
                                            .orElseGet(() -> deliveries.stream()
                                                                       .filter(delivery -> checkDelivery(delivery, currentUser))
                                                                       .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkDocUnit(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final DocUnit docUnit = docUnitService.findOne(identifier);
            return checkDocUnit(docUnit, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<DocUnit> filterDocUnits(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final Set<DocUnit> docUnits = docUnitService.findAllById(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? docUnits : Collections.<DocUnit>emptyList())
                                            .orElseGet(() -> docUnits.stream()
                                                                     .filter(docUnit -> checkDocUnit(docUnit, currentUser))
                                                                     .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkBibliographicRecord(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final BibliographicRecord record = bibliographicRecordService.getOne(identifier);
            return checkBibliographicRecord(record, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<BibliographicRecord> filterBibliographicRecords(final List<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<BibliographicRecord> records = bibliographicRecordService.findAllByIdentifierIn(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? records : Collections.<BibliographicRecord>emptyList())
                                            .orElseGet(() -> records.stream()
                                                                    .filter(record -> checkBibliographicRecord(record, currentUser))
                                                                    .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkConditionReportDetail(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final ConditionReportDetail conditionReportDetail = conditionReportDetailService.findByIdentifier(identifier);
            return checkConditionReportDetail(conditionReportDetail, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public List<ConditionReportDetail> filterConditionReportDetails(final List<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<ConditionReportDetail> conditionReportDetails = conditionReportDetailService.findByIdentifierIn(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? conditionReportDetails : Collections.<ConditionReportDetail>emptyList())
                                            .orElseGet(() -> conditionReportDetails.stream()
                                                                                   .filter(record -> checkConditionReportDetail(record, currentUser))
                                                                                   .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkDigitalDocument(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            final DigitalDocument digitalDocument = digitalDocumentService.findOne(identifier);
            return checkDigitalDocument(digitalDocument, currentUser);
        });
    }

    @Transactional(readOnly = true)
    public Collection<DigitalDocument> filterDigitalDocuments(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<DigitalDocument> digitalDocs = digitalDocumentService.findAll(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? digitalDocs : Collections.<DigitalDocument>emptyList())
                                            .orElseGet(() -> digitalDocs.stream()
                                                                        .filter(digitalDocument -> checkDigitalDocument(digitalDocument, currentUser))
                                                                        .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public boolean checkUser(final String identifier) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return checkCurrentUser(currentUser).orElseGet(() -> {
            // Un utilisateur peut accéder à lui-même
            if (StringUtils.equals(identifier, currentUser.getIdentifier())) {
                return true;
            } else {
                final User user = userService.findByIdentifier(identifier);
                return checkUserLib(user, currentUser) && checkUserCategory(user, currentUser);
            }
        });
    }

    @Transactional(readOnly = true)
    public Collection<User> filterUsers(final Iterable<String> identifiers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        final List<User> users = userService.findAll(identifiers);

        return checkCurrentUser(currentUser).map(check -> check ? users : Collections.<User>emptyList())
                                            .orElseGet(() -> users.stream()
                                                                  .filter(user -> checkUserLib(user, currentUser) && checkUserCategory(user,
                                                                                                                                       currentUser))
                                                                  .collect(Collectors.toList()));
    }

    /**
     * Vérification rapides des droits
     *
     * @param currentUser
     * @return false si on n'a pas affaire à un user connecté,
     * true si le user est super user,
     * empty si une vérification plus poussée des droits est nécessaire
     */
    @Transactional(readOnly = true)
    public Optional<Boolean> checkCurrentUser(final CustomUserDetails currentUser) {
        if (currentUser == null) {
            return Optional.of(false);
        } else if (currentUser.isSuperuser() || StringUtils.equals(SUPER_ADMIN_ID, currentUser.getIdentifier())) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Droits d'accès au projet:
     * - même bib + presta
     * - bib associée
     * - intervenant
     *
     * @param project
     * @param currentUser
     * @return
     */
    private boolean checkProject(final Project project, final CustomUserDetails currentUser) {
        return project != null && (
            // bib du projet + prestataire du projet (ou pas)
            (checkCurrentLibrary(project.getLibrary(), currentUser) && checkProjectForProvider(project, currentUser))
            // bib partenaire + intervenant
            || (checkAssociatedLibraries(project.getAssociatedLibraries(), currentUser) && checkAssociatedUsers(project.getAssociatedUsers(),
                                                                                                                currentUser)));
    }

    /**
     * Droits d'accès au projet:
     * - même bib (sans vérif presta)
     * - bib associée
     * - intervenant
     *
     * @param project
     * @param currentUser
     * @return
     */
    private boolean checkProjectIgnoringPresta(final Project project, final CustomUserDetails currentUser) {
        return project != null && (
            // bib du projet
            checkCurrentLibrary(project.getLibrary(), currentUser)
            // bib partenaire + intervenant
            || (checkAssociatedLibraries(project.getAssociatedLibraries(), currentUser) && checkAssociatedUsers(project.getAssociatedUsers(),
                                                                                                                currentUser)));
    }

    /**
     * Droits d'accès au lot:
     * - même bib + presta
     * - bib associée
     * - intervenant
     *
     * @param lot
     * @param currentUser
     * @return
     */
    private boolean checkLot(final Lot lot, final CustomUserDetails currentUser) {
        return lot != null
               // prestataire du lot (ou du projet)
               && checkLotForProvider(lot, currentUser)
               // projet (pas le presta qui est testé sur le lot uniquement)
               && checkProjectIgnoringPresta(lot.getProject(), currentUser);
    }

    /**
     * Droits d'accès au lot:
     * - même bib (sans vérif presta)
     * - bib associée
     * - intervenant
     *
     * @param lot
     * @param currentUser
     * @return
     */
    private boolean checkLotIgnoringPresta(final Lot lot, final CustomUserDetails currentUser) {
        return lot != null && checkProjectIgnoringPresta(lot.getProject(), currentUser);
    }

    private boolean checkTrain(final Train train, final CustomUserDetails currentUser) {
        return train != null && checkProject(train.getProject(), currentUser);
    }

    private boolean checkDelivery(final Delivery delivery, final CustomUserDetails currentUser) {
        return delivery != null && checkLot(delivery.getLot(), currentUser);
    }

    private boolean checkDocUnit(final DocUnit docUnit, final CustomUserDetails currentUser) {
        if (docUnit == null) {
            return true;
        }
        // même bib que l'unité documentaire
        if (checkCurrentLibrary(docUnit.getLibrary(), currentUser)) {
            return true;
        }
        // Droits du lots
        else if (docUnit.getLot() != null) {
            return checkLotIgnoringPresta(docUnit.getLot(), currentUser);
        }
        // Droits du projet
        else if (docUnit.getProject() != null) {
            return checkProjectIgnoringPresta(docUnit.getProject(), currentUser);
        }
        return false;
    }

    private boolean checkBibliographicRecord(final BibliographicRecord record, final CustomUserDetails currentUser) {
        return record == null || checkDocUnit(record.getDocUnit(), currentUser);
    }

    private boolean checkConditionReportDetail(final ConditionReportDetail detail, final CustomUserDetails currentUser) {
        return detail == null || checkDocUnit(detail.getReport().getDocUnit(), currentUser);
    }

    private boolean checkDigitalDocument(final DigitalDocument digitalDocument, final CustomUserDetails currentUser) {
        return digitalDocument == null || checkDocUnit(digitalDocument.getDocUnit(), currentUser);
    }

    /**
     * Vérification de l'accès des prestataires au projet + lot
     *
     * @param project
     * @param currentUser
     * @return true si currentUser est un presta et vérifie les droits presta sur le projet
     */
    private boolean checkProjectForProvider(final Project project, final CustomUserDetails currentUser) {
        if (currentUser.getCategory() != null) {
            switch (currentUser.getCategory()) {
                case PROVIDER:
                    // prestataire sur le projet
                    return project.getProvider() != null && StringUtils.equals(currentUser.getIdentifier(), project.getProvider().getIdentifier())
                           // prestataire sur l'un des lots du projet
                           || project.getLots().stream().anyMatch(lot -> checkUser(lot.getProvider(), currentUser));
                case OTHER:
                    break;
            }
        }
        return true;
    }

    /**
     * Vérification de l'accès des prestataires au lot
     *
     * @param lot
     * @param currentUser
     * @return true si currentUser n'est pas un presta, ou vérifie les droits presta sur le lot
     */
    private boolean checkLotForProvider(final Lot lot, final CustomUserDetails currentUser) {
        if (currentUser.getCategory() != null) {
            switch (currentUser.getCategory()) {
                case PROVIDER:
                    return checkUser(lot.getProvider(), currentUser) || (lot.getProvider() == null
                                                                         && lot.getProject() != null
                                                                         && checkUser(lot.getProject().getProvider(), currentUser));
                case OTHER:
                    break;
            }
        }
        return true;
    }

    /**
     * Vérification des bibliothèques associées
     * L'accès est restreint à la liste fournie
     *
     * @param allowedLibraries
     * @param currentUser
     * @return
     */
    private boolean checkAssociatedLibraries(final Collection<Library> allowedLibraries, final CustomUserDetails currentUser) {
        return CollectionUtils.isNotEmpty(allowedLibraries) && allowedLibraries.stream()
                                                                               .anyMatch(lib -> StringUtils.equals(lib.getIdentifier(),
                                                                                                                   currentUser.getLibraryId()));
    }

    /**
     * Un prestataire ne peut pas voir les autres prestataires
     *
     * @param user
     * @param currentUser
     * @return
     */
    private boolean checkUserCategory(final User user, final CustomUserDetails currentUser) {
        return user == null || !Objects.equals(currentUser.getCategory(), User.Category.PROVIDER) || !Objects.equals(user.getCategory(),
                                                                                                                     User.Category.PROVIDER);
    }

    @Transactional
    public boolean checkUserIsPresta() {
        final CustomUserDetails user = SecurityUtils.getCurrentUser();
        return Objects.equals(user.getCategory(), User.Category.PROVIDER);
    }

    /**
     * Un utilisateur ne peut pas voir les utilisateurs d'autres bibliothèques
     *
     * @param user
     * @param currentUser
     * @return
     */
    private boolean checkUserLib(final User user, final CustomUserDetails currentUser) {
        return user == null || checkCurrentLibrary(user.getLibrary(), currentUser);
    }

    /**
     * Un utilisateur peut se voir lui-même
     *
     * @param user
     * @param currentUser
     * @return
     */
    private boolean checkUser(final User user, final CustomUserDetails currentUser) {
        return user != null && StringUtils.equals(currentUser.getIdentifier(), user.getIdentifier());
    }

    /**
     * Vérification des intervenants
     * Si la liste d'intervenants est vide, l'accès est autorisé
     * Sinon l'accès est restreint à la liste fournie
     *
     * @param allowedUsers
     * @param currentUser
     * @return
     */
    private boolean checkAssociatedUsers(final Collection<User> allowedUsers, final CustomUserDetails currentUser) {
        return CollectionUtils.isEmpty(allowedUsers) || allowedUsers.stream()
                                                                    .anyMatch(u -> StringUtils.equals(u.getIdentifier(),
                                                                                                      currentUser.getIdentifier()));
    }

    /**
     * Vérification de la biblitothèque de l'utilisateur
     *
     * @param library
     * @param currentUser
     * @return
     */
    private boolean checkCurrentLibrary(final Library library, final CustomUserDetails currentUser) {
        return library == null || StringUtils.equals(library.getIdentifier(), currentUser.getLibraryId()) || currentUser.isUserInRole(AuthorizationConstants.ADMINISTRATION_LIB);
    }
}
