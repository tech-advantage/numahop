package fr.progilone.pgcn.service;

import fr.progilone.pgcn.domain.Lock;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.repository.LockRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LockServiceTest {

    @Mock
    private LockRepository lockRepository;

    private LockService service;

    @Before
    public void setUp() {
        service = new LockService(lockRepository);
        setUser();
    }

    @Test
    public void testAcquireLock() {
        final String id = "dcab0d3d-d47e-49d9-a941-5e85f5ee0d72";
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier(id);

        final Lock lock = new Lock();
        lock.setIdentifier(id);
        lock.setClazz(DocUnit.class.getName());
        lock.setLockedDate(LocalDateTime.now().minusMinutes(15));
        lock.setLockedBy("anne");

        when(lockRepository.findOne(docUnit.getIdentifier())).thenReturn(null, lock);
        when(lockRepository.save(any(Lock.class))).then(new ReturnsArgumentAt(0));

        // création du lock => ok
        try {
            final Lock actual = service.acquireLock(docUnit);

            assertEquals(id, actual.getIdentifier());
            assertEquals(DocUnit.class.getName(), actual.getClazz());
            assertEquals("anne", actual.getLockedBy());
            assertNotNull(actual.getLockedDate());

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur anne => ok
        try {
            lock.setLockedBy("anne");
            lock.setLockedDate(LocalDateTime.now().minusHours(2));

            final Lock actual = service.acquireLock(docUnit);

            assertEquals(id, actual.getIdentifier());
            assertEquals(DocUnit.class.getName(), actual.getClazz());
            assertEquals("anne", actual.getLockedBy());
            assertNotNull(actual.getLockedDate());

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur qqn d'autre, timeout dépassé => ok
        try {
            lock.setLockedBy("Mr. Chow");
            lock.setLockedDate(LocalDateTime.now().minusHours(2));
            final Lock actual = service.acquireLock(docUnit);

            assertEquals(id, actual.getIdentifier());
            assertEquals(DocUnit.class.getName(), actual.getClazz());
            assertEquals("anne", actual.getLockedBy());
            assertNotNull(actual.getLockedDate());

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur qqn d'autre, en cours => ko
        try {
            lock.setLockedBy("Ms. Chan");
            lock.setLockedDate(LocalDateTime.now().minusMinutes(30));
            service.acquireLock(docUnit);

            fail("testAcquireLock - unexpected success");

        } catch (PgcnLockException e) {
            assertEquals(id, e.getObjectId());
            assertEquals(DocUnit.class.getName(), e.getObjectClass());
            assertNotEquals("anne", e.getLockedBy());
            assertNotNull(e.getLockedDate());
        }
    }

    @Test
    public void testReleaseLock() {
        final String id = "dcab0d3d-d47e-49d9-a941-5e85f5ee0d72";
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier(id);

        final Lock lock = new Lock();
        lock.setIdentifier(id);
        lock.setClazz(DocUnit.class.getName());
        lock.setLockedDate(LocalDateTime.now().minusMinutes(15));
        lock.setLockedBy("anne");

        when(lockRepository.findOne(docUnit.getIdentifier())).thenReturn(null, lock);

        // pas de lock en cours
        try {
            service.releaseLock(docUnit);
            verify(lockRepository, never()).delete(any(Lock.class));

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur anne => ok
        try {
            lock.setLockedBy("anne");
            lock.setLockedDate(LocalDateTime.now().minusHours(2));

            service.releaseLock(docUnit);
            verify(lockRepository).delete(lock);

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur qqn d'autre, timeout dépassé => ok
        try {
            lock.setLockedBy("Mr. Chow");
            lock.setLockedDate(LocalDateTime.now().minusHours(2));

            service.releaseLock(docUnit);
            verify(lockRepository, times(2)).delete(lock);

        } catch (PgcnLockException e) {
            fail("testAcquireLock - unexpected failure: " + e.getMessage());
        }

        // lock sur qqn d'autre, en cours => ko
        try {
            lock.setLockedBy("Ms. Chan");
            lock.setLockedDate(LocalDateTime.now().minusMinutes(30));
            service.releaseLock(docUnit);

            fail("testAcquireLock - unexpected success");

        } catch (PgcnLockException e) {
            assertEquals(id, e.getObjectId());
            assertEquals(DocUnit.class.getName(), e.getObjectClass());
            assertNotEquals("anne", e.getLockedBy());
            assertNotNull(e.getLockedDate());
        }
    }

    private void setUser() {
        final CustomUserDetails userDetails = new CustomUserDetails("4efbfc73-3af8-4747-b730-8610374acf86",
                                                                    "anne",
                                                                    "azerty",
                                                                    Lang.FR,
                                                                    "LIB-001",
                                                                    Collections.emptyList(),
                                                                    false,
                                                                    User.Category.PROVIDER);
        final TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(userDetails, "credentials");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
