package fr.progilone.pgcn.web.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.train.TrainService;
import fr.progilone.pgcn.service.user.UserService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Sébastien on 20/12/2016.
 */
@ExtendWith(MockitoExtension.class)
public class AccessHelperTest {

    public static final CustomUserDetails USER_PRESTA = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                              "anne",
                                                                              "azerty",
                                                                              Lang.FR,
                                                                              "LIB-001",
                                                                              Collections.emptyList(),
                                                                              false,
                                                                              User.Category.PROVIDER);
    public static final CustomUserDetails USER_TOTO = new CustomUserDetails("199c0268-1b1b-453d-9725-6d9747588477",
                                                                            "toto",
                                                                            "azerty",
                                                                            Lang.FR,
                                                                            "LIB-001",
                                                                            Collections.emptyList(),
                                                                            false,
                                                                            User.Category.OTHER);
    public static final CustomUserDetails USER_SUPER = new CustomUserDetails("9503d0dc-014b-413e-ab2f-3521572545fa",
                                                                             "superman",
                                                                             "azerty",
                                                                             Lang.FR,
                                                                             "LIB-001",
                                                                             Collections.emptyList(),
                                                                             true,
                                                                             User.Category.OTHER);

    @Mock
    private BibliographicRecordService bibliographicRecordService;
    @Mock
    private DeliveryService deliveryService;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private DocUnitService docUnitService;
    @Mock
    private LotService lotService;
    @Mock
    private ProjectService projectService;
    @Mock
    private TrainService trainService;
    @Mock
    private UserService userService;
    @Mock
    private ConditionReportDetailService conditionReportDetailService;

    private AccessHelper accessHelper;

    @BeforeEach
    public void setUp() {
        accessHelper = new AccessHelper(bibliographicRecordService,
                                        deliveryService,
                                        digitalDocumentService,
                                        docUnitService,
                                        lotService,
                                        projectService,
                                        trainService,
                                        userService,
                                        conditionReportDetailService);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testCheckProjectNoOne() {
        final String identifier = "18b62180-6773-407c-9171-2e978b8f57eb";

        // pas connecté
        final boolean actual = accessHelper.checkProject(identifier);
        assertFalse(actual);
        verify(projectService, never()).findByIdentifier(identifier);
    }

    @Test
    public void testCheckProjectSuperUser() {
        final String identifier = "18b62180-6773-407c-9171-2e978b8f57eb";

        // super user
        setUser(USER_SUPER);

        final boolean actual = accessHelper.checkProject(identifier);
        assertTrue(actual);
        verify(projectService, never()).findByIdentifier(identifier);
    }

    @Test
    public void testCheckProjectNormal() {
        final String identifier = "18b62180-6773-407c-9171-2e978b8f57eb";
        final Project project = new Project();
        final Library library = new Library();
        project.setLibrary(library);
        final Library assoLibrary = new Library();
        project.addLibrary(assoLibrary);

        // user normal
        setUser(USER_TOTO);
        when(projectService.findByIdentifier(identifier)).thenReturn(project);

        // checkCurrentLibrary ko
        boolean actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // checkCurrentLibrary ok
        library.setIdentifier(USER_TOTO.getLibraryId());
        actual = accessHelper.checkProject(identifier);
        assertTrue(actual);

        // checkAssociatedLibraries ko
        library.setIdentifier(null);
        actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // checkAssociatedLibraries ok
        assoLibrary.setIdentifier(USER_TOTO.getLibraryId());
        actual = accessHelper.checkProject(identifier);
        assertTrue(actual);

        // checkAssociatedUsers ko
        final User user = new User();
        user.setIdentifier("803d9a18-dd0f-41e2-9f56-5310636ef051");
        project.addAssociatedUser(user);

        actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // checkAssociatedUsers ok
        user.setIdentifier(USER_TOTO.getIdentifier());

        actual = accessHelper.checkProject(identifier);
        assertTrue(actual);
    }

    @Test
    public void testCheckProjectPresta() {
        final String identifier = "18b62180-6773-407c-9171-2e978b8f57eb";
        final Project project = new Project();
        project.setLibrary(new Library());

        // user presta
        setUser(USER_PRESTA);
        when(projectService.findByIdentifier(identifier)).thenReturn(project);

        // pas de presta sur le projet
        boolean actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // autre presta sur le projet
        final User user = new User();
        user.setIdentifier("9b94611e-6cbe-4a97-8edc-4e117653bb23");
        project.setProvider(user);

        actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // presta ok sur le projet, mais pas la même bib
        user.setIdentifier(USER_PRESTA.getIdentifier());

        actual = accessHelper.checkProject(identifier);
        assertFalse(actual);

        // presta ok sur le projet, bib ok
        project.getLibrary().setIdentifier(USER_PRESTA.getLibraryId());

        actual = accessHelper.checkProject(identifier);
        assertTrue(actual);
    }

    @Test
    public void testFilterProjects() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final List<Project> projects = identifiers.stream().map(id -> {
            final Project project = new Project();
            project.setIdentifier(id);
            project.setLibrary(new Library());
            return project;
        }).collect(Collectors.toList());
        projects.get(0).getLibrary().setIdentifier(USER_TOTO.getLibraryId());

        when(projectService.findAll(identifiers)).thenReturn(projects);

        // pas connecté
        Collection<Project> actual = accessHelper.filterProjects(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterProjects(identifiers);
        assertEquals(3, actual.size());

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.filterProjects(identifiers);
        assertEquals(1, actual.size());
    }

    @Test
    public void testFilterLots() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final List<Lot> lots = identifiers.stream().map(id -> {
            final Lot lot = new Lot();
            lot.setIdentifier(id);
            lot.setProject(new Project());
            return lot;
        }).collect(Collectors.toList());

        final Library library = new Library();
        library.setIdentifier("4b7d757d-a679-4c5b-97cb-6b54f60c5d98");
        final Project project = new Project();
        project.setLibrary(library);
        lots.get(1).setProject(project);

        when(lotService.findAll(identifiers)).thenReturn(lots);

        // pas connecté
        Collection<Lot> actual = accessHelper.filterLots(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterLots(identifiers);
        assertEquals(3, actual.size());

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.filterLots(identifiers);
        assertEquals(2, actual.size());
    }

    @Test
    public void testFilterTrains() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final List<Train> trains = identifiers.stream().map(id -> {
            final Train train = new Train();
            train.setIdentifier(id);
            train.setProject(new Project());
            return train;
        }).collect(Collectors.toList());

        final Library library = new Library();
        library.setIdentifier("4b7d757d-a679-4c5b-97cb-6b54f60c5d98");
        final Project project = new Project();
        project.setLibrary(library);
        trains.get(1).setProject(project);

        when(trainService.findAll(identifiers)).thenReturn(trains);

        // pas connecté
        Collection<Train> actual = accessHelper.filterTrains(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterTrains(identifiers);
        assertEquals(3, actual.size());

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.filterTrains(identifiers);
        assertEquals(2, actual.size());
    }

    @Test
    public void testCheckDelivery() {
        final String identifier = "e00d7033-1935-4f21-b825-91fec1ec3067";
        final Delivery delivery = new Delivery();
        delivery.setIdentifier(identifier);

        final Library library = new Library();
        library.setIdentifier("4b7d757d-a679-4c5b-97cb-6b54f60c5d98");
        final Project project = new Project();
        project.setLibrary(library);
        final Lot lot = new Lot();
        lot.setProject(project);
        delivery.setLot(lot);

        when(deliveryService.getOne(identifier)).thenReturn(delivery);

        // pas connecté
        boolean actual = accessHelper.checkDelivery(identifier);
        assertFalse(actual);

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.checkDelivery(identifier);
        assertTrue(actual);

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.checkDelivery(identifier);
        assertFalse(actual);

        lot.setProject(null);
        actual = accessHelper.checkDelivery(identifier);
        assertFalse(actual);
    }

    @Test
    public void testFilterDigitalDocuments() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final List<DigitalDocument> digitalDocuments = identifiers.stream().map(id -> {
            final DigitalDocument digitalDocument = new DigitalDocument();
            digitalDocument.setIdentifier(id);
            return digitalDocument;
        }).collect(Collectors.toList());

        when(digitalDocumentService.findAll(identifiers)).thenReturn(digitalDocuments);

        // pas connecté
        Collection<DigitalDocument> actual = accessHelper.filterDigitalDocuments(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterDigitalDocuments(identifiers);
        assertEquals(3, actual.size());

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.filterDigitalDocuments(identifiers);
        assertEquals(3, actual.size());
    }

    @Test
    public void testFilterDocUnits() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final Set<DocUnit> docUnits = identifiers.stream().map(id -> {
            final DocUnit docUnit = new DocUnit();
            docUnit.setIdentifier(id);
            final Library library = new Library();
            library.setIdentifier("995fb085-02df-44d3-af57-451044a75aef");
            docUnit.setLibrary(library);
            return docUnit;
        }).collect(Collectors.toSet());

        when(docUnitService.findAllById(identifiers)).thenReturn(docUnits);

        // pas connecté
        Collection<DocUnit> actual = accessHelper.filterDocUnits(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterDocUnits(identifiers);
        assertEquals(3, actual.size());

        // user presta
        setUser(USER_PRESTA);
        actual = accessHelper.filterDocUnits(identifiers);
        assertTrue(actual.isEmpty());

        // check lot
        final DocUnit docUnit = docUnits.iterator().next();
        final Lot lot = new Lot();
        final Project project = new Project();
        final Library library = new Library();
        docUnit.setLot(lot);
        lot.setProject(project);
        project.setLibrary(library);
        library.setIdentifier(USER_PRESTA.getLibraryId());

        actual = accessHelper.filterDocUnits(identifiers);
        assertEquals(1, actual.size());

        // check project
        docUnit.setLot(null);
        docUnit.setProject(project);

        actual = accessHelper.filterDocUnits(identifiers);
        assertEquals(1, actual.size());

        // check doc unit
        docUnit.setLot(null);
        docUnit.setProject(null);
        docUnit.setLibrary(library);

        actual = accessHelper.filterDocUnits(identifiers);
        assertEquals(1, actual.size());
    }

    @Test
    public void testCheckUser() {
        final String identifier = "7d0c0d6a-b02b-4ea1-910f-2d875a379019";
        final User user = new User();
        user.setIdentifier(identifier);
        user.setLibrary(new Library());

        when(userService.findByIdentifier(identifier)).thenReturn(user);

        // pas connecté
        boolean actual = accessHelper.checkUser(identifier);
        assertFalse(actual);

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.checkUser(identifier);
        assertTrue(actual);

        // user normal
        setUser(USER_TOTO);
        // lui-même
        actual = accessHelper.checkUser(USER_TOTO.getIdentifier());
        assertTrue(actual);

        // un autre user
        actual = accessHelper.checkUser(identifier);
        assertFalse(actual);

        user.getLibrary().setIdentifier(USER_TOTO.getLibraryId());
        actual = accessHelper.checkUser(identifier);
        assertTrue(actual);
    }

    @Test
    public void testFilterUsers() {
        final List<String> identifiers = Arrays.asList("e00d7033-1935-4f21-b825-91fec1ec3067", "6c2aa430-e67d-4b8a-800f-352a754498ae", "c0d6edc7-6e5d-4100-9b36-eb295a63cfa9");
        final List<User> users = identifiers.stream().map(id -> {
            final User user = new User();
            user.setIdentifier(id);
            user.setLibrary(new Library());
            return user;
        }).collect(Collectors.toList());
        users.get(0).getLibrary().setIdentifier(USER_TOTO.getLibraryId());

        when(userService.findAll(identifiers)).thenReturn(users);

        // pas connecté
        Collection<User> actual = accessHelper.filterUsers(identifiers);
        assertTrue(actual.isEmpty());

        // super user
        setUser(USER_SUPER);
        actual = accessHelper.filterUsers(identifiers);
        assertEquals(3, actual.size());

        // user normal
        setUser(USER_TOTO);
        actual = accessHelper.filterUsers(identifiers);
        assertEquals(1, actual.size());
    }

    private void setUser(final CustomUserDetails userDetails) {
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
