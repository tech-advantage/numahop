package fr.progilone.pgcn.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.domain.administration.Module;
import fr.progilone.pgcn.domain.dto.user.AuthorizationDTO;
import fr.progilone.pgcn.domain.user.Authorization;
import fr.progilone.pgcn.repository.user.AuthorizationRepository;
import fr.progilone.pgcn.web.util.AuthorizationManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private AuthorizationRepository authorizationRepository;

    private AuthorizationService service;

    @BeforeEach
    public void setUp() {
        service = new AuthorizationService(authorizationRepository);
    }

    @Test
    public void testFindAllDTO() {
        final List<Authorization> authorizations = new ArrayList<>();
        final Authorization auth = new Authorization();
        auth.setCode("UC00-HAB001");
        auth.setIdentifier("1234");
        auth.setLabel("LABEL");
        auth.setModule(Module.USER);
        authorizations.add(auth);
        when(authorizationRepository.findAll()).thenReturn(authorizations);

        AuthorizationManager.setRequirements("UC00-HAB001", "UC00-HABAA1", "UC00-HABAA2");
        AuthorizationManager.setRequirements("UC00-HABZZ1", "UC00-HAB001", "UC00-HAB002");

        final List<AuthorizationDTO> actual = service.findAllDTO();

        assertEquals(1, actual.size());
        final AuthorizationDTO dto = actual.get(0);
        assertEquals(auth.getCode(), dto.getCode());
        assertEquals(auth.getIdentifier(), dto.getIdentifier());
        assertEquals(auth.getLabel(), dto.getLabel());
        assertEquals(auth.getModule().name(), dto.getModule());
        assertEquals(2, dto.getRequirements().size());
        assertEquals(1, dto.getDependencies().size());
    }

    @Test
    public void testFindAllWithRoles() {
        final List<Authorization> authorizations = new ArrayList<>();
        when(authorizationRepository.findAllWithRoles()).thenReturn(authorizations);

        final List<Authorization> actual = service.findAll();
        assertSame(authorizations, actual);
    }

    @Test
    public void testFindOne() {
        final Authorization authorization = new Authorization();
        final String identifier = "123";
        when(authorizationRepository.findOneWithRoles(identifier)).thenReturn(authorization);

        final Authorization actual = service.findOne(identifier);
        assertSame(authorization, actual);
    }

}
