package fr.progilone.pgcn.service.exchange.template.velocity;

import fr.progilone.pgcn.service.exchange.template.loader.ResourceName;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.ExtProperties;

/**
 * Ce {@link org.apache.velocity.runtime.resource.loader.ResourceLoader} par défaut est en fait un {@link ClasspathResourceLoader}
 * qui cherche à partir d'un répertoire donné, configuré avec ds.resource.loader.basepath.
 * <p>
 * Exemple de configuration:
 * <code>
 * Velocity.setProperty(Velocity.RESOURCE_LOADER, "ds");
 * Velocity.setProperty("ds.resource.loader.class", "fr.progilone.pgcn.service.exchange.template.velocity.DefaultResourceLoader");
 * Velocity.setProperty("ds.resource.loader.basepath", "/templates/");
 * </code>
 *
 * @see fr.progilone.pgcn.service.exchange.template.loader.DefaultResourceLoader
 */
public class DefaultResourceLoader extends ClasspathResourceLoader {

    private fr.progilone.pgcn.service.exchange.template.loader.DefaultResourceLoader delegate;

    @Override
    public void init(final ExtProperties configuration) {
        final String basePath = configuration.getString("basepath", "");
        delegate = new fr.progilone.pgcn.service.exchange.template.loader.DefaultResourceLoader(basePath);
        super.init(configuration);
    }

    @Override
    public Reader getResourceReader(final String name, final String encoding) {
        return new InputStreamReader(delegate.getResourceStream(new ResourceName(name)));
    }

    @Override
    public boolean isSourceModified(final Resource resource) {
        return super.isSourceModified(resource);
    }

    @Override
    public long getLastModified(final Resource resource) {
        return super.getLastModified(resource);
    }
}
