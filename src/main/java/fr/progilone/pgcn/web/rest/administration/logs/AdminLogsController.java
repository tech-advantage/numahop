package fr.progilone.pgcn.web.rest.administration.logs;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.administration.logs.AdminLogsService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;

@RestController
@RequestMapping(value = "/api/rest/downloadlogsfile")
public class AdminLogsController extends AbstractRestController {
    
    private final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.FRENCH);
    
    private final AccessHelper accessHelper;
    private final AdminLogsService adminLogsService;
    
    public AdminLogsController(final AccessHelper accessHelper, final AdminLogsService adminLogsService) {
        this.accessHelper = accessHelper;
        this.adminLogsService = adminLogsService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"logFile"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    //@RolesAllowed(DEL_HAB0)
    public ResponseEntity<?> getLogFile(final HttpServletResponse response, 
                                        @RequestParam(value = "dtFile") final String dtFile) throws PgcnTechnicalException {
        
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (!accessHelper.checkCurrentUser(currentUser).get()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        LocalDate dateFile;
        if (dtFile == null) {
            dateFile = LocalDate.parse(LocalDate.now().format(dtFormat), dtFormat);
        } else {
            dateFile = LocalDate.parse(dtFile, dtFormat);
        }
        final File log = adminLogsService.getLogFile(dateFile);
        if (log == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, log, MediaType.TEXT_PLAIN_VALUE, log.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    
}
