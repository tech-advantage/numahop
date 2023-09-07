package fr.progilone.pgcn.service.storage;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AltoService {

    private static final Logger LOG = LoggerFactory.getLogger(AltoService.class);

    public static final String ALTO_XML_FILE = "alto.xml";
    public static final String OCR_TXT_FILE = "ocr.txt";

    @Value("classpath:config/xsl/hocr2alto2.1.xsl")
    private Resource xslAltoPath;

    @Value("classpath:config/xsl/hocr2text.1.xsl")
    private Resource xslTextPath;

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.archive.alto}")
    private String outputPath;

    @Value("${services.archive.text}")
    private String outputTextPath;

    private final FileStorageManager fm;

    public AltoService(final FileStorageManager fm) {
        this.fm = fm;
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

        try (final InputStream xslInput = xslAltoPath.getInputStream()) {

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

            final File xmlAlto = new File(Paths.get(outputPath, libraryId, prefix).toFile(), ALTO_XML_FILE);
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

    /**
     * Creation du fichier text par transformation depuis un hocr.
     *
     * @param hocr
     * @param prefix
     * @param libraryId
     */
    public void transformHocrToText(final File hocr, final String prefix, final String libraryId) {

        // Utilisation de Saxon car xslt2.0
        final TransformerFactory fact = TransformerFactoryImpl.newInstance();

        try (final InputStream xslInput = xslTextPath.getInputStream()) {

            final Transformer transformer = fact.newTransformer(new StreamSource(xslInput));

            transformer.setURIResolver(new URIResolver() {

                @Override
                public Source resolve(final String href, final String base) throws TransformerException {
                    try {
                        return new StreamSource(this.getClass().getResource("/config/xsl/" + href).openStream());
                    } catch (final Exception e) {
                        LOG.error("Erreur avant la construction du fichier text", e);
                        return null;
                    }
                }
            });

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            final File text = new File(Paths.get(outputTextPath, libraryId, prefix).toFile(), OCR_TXT_FILE);
            if (hocr != null && hocr.isFile()) {
                final Source hocrInput = new StreamSource(hocr);
                final StreamResult altoOutput = new StreamResult(text);
                transformer.transform(hocrInput, altoOutput);
            }

        } catch (final IOException | TransformerException e) {
            LOG.error("Erreur lors de la construction du fichier text", e);
            return;
        }

    }

    /**
     * Récupération du fichier alto.xml et text stocké
     * pour un docUnit donné
     *
     * @param docUnit
     * @return
     */
    public List<File> retrieveAlto(final String docUnit, final String libraryId, final boolean alto, final boolean text) {
        final List<File> altoAndTxt = new ArrayList<>();
        if (docUnit != null) {
            if (alto) {
                final Path rootAlto = Paths.get(outputPath, libraryId, docUnit);
                final File altoFile = fm.retrieveFile(rootAlto.toFile(), ALTO_XML_FILE);
                if (altoFile != null) {
                    altoAndTxt.add(altoFile);
                }
            }
            if (text) {
                final Path rootOcr = Paths.get(outputTextPath, libraryId, docUnit);
                final File textFile = fm.retrieveFile(rootOcr.toFile(), OCR_TXT_FILE);
                if (textFile != null) {
                    altoAndTxt.add(textFile);
                }
            }
            return altoAndTxt;
        }
        return null;
    }
}
