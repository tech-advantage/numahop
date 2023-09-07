package fr.progilone.pgcn.web.rest.administration;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Controller permettant de rediriger les appels vers /actuator/health pour ne pas exposer directement /actuator et avoir un accès authentifié
 */
@RestController
@RequestMapping(value = "/api/rest/health")
public class HealthController {

    private final RestTemplate restTemplate;
    private final String healthUrl;

    public HealthController(final RestTemplateBuilder restTemplateBuilder, @Value("${server.port}") final int serverPort) {
        this.healthUrl = "http://localhost:" + serverPort
                         + "/actuator/health";
        this.restTemplate = restTemplateBuilder.errorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(final ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(final ClientHttpResponse response) throws IOException {
                // Il n'y a jamais d'erreur donc rien à faire ici...
            }
        }).build();
    }

    @GetMapping
    @ResponseBody
    public String mirrorRest(final HttpServletRequest request) throws URISyntaxException {
        return restTemplate.getForEntity(healthUrl, String.class).getBody();
    }

}
