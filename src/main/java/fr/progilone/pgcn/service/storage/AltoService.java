package fr.progilone.pgcn.service.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import net.sf.saxon.TransformerFactoryImpl;

@Service
public class AltoService {

    private static final Logger LOG = LoggerFactory.getLogger(AltoService.class);

    @Value("classpath:config/xsl/hocr2alto2.1.xsl")
    private Resource xslPath;

    @Value("${instance.libraries}")
    private String[] instanceLibraries;
    
    @Value("${services.archive.alto}")
    private String outputPath;

    public AltoService() {
    }

    @PostConstruct
    public void initialize() {
        
        // 1 disk space per library 
        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(outputPath, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }); 
    }

    /**
     * Creation du fichier alto.xml par transformation depuis un hocr.
     * 
     * @param hocr
     * @param prefix
     * @param libraryId
     */
    public void transformHocrToAlto(final File hocr, final String prefix, final String libraryId) {

        // Utilisation de Saxon car xslt2.0
        final TransformerFactory fact = TransformerFactoryImpl.newInstance();

        try (InputStream xslInput = xslPath.getInputStream()) {

            final Transformer transformer = fact.newTransformer(new StreamSource(xslInput));

            transformer.setURIResolver(new URIResolver() {
                @Override
                public Source resolve(final String href, final String base) throws TransformerException {
                    try {
                        return new StreamSource(this.getClass().getResource("/config/xsl/" + href).openStream());
                    } catch (final Exception e) {
                        LOG.error("Erreur avant la construction du fichier alto", e);
                        return null;
                    }
                }
            });

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            final File xmlAlto = new File(Paths.get(outputPath, libraryId, prefix).toFile(), "alto.xml");
            if (hocr != null && hocr.isFile()) {
                final Source hocrInput = new StreamSource(hocr);
                final StreamResult altoOutput = new StreamResult(xmlAlto);
                transformer.transform(hocrInput, altoOutput);
            }

        } catch (final IOException | TransformerException e) {
            LOG.error("Erreur lors de la construction du fichier alto", e);
            return;
        }

    }

}
