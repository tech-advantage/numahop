package fr.progilone.pgcn.service.exchange.oaipmh;

import fr.progilone.pgcn.domain.jaxb.oaipmh.OAIPMHerrorType;
import fr.progilone.pgcn.domain.jaxb.oaipmh.OAIPMHtype;
import fr.progilone.pgcn.domain.jaxb.oaipmh.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Client OAI-PMH
 *
 * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html">The Open Archives Initiative Protocol for Metadata Harvesting</a>
 */
@Service
public class OaiPmhService {

    private static final Logger LOG = LoggerFactory.getLogger(OaiPmhService.class);

    /**
     * @param baseUrl
     * @param prefix
     * @param identifier
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord">GetRecord</a>
     */
    public Optional<OAIPMHtype> getRecord(final String baseUrl, final String prefix, final String identifier) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "GetRecord").addParameter("metadataPrefix", prefix).addParameter("identifier", identifier);
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param baseUrl
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify">Identify</a>
     */
    public Optional<OAIPMHtype> identify(final String baseUrl) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "Identify");
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param baseUrl
     * @param prefix
     * @param from
     *            yyyy-MM-dd
     * @param until
     *            yyyy-MM-dd
     * @param set
     * @param token
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">ListIdentifiers</a>
     */
    public Optional<OAIPMHtype> listIdentifiers(final String baseUrl, final String prefix, final String from, final String until, final String set, final String token) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "ListIdentifiers");
            if (token != null) {
                uriBuilder.addParameter("resumptionToken", token);

            } else {
                uriBuilder.addParameter("metadataPrefix", prefix);

                if (from != null) {
                    uriBuilder.addParameter("from", from);
                }
                if (until != null) {
                    uriBuilder.addParameter("until", until);
                }
                if (set != null) {
                    uriBuilder.addParameter("set", set);
                }
            }
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param baseUrl
     * @param identifier
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListMetadataFormats">ListMetadataFormats</a>
     */
    public Optional<OAIPMHtype> listMetadataFormats(final String baseUrl, final String identifier) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "ListMetadataFormats");
            if (identifier != null) {
                uriBuilder.addParameter("identifier", identifier);
            }
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param baseUrl
     * @param prefix
     * @param from
     * @param until
     * @param set
     * @param token
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">ListRecords</a>
     */
    public Optional<OAIPMHtype> listRecords(final String baseUrl, final String prefix, final String from, final String until, final String set, final String token) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "ListRecords");
            if (token != null) {
                uriBuilder.addParameter("resumptionToken", token);

            } else {
                uriBuilder.addParameter("metadataPrefix", prefix);

                if (from != null) {
                    uriBuilder.addParameter("from", from);
                }
                if (until != null) {
                    uriBuilder.addParameter("until", until);
                }
                if (set != null) {
                    uriBuilder.addParameter("set", set);
                }
            }
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param baseUrl
     * @param token
     * @return
     * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets">ListSets</a>
     */
    public Optional<OAIPMHtype> listSets(final String baseUrl, final String token) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl).addParameter("verb", "ListSets");
            if (token != null) {
                uriBuilder.addParameter("resumptionToken", token);
            }
            return get(uriBuilder.toString());

        } catch (final JAXBException | IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Appel HTTP GET, avec gestion du statut de la réponse
     *
     * @param url
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    private Optional<OAIPMHtype> get(final String url) throws IOException, JAXBException {
        LOG.trace("Requête sur l'URL {}", url);
        final HttpGet get = new HttpGet(url);
        final HttpClient client = HttpClientBuilder.create().build();

        final HttpResponse response = client.execute(get);
        final int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode >= 200 && statusCode < 300) {
            if (response.getEntity() != null) {
                final OAIPMHtype oaiPmh = parseResponse(response);

                if (checkOaiPmhResponse(oaiPmh, url)) {
                    return Optional.of(oaiPmh);
                }
            }
        } else {
            LOG.error("La requête {} a échoué avec l'erreur {}: {}", url, statusCode, response.getStatusLine().getReasonPhrase());
        }
        return Optional.empty();
    }

    /**
     * Parse la réponse HTTP pour récupérer un {@link OAIPMHtype}
     *
     * @param response
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    private OAIPMHtype parseResponse(final HttpResponse response) throws JAXBException, IOException {
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class, fr.progilone.pgcn.domain.jaxb.oaidc.ObjectFactory.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final Object content = unmarshaller.unmarshal(response.getEntity().getContent());
        return ((JAXBElement<OAIPMHtype>) content).getValue();
    }

    /**
     * Vérification des erreurs retournées par le service OAI-PMH
     *
     * @param oaiPmh
     * @param url
     * @return
     */
    private boolean checkOaiPmhResponse(final OAIPMHtype oaiPmh, final String url) {
        if (CollectionUtils.isNotEmpty(oaiPmh.getError())) {
            LOG.error("La requête {} a échoué sur le(s) erreur(s) suivante(s): ", url);

            for (final OAIPMHerrorType error : oaiPmh.getError()) {
                LOG.error("{}: {}", error.getCode(), error.getValue());
            }
            return false;
        }
        return true;
    }
}
