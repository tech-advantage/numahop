package fr.progilone.pgcn.service.exchange.template.loader;

import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.InputStream;

public interface ResourceLoader {

    InputStream getResourceStream(final ResourceName resourceName) throws ResourceNotFoundException;
}
