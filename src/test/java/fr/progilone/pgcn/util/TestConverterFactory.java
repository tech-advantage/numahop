package fr.progilone.pgcn.util;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import org.springframework.core.convert.converter.Converter;

public class TestConverterFactory {

    /**
     * Retourne un Converter de String en AbstractDomainObject,
     * utilisé pour tester les contrôleur qui prennent un AbstractDomainObject en paramètre d'une requête GET
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends AbstractDomainObject> Converter<String, T> getConverter(final Class<T> clazz) {
        return source -> {
            try {
                final T object = clazz.newInstance();
                object.setIdentifier(source);
                return object;

            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}
