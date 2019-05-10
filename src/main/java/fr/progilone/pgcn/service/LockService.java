package fr.progilone.pgcn.service;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.Lock;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.repository.LockRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LockService {

    private static final Logger LOG = LoggerFactory.getLogger(LockService.class);
    private static final long LOCK_TIMEOUT = 60; // minutes

    private final LockRepository lockRepository;

    @Autowired
    public LockService(final LockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    @Scheduled(cron = "${cron.cleanLocks}")
    @Transactional
    public void cleanExpiredLocks() {
        LOG.info("Nettoyage des verrous arrivés à expiration...");
        lockRepository.deleteByLockedDateBefore(LocalDateTime.now().minusMinutes(LOCK_TIMEOUT));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T extends AbstractDomainObject> Lock acquireLock(final T object) throws PgcnLockException {
        Lock lock = lockRepository.findOne(object.getIdentifier());
        // un verrou existe sur cet objet
        if (lock != null) {
            // vérification du verrou
            checkLock(lock);
            // sinon on met à jour le verrou
        }
        // pas de verrou -> on le créé
        else {
            lock = new Lock();
            lock.setIdentifier(object.getIdentifier());
            lock.setClazz(object.getClass().getName());
        }
        lock.setLockedBy(SecurityUtils.getCurrentLogin());
        lock.setLockedDate(LocalDateTime.now());
        return lockRepository.save(lock);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T extends AbstractDomainObject> void releaseLock(final T object) throws PgcnLockException {
        final Lock lock = lockRepository.findOne(object.getIdentifier());
        if (lock != null) {
            // vérification du verrou
            checkLock(lock);
            // sinon on supprime le verrou
            lockRepository.delete(lock);
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T extends AbstractDomainObject> void checkLock(final T object) throws PgcnLockException {
        final Lock lock = lockRepository.findOne(object.getIdentifier());
        if (lock != null) {
            // vérification du verrou
            checkLock(lock);
        }
    }

    private void checkLock(final Lock lock) throws PgcnLockException {
        // le verrou est posé par un autre user + le timeout n'est pas passé -> exception
        if (!StringUtils.equals(lock.getLockedBy(), SecurityUtils.getCurrentLogin())
            && ChronoUnit.MINUTES.between(lock.getLockedDate(), LocalDateTime.now()) < LOCK_TIMEOUT) {

            throw new PgcnLockException(lock);
        }
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void releaseLocksOnLogout(final String userLogin) {
        // Libération des locks posés par le user au logout.
        lockRepository.deleteByLockedBy(userLogin);
    }
}
