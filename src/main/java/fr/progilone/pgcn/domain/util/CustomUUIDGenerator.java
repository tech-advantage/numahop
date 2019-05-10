package fr.progilone.pgcn.domain.util;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * CustomUUIDGenerator returns the current identifier if it is defined,
 * or generate a new one with UUIDGenerator
 * 
 * @author Sébastien DITER
 */
public class CustomUUIDGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        if (object != null && object instanceof AbstractDomainObject) {
            String identifier = ((AbstractDomainObject) object).getIdentifier();

            if (identifier != null) {
                return identifier;
            }
        }
        return super.generate(session, object);
    }

}
