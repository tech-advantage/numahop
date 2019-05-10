package fr.progilone.pgcn.service.administration.logs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

@Service
public class AdminLogsService {
    
    
    @Value("${logging.file}")
    private String logFileName; 
    
    /**
     * Récupération du fichier de log.
     * 
     * @param deliveryId
     * @return
     */
    public File getLogFile(@DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date) {
        final String fullName;
        if (date.equals(LocalDate.now())) {
            fullName = logFileName.concat(".log");
        } else {
            fullName = logFileName.concat(".").concat(date.toString()).concat(".log");
        }
        final Path root = Paths.get(fullName);
        if(root != null && root.toFile().canRead()) {
            return root.toFile();
        }
        return null;
    }

}
