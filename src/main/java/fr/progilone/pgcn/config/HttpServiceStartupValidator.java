package fr.progilone.pgcn.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Bean vérifiant si une URL est joignable. Sert à attendre qu'un service web soit disponible avant de lancer l'application, à faire dépendre d'un
 * autre bean via une relation depends-on dans spring.
 */
public class HttpServiceStartupValidator implements InitializingBean {

    /**
     * The default interval.
     */
    public static final int DEFAULT_INTERVAL = 1;

    /**
     * The default timeout.
     */
    public static final int DEFAULT_TIMEOUT = 60;

    private static final Logger LOG = LoggerFactory.getLogger(HttpServiceStartupValidator.class);

    private final String url;

    private int interval = DEFAULT_INTERVAL;

    private int timeout = DEFAULT_TIMEOUT;

    public HttpServiceStartupValidator(final String url) {
        super();
        this.url = url;
    }

    /**
     * Définit l'interval entre 2 essais (en secondes)
     * Par défaut {@value #DEFAULT_INTERVAL}.
     */
    public void setInterval(final int interval) {
        this.interval = interval;
    }

    /**
     * Définit la durée après laquelle une exception fatale sera levée (en secondes).
     * Par défaut {@value #DEFAULT_TIMEOUT}.
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    /**
     * Vérifie que l'url est accessible. Réessaie pendant 'timeout' secondes, toutes les 'interval' secondes
     */
    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Property 'url' is required");
        }

        try {
            boolean validated = false;
            final long beginTime = System.currentTimeMillis();
            final long deadLine = beginTime + TimeUnit.SECONDS.toMillis(this.timeout);

            final RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(interval)).setReadTimeout(Duration.ofSeconds(interval)).build();

            while (!validated && System.currentTimeMillis() < deadLine) {
                validated = tryToConnect(restTemplate, deadLine);

                if (!validated) {
                    TimeUnit.SECONDS.sleep(this.interval);
                }
            }

            if (!validated) {
                throw new ResourceAccessException("I/O error on HEAD request for " + url);
            }

            if (LOG.isInfoEnabled()) {
                final float duration = ((float) (System.currentTimeMillis() - beginTime)) / 1000;
                LOG.info("URL {} can be reached after {} seconds", url, duration);
            }
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean tryToConnect(final RestTemplate restTemplate, final long deadLine) {
        try {
            restTemplate.headForHeaders(url);
            return true;
        } catch (final RestClientException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("URL {} verification failed :", url, ex);
            }
            if (LOG.isInfoEnabled()) {
                final float rest = ((float) (deadLine - System.currentTimeMillis())) / 1000;
                if (rest > this.interval) {
                    LOG.info("URL {} not reachable - retry in {} seconds (fail in {} seconds) : {}", url, this.interval, rest, ex.getLocalizedMessage());
                }
            }
            return false;
        }
    }
}
