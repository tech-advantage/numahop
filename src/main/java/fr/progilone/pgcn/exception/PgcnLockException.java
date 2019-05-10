package fr.progilone.pgcn.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import fr.progilone.pgcn.domain.Lock;

public class PgcnLockException extends PgcnTechnicalException {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String objectId;
    private final String objectClass;
    private final String lockedBy;
    private final LocalDateTime lockedDate;

    public PgcnLockException(final Lock lock) {
        this.objectId = lock.getIdentifier();
        this.objectClass = lock.getClazz();
        this.lockedBy = lock.getLockedBy();
        this.lockedDate = lock.getLockedDate();
    }

    public String getObjectId() {
        return objectId;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public LocalDateTime getLockedDate() {
        return lockedDate;
    }

    public Map<String, Object> getLockDetails() {
        final Map<String, Object> details = new HashMap<>();
        details.put("objectId", objectId);
        details.put("objectClass", objectClass);
        details.put("lockedBy", lockedBy);
        details.put("lockedDate", lockedDate);
        return details;
    }

    @Override
    public String getMessage() {
        return String.format("L'objet [%s] %s est verrouillé par %s depuis le %s", objectClass, objectId, lockedBy, lockedDate.format(formatter));
    }
}
