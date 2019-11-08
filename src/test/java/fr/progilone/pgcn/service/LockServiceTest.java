package fr.progilone.pgcn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.domain.Lock;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.repository.LockRepository;

@RunWith(MockitoJUnitRunner.class)
public class LockServiceTest {

    @Mock
    private LockRepository lockRepository;

    private LockService service;

    @Before
    public void setUp() throws Exception {
        service = new LockService(lockRepository);
    }

    @Test
    public void testAcquireLock_ShouldThrowException_whenExistingNonExpiredLockOnEntityForAnotherUser() {
        final String USER_ID = "user";
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                  "user",
                                                                  "azerty",
                                                                  Lang.FR,
                                                                  "LIB-001",
                                                                  Collections.emptyList(),
                                                                  false,
                                                                  User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");

        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "toto");
        lockOnWork.setIdentifier("lock");

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        try {
            service.acquireLock(lockedWork);
            fail("testAcquireLock_ShouldThrowException_whenExistingNonExpiredLockOnEntityForAnotherUser should have trow an Exception");
        } catch (final PgcnLockException e) {
            verify(lockRepository, never()).save(any(Lock.class));
        }
    }

    @Test
    public void testAcquireLock_ShouldAcquireLockAndRemoveExpiredLockifPresent_whenExistingExpiredLockOnEntity() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");

        final LocalDateTime lockedOn = LocalDateTime.of(2018,11,5,0,0);
        final Lock expiredLockOnWork = new Lock(lockedWork.getIdentifier(), "toto");
        ReflectionTestUtils.setField(expiredLockOnWork, "lockedDate", lockedOn);

        expiredLockOnWork.setIdentifier("lock");

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(expiredLockOnWork);
        final ArgumentCaptor<Lock> argument = ArgumentCaptor.forClass(Lock.class);

        service.acquireLock(lockedWork);

        verify(lockRepository, times(1)).deleteByIdentifier(lockedWork.getIdentifier());
        verify(lockRepository).save(argument.capture());
        assertEquals(lockedWork.getIdentifier(), argument.getValue().getIdentifier());
        assertEquals(userDetails.getLogin(), argument.getValue().getLockedBy());
    }

    @Test
    public void testAcquireLock_ShouldAcquireNewLock_whenNoExistingLockOnEntity() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit DocUnit = new DocUnit();
        DocUnit.setIdentifier("DocUnit");

        when(lockRepository.findByIdentifier(DocUnit.getIdentifier())).thenReturn(null);
        final ArgumentCaptor<Lock> argument = ArgumentCaptor.forClass(Lock.class);

        service.acquireLock(DocUnit);

        verify(lockRepository).save(argument.capture());
        assertEquals(DocUnit.getIdentifier(), argument.getValue().getIdentifier());
        assertEquals(userDetails.getLogin(), argument.getValue().getLockedBy());
    }

    @Test
    public void testReleaseLock_ShouldReleaseLock_whenExistingLockOnEntityForCurrentUser() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");

        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "user");

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        service.releaseLock(lockedWork);

        verify(lockRepository).deleteByIdentifier(lockedWork.getIdentifier());
    }

    @Test
    public void testReleaseLock_ShouldThrowException_whenExistingLockOnEntityForAnotherUser()  {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");
        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "toto");

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        try {
            service.releaseLock(lockedWork);
            fail("testReleaseLock_ShouldThrowException_whenExistingLockOnEntityForAnotherUser should have trow an Exception");
        } catch (final PgcnLockException e) {
            verify(lockRepository, never()).deleteByIdentifier(any());
        }
    }

    @Test(expected = PgcnLockException.class)
    public void testCheckLock_ShouldThrowException_whenExistingNonExpiredLockOnEntityForAnotherUser() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");
        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "toto");

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        service.checkLock(lockedWork);
    }

    @Test
    public void testCheckLock_ShouldNotThrowException_whenExistingExpiredLockOnEntityForAnotherUser() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");

        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "toto", LocalDateTime.now().minusYears(1));

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        service.checkLock(lockedWork);
    }

    @Test
    public void testCheckLock_ShouldNotThrowException_whenExistingExpiredLockOnEntityForCurrentUser() throws PgcnLockException {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "user",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        final DocUnit lockedWork = new DocUnit();
        lockedWork.setIdentifier("DocUnit");

        final Lock lockOnWork = new Lock(lockedWork.getIdentifier(), "user", LocalDateTime.now().minusYears(1));

        when(lockRepository.findByIdentifier(lockedWork.getIdentifier())).thenReturn(lockOnWork);

        service.checkLock(lockedWork);
    }
}

