package fr.progilone.pgcn.service.exchange.template.loader;

import java.io.InputStream;
import org.apache.velocity.exception.ResourceNotFoundException;

public interface ResourceLoader {

    InputStream getResourceStream(final ResourceName resourceName) throws ResourceNotFoundException;
}
