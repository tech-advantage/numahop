package fr.progilone.pgcn.web.rest_int;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import fr.progilone.pgcn.web.rest.dto.LoggerDTO;

import com.codahale.metrics.annotation.Timed;


/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api_int")
public class LogsResource {

    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    @RequestMapping(value = "/logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<LoggerDTO> getList() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList().stream().map(LoggerDTO::new).collect(Collectors.toList());

    }

    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    @RequestMapping(value = "/logs", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void changeLevel(@RequestBody final LoggerDTO jsonLogger) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
}
