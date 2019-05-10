package fr.progilone.pgcn.service.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

@Service
public class TesseractService {

    private static final Logger LOG = LoggerFactory.getLogger(TesseractService.class);

    private ExecutorService executorService;

    private AltoService altoService;

    public TesseractService(final AltoService altoService) {
        this.altoService = altoService;
    }

    protected String tessProcessPath;

    private boolean isConfigured;

    public void initialize(final String tessProcessPath) {
        this.tessProcessPath = tessProcessPath;
        this.isConfigured = getConfigurationState();
    }

    /**
     * Vérification de la bonne configuration
     */
    @SuppressWarnings("findsecbugs:COMMAND_INJECTION")
    protected boolean getConfigurationState() {

        try {
            final ProcessBuilder builder = new ProcessBuilder(tessProcessPath, "--version");
            builder.redirectErrorStream(true);
            final Process process = builder.start();

            try (final InputStream is = process.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader br = new BufferedReader(isr);) {

                String line;
                final StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                final String result = new String(sb);
                // check the version message
                if (result.contains("tesseract")) {
                    LOG.info("Tesseract detected : {}", StringUtils.abbreviate(result, 100));
                    return true;
                } else {
                    LOG.info("Tesseract not found");
                }
            }
        } catch (final IOException e) {
            LOG.error("unable to check Tesseract from System: {}", e.getMessage());
            LOG.trace(e.getMessage(), e);
        }
        return false;
    }

    protected boolean isConfigured() {
        return isConfigured;
    }

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Generation du PDF d'OCRisation et du Hocr éventuel.
     *
     * @param imgFile
     * @param parentDirectory
     * @param prefix
     * @param outPath
     * @param language
     * @param pdfs
     * @param generateHocr
     * @return
     * @throws PgcnTechnicalException
     */
    public Future<?> buildPdf(final File imgFile,
                              final String parentDirectory,
                              final String prefix,
                              final String outPath,
                              final String language,
                              final List<File> pdfs,
                              final boolean generateHocr,
                              final String libraryId) throws PgcnTechnicalException {

        if (!this.isConfigured) {
            throw new PgcnTechnicalException("Le service Tesseract n'est pas configuré, impossible de générer les pdf ocr-isés");
        }

        return executorService.submit(() -> {

            LOG.debug("Lancement de l'OCRisation pour le fichier {}", imgFile.getName());

            final ProcessBuilder builder =
                new ProcessBuilder(tessProcessPath, imgFile.getAbsolutePath(), outPath, "-l", language, "pdf", generateHocr ? "hocr" : "");

            final String outputName = parentDirectory + File.separatorChar + prefix;

            builder.redirectError(Redirect.INHERIT);
            builder.redirectOutput(Redirect.INHERIT);
            try {
                final Process process = builder.start();
                if (process.waitFor(2, TimeUnit.HOURS) && process.exitValue() == 0) {
                    // when pdf is done, write to the correct output
                    // only if the size is not null

                    final File pdfFile = new File(outputName + ".pdf");
                    if (pdfFile.exists() && pdfFile.canRead() && pdfFile.length() > 0L) {
                        pdfs.add(pdfFile);

                        LOG.debug("fichier pdf genere : {}", pdfFile.getName());

                        final File hocr = new File(outputName + ".hocr");
                        if (hocr.exists() && hocr.canRead()) {
                            altoService.transformHocrToAlto(hocr, prefix, libraryId);
                        }

                    } else {
                        LOG.info("[Tesseract] Unable to generate pdf and alto {}", pdfFile.getName());
                    }
                }
            } catch (final IOException | InterruptedException e) {
                LOG.error("[Tesseract] Unable to generate pdf and alto files", e);
            }

            LOG.debug("Fin de l'OCRisation pour le fichier {}", imgFile.getName());
        });
    }

}
