package fr.progilone.pgcn.service.user;

import fr.progilone.pgcn.domain.dto.user.RoleDTO;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.user.RoleRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    private LibraryRepository libraryRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;

    private RoleService service;

    @Before
    public void setUp() {
        service = new RoleService(libraryRepository, roleRepository, userRepository);
    }

    @Test
    public void testFindAllDTO() {
        final List<Role> roles = new ArrayList<>();
        final Role role = new Role();
        role.setCode("CODE");
        role.setIdentifier("1234");
        role.setLabel("LABEL");
        roles.add(role);
        when(roleRepository.findAll()).thenReturn(roles);

        final List<RoleDTO> actual = service.findAllDTO();
        assertEquals(1, actual.size());
        final RoleDTO dto = actual.get(0);
        assertEquals(role.getCode(), dto.getCode());
        assertEquals(role.getIdentifier(), dto.getIdentifier());
        assertEquals(role.getLabel(), dto.getLabel());
    }

    @Test
    public void testFindAll() {
        final List<Role> roles = new ArrayList<>();
        when(roleRepository.findAllWithAuthorizations()).thenReturn(roles);

        final List<Role> actual = service.findAll();
        assertSame(roles, actual);
    }

    @Test
    public void testFindOne() {
        final Role role = new Role();
        final String identifier = "123";
        when(roleRepository.findOneWithAuthorizations(identifier)).thenReturn(role);

        final Role actual = service.findOne(identifier);
        assertSame(role, actual);
    }

    @Test
    public void testDelete() {
        final String identifier = "123";
        service.delete(identifier);
        verify(roleRepository).delete(identifier);
    }

    @Test
    public void testSave() {
        final String dup_identifier = "DUPLICATED_ID";
        final String dup_label = "DUPLICATED";
        final String label = "numahop";
        final Role dbRole = new Role();
        Role actual = null;

        when(roleRepository.findByCode(dup_label)).thenReturn(dbRole);
        when(roleRepository.findByCodeAndIdentifierNot(dup_label, dup_identifier)).thenReturn(dbRole);
        when(roleRepository.findOneByLabel(dup_label)).thenReturn(dbRole);
        when(roleRepository.findOneByLabelAndIdentifierNot(dup_label, dup_identifier)).thenReturn(dbRole);
        when(roleRepository.save(any(Role.class))).thenReturn(dbRole);

        // USER_ROLE_CODE_MANDATORY
        // USER_ROLE_LABEL_MANDATORY
        try {
            final Role role = new Role();

            actual = service.save(role);
            fail("USER_ROLE_CODE_MANDATORY and USER_ROLE_LABEL_MANDATORY expected");

        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, USER_ROLE_CODE_MANDATORY, USER_ROLE_LABEL_MANDATORY);
        }

        // USER_ROLE_UNIQUE_CODE_VIOLATION
        // USER_ROLE_UNIQUE_LABEL_VIOLATION
        // (create)
        try {
            final Role role = new Role();
            role.setCode(dup_label);
            role.setLabel(dup_label);

            actual = service.save(role);
            fail("USER_ROLE_UNIQUE_CODE_VIOLATION and USER_ROLE_UNIQUE_LABEL_VIOLATION expected");

        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, USER_ROLE_UNIQUE_CODE_VIOLATION, USER_ROLE_UNIQUE_LABEL_VIOLATION);
        }

        // USER_ROLE_UNIQUE_CODE_VIOLATION
        // USER_ROLE_UNIQUE_LABEL_VIOLATION
        // (update)
        try {
            final Role role = new Role();
            role.setIdentifier(dup_identifier);
            role.setCode(dup_label);
            role.setLabel(dup_label);

            actual = service.save(role);
            fail("USER_ROLE_UNIQUE_CODE_VIOLATION and USER_ROLE_UNIQUE_LABEL_VIOLATION expected");

        } catch (final PgcnValidationException e) {
            TestUtil.checkPgcnException(e, USER_ROLE_UNIQUE_CODE_VIOLATION, USER_ROLE_UNIQUE_LABEL_VIOLATION);
        }

        // Save ok
        try {
            final Role role = new Role();
            role.setCode(label);
            role.setLabel(label);

            actual = service.save(role);
            assertSame(dbRole, actual);

            // default values
            assertEquals(label.toUpperCase(), role.getCode());

        } catch (final PgcnValidationException e) {
            fail("Validation should be ok");
        }
    }
}
