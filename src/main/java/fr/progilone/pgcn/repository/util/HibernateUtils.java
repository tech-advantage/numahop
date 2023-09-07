package fr.progilone.pgcn.repository.util;

import org.hibernate.proxy.HibernateProxy;

public class HibernateUtils {

    private HibernateUtils() {
    }

    /**
     * Conversion du proxy hibernate vers l'objet utilisé réellement
     *
     * @param entity
     * @return retourne l'objet réel
     */
    @SuppressWarnings("unchecked")
    public static <T> T unproxy(final T entity) {
        if (entity instanceof HibernateProxy) {
            final HibernateProxy proxy = (HibernateProxy) entity;
            return (T) proxy.getHibernateLazyInitializer().getImplementation();
        } else {
            return entity;
        }
    }

}
