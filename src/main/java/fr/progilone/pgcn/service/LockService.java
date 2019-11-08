package fr.progilone.pgcn.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.Lock;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.repository.LockRepository;
import fr.progilone.pgcn.security.SecurityUtils;

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

    /**
     * Acquisition d'un verrou sur une ressource
     *
     * @param object
     *            la ressource pour laquelle on souhaite acquérir un verrou ne doit pas être {@literal null}.
     * @param <T>
     *            type de la ressource
     * @throws SemanthequeLockException
     *             en cas d'echec lors de l'acquisition du verrou : entité actuellement verrouillée par un autre uager
     * @throws NullPointerException
     *             si la ressource est null
     */
    @Transactional
    public <T extends AbstractDomainObject> Lock acquireLock(final T object) throws PgcnLockException {
        Assert.notNull(object, "La ressource ne peut être null");
        Assert.notNull(object, "La ressource doit posséder un identifiant");
        final Optional<Lock> existingLockForAnotherUser = isCurrentlyLockedByAnotherUser(object);
        // Verrou expiré d'un autre utilisateur ou verrou de l'utilisateur courant (expiré ou non)
        // => On supprime le verrou et on en crée un nouveau
        if(!existingLockForAnotherUser.isPresent()) {
            lockRepository.deleteByIdentifier(object.getIdentifier());
            lockRepository.flush();
            final Lock newLock = new Lock(object.getIdentifier(), SecurityUtils.getCurrentLogin(), object.getClass().getName());
            return lockRepository.save(newLock);
            
        } else {
            throw new PgcnLockException(existingLockForAnotherUser.get());
        }
    }

    /**
     * Suppression d'un verrou sur une ressource
     *
     * @param object
     *            la ressource pour laquelle on souhaite acquérir un verrou ne doit pas être {@literal null}.
     * @param <T>
     *            type de la ressource
     * @throws SemanthequeLockException
     *             en cas d'echec lors de la suppression du verrou : entité actuellement verrouillée par un autre uager que l'utilisateur courant
     */
    @Transactional
    public <T extends AbstractDomainObject> void releaseLock(final T object) throws PgcnLockException {
        Assert.notNull(object, "La ressource ne peut être null");
        Assert.notNull(object, "La ressource doit posséder un identifiant");
        final Optional<Lock> currentlyLockedByAnotherUser = isCurrentlyLockedByAnotherUser(object);
        if(currentlyLockedByAnotherUser.isPresent()) {
            throw new PgcnLockException(currentlyLockedByAnotherUser.get());
        } else {
            lockRepository.deleteByIdentifier(object.getIdentifier());
        }
    }

    /**
     * Vérification si la ressource est disponible pour l'usager courant
     *
     * @param object la ressource pour laquelle on souhaite vérifier la présence d'un verrou ne doit pas être {@literal null}.
     * @param <T> type de la ressource
     * @throws SemanthequeLockException entité actuellement verrouillée par un autre uager
     */
    @Transactional
    public <T extends AbstractDomainObject> void checkLock(final T object) throws PgcnLockException {
        if (object.getIdentifier() != null) {
            final Optional<Lock> currentlyLockedByAnotherUser = isCurrentlyLockedByAnotherUser(object);
            if(currentlyLockedByAnotherUser.isPresent()) {
                throw new PgcnLockException(currentlyLockedByAnotherUser.get());
            }
        }
    }

    
    /**
     * le verrou est posé par un autre user + le timeout n'est pas passé
     *
     * @param object
     *            verrou
     * @return
     */
    private <T extends AbstractDomainObject> Optional<Lock> isCurrentlyLockedByAnotherUser(final T object) {
        final Lock lock = lockRepository.findByIdentifier(object.getIdentifier());
        if (lock != null) {
            if (!StringUtils.equals(lock.getLockedBy(), SecurityUtils.getCurrentLogin())
                && ChronoUnit.MINUTES.between(lock.getLockedDate(), LocalDateTime.now()) < LOCK_TIMEOUT) {
                return Optional.of(lock);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void releaseLocksOnLogout(final String userLogin) {
        // Libération des locks posés par le user au logout.
        lockRepository.deleteByLockedBy(userLogin);
    }
}
