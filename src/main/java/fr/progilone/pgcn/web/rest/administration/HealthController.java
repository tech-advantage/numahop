package fr.progilone.pgcn.web.rest.administration;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller permettant de rediriger les appels vers /actuator/health pour ne pas exposer directement /actuator et avoir un accès authentifié
 */
@RestController
@RequestMapping(value = "/api/rest/health")
public class HealthController {

    private static final String HEALTH_URL = "http://localhost:8080/actuator/health";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    @ResponseBody
    public String mirrorRest(final HttpServletRequest request) throws URISyntaxException {
        return restTemplate.getForEntity(new URI(HEALTH_URL), String.class).getBody();
    }

}
