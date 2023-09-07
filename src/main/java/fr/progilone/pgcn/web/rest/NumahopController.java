package fr.progilone.pgcn.web.rest;

import com.codahale.metrics.annotation.Timed;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/numahop")
public class NumahopController {

    @Value("${info.build.artifact}")
    private String buildArtifact;

    @Value("${info.build.name}")
    private String buildName;

    @Value("${info.build.description}")
    private String buildDescription;

    @Value("${info.build.version}")
    private String buildVersion;

    @RequestMapping(method = RequestMethod.GET, params = {"build"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<String, Object>> getBuild() {
        final Map<String, Object> response = new HashMap<>();
        response.put("artifact", buildArtifact);
        response.put("name", buildName);
        response.put("description", buildDescription);
        response.put("version", buildVersion);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
