package fr.progilone.pgcn.web.rest;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.security.DenyAll;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@DenyAll
public abstract class AbstractRestController {

    public <T> ResponseEntity<T> createResponseEntity(final T result) {
        return this.createResponseEntity(Optional.ofNullable(result));
    }

    public <T> ResponseEntity<T> createResponseEntity(final Optional<T> result) {
        return result.map(r -> new ResponseEntity<>(r, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    protected void writeResponseHeaderForDownload(final HttpServletResponse response,
                                                  final String contentType,
                                                  final Integer contentLength,
                                                  final String filename) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Cache-Control", "must-revalidate");

        if (contentLength != null) {
            response.setContentLength(contentLength);
        }
    }

    protected void writeResponseForDownload(final HttpServletResponse response,
                                            final File file,
                                            final String contentType,
                                            final String filename) throws PgcnTechnicalException {
        writeResponseHeaderForDownload(response, contentType, (int) FileUtils.sizeOf(file), filename);
        try {
            FileUtils.copyFile(file, response.getOutputStream());
        } catch (IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }
}
