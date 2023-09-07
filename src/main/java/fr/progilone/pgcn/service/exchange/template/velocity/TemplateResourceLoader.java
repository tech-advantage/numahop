package fr.progilone.pgcn.service.exchange.template.velocity;

import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.service.exchange.template.loader.ResourceName;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

/**
 * Ce {@link ResourceLoader} gère les templates Velocity stockés dans la table des Templates.
 * A la différence de {@link org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader}, la base est interrogées en passant par hibernate,
 * et en utilisant le pool de connexions de l'application
 * <p>
 * Exemple de configuration:
 * <code>
 * Velocity.setProperty(Velocity.RESOURCE_LOADER, "ds");
 * Velocity.setProperty("ds.resource.loader.class", "fr.progilone.pgcn.service.exchange.template.velocity.TemplateResourceLoader");
 * Velocity.setProperty("ds.resource.loader.service", templateService);
 * </code>
 *
 * @see fr.progilone.pgcn.service.exchange.template.loader.TemplateResourceLoader
 */
public class TemplateResourceLoader extends ResourceLoader {

    private fr.progilone.pgcn.service.exchange.template.loader.TemplateResourceLoader delegate;

    @Override
    public void init(final ExtProperties configuration) {
        final Object service = configuration.get("service");
        delegate = new fr.progilone.pgcn.service.exchange.template.loader.TemplateResourceLoader(service);
    }

    @Override
    public synchronized Reader getResourceReader(final String source, final String encoding) throws ResourceNotFoundException {
        return new InputStreamReader(delegate.getResourceStream(new ResourceName(source)));
    }

    @Override
    public boolean isSourceModified(final Resource resource) {
        return resource.getLastModified() != readLastModified(resource);
    }

    @Override
    public long getLastModified(final Resource resource) {
        return readLastModified(resource);
    }

    private long readLastModified(final Resource resource) {
        final Template template = delegate.findTemplateByName(new ResourceName(resource.getName()));
        final LocalDateTime lastModifiedDate = template.getLastModifiedDate();
        return lastModifiedDate != null ? Timestamp.valueOf(lastModifiedDate).getTime()
                                        : 0;
    }
}
