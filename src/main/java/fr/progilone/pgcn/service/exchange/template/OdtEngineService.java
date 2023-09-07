package fr.progilone.pgcn.service.exchange.template;

import fr.opensagres.odfdom.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.ImageFormat;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.template.loader.DefaultResourceLoader;
import fr.progilone.pgcn.service.exchange.template.loader.ResourceLoader;
import fr.progilone.pgcn.service.exchange.template.loader.ResourceName;
import fr.progilone.pgcn.service.exchange.template.loader.TemplateResourceLoader;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.collections.MapUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service gérant la génération de documents ODT avec le moteur de templates XDocReport
 *
 * @see <a href="https://github.com/opensagres/xdocreport/wiki">XDocReport Wiki</a>
 */
@Service
public class OdtEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(OdtEngineService.class);

    private final MessageService messageService;
    private final TemplateService templateService;

    private final List<ResourceLoader> resourceLoaders = new ArrayList<>();

    @Autowired
    public OdtEngineService(final MessageService messageService, final TemplateService templateService) {
        this.messageService = messageService;
        this.templateService = templateService;
    }

    @PostConstruct
    public void initialize() {
        resourceLoaders.add(new TemplateResourceLoader(templateService));   // #1. recherche dans la table des templates (exc_template)
        resourceLoaders.add(new DefaultResourceLoader("/templates/"));      // #2. recherche dans le classpath (ex. /templates/ReinitPassword)
    }

    /**
     * Génération d'un document PDF à partir d'un template ODT
     * Le résultat est un {@link Reader}
     *
     * @param templateName
     *            nom du template
     * @param library
     *            bibliothèque
     * @param parameters
     * @param imageParams
     * @return
     */
    public void generateDocumentPDF(final Name templateName,
                                    final Library library,
                                    final Map<String, Object> parameters,
                                    final Map<String, IImageProvider> imageParams,
                                    final OutputStream out,
                                    final FieldsMetadata fieldsMetadata) throws PgcnTechnicalException {
        LOG.debug("Génération du document {}, bibliothèque {} avec les paramètres {}", templateName, library.getIdentifier(), parameters.keySet());
        try {
            // Recherche du template
            final InputStream templateStream = getResourceStream(new ResourceName(templateName, library.getIdentifier()));

            // Pas d'image: on procède en une seule passe
            if (imageParams.isEmpty()) {
                generateDocumentPDF(templateStream, parameters, imageParams, out, fieldsMetadata);
            }
            // Gestion des images en 2 passes (pour traiter le cas d'images nommées dynamiquement, qui ne fonctionne pas en une seule passe)
            else {
                final ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
                generateDocumentODT(templateStream, parameters, null, tempOut, fieldsMetadata); // NOSONAR

                final ByteArrayInputStream in = new ByteArrayInputStream(tempOut.toByteArray());
                generateDocumentPDF(in, null, imageParams, out, null); // NOSONAR
            }
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Génération d'un document ODT à partir d'un template ODT
     * Le résultat est un {@link Reader}
     *
     * @param templateName
     *            nom du template
     * @param library
     *            bibliothèque
     * @param parameters
     * @param imageParams
     * @return
     */
    public void generateDocumentODT(final Name templateName,
                                    final Library library,
                                    final Map<String, Object> parameters,
                                    final Map<String, IImageProvider> imageParams,
                                    final OutputStream out) throws PgcnTechnicalException {
        try {
            LOG.debug("Génération du document {}, bibliothèque {} avec les paramètres {}", templateName, library.getIdentifier(), parameters.keySet());

            // Recherche du template
            final InputStream templateStream = getResourceStream(new ResourceName(templateName, library.getIdentifier()));

            // Pas d'image: on procède en une seule passe
            if (imageParams.isEmpty()) {
                generateDocumentODT(templateStream, parameters, imageParams, out, null);
            }
            // Gestion des images en 2 passes (pour traiter le cas d'images nommées dynamiquement, qui ne fonctionne pas en une seule passe)
            else {
                final ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
                generateDocumentODT(templateStream, parameters, null, tempOut, null); // NOSONAR

                final ByteArrayInputStream in = new ByteArrayInputStream(tempOut.toByteArray());
                generateDocumentODT(in, null, imageParams, out, null); // NOSONAR
            }
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Génération d'un document PDF
     *
     * @param templateStream
     * @param parameters
     * @param imageParams
     * @param out
     * @throws PgcnTechnicalException
     */
    private void generateDocumentPDF(final InputStream templateStream,
                                     final Map<String, Object> parameters,
                                     final Map<String, IImageProvider> imageParams,
                                     final OutputStream out,
                                     final FieldsMetadata fieldsMetadata) throws PgcnTechnicalException {
        try {
            // set Velocity template engine and cache it to the registry
            final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateStream, TemplateEngineKind.Velocity);

            final FieldsMetadata metadata;
            // Initialisation des métadonnées des champs
            if (fieldsMetadata == null) {
                metadata = new FieldsMetadata();
            } else {
                metadata = fieldsMetadata;
            }
            report.setFieldsMetadata(metadata);

            // Initialisation du contexte
            final IContext context = report.createContext();
            if (MapUtils.isNotEmpty(parameters)) {
                parameters.forEach(context::put);
            }

            // Initialisation des images
            if (MapUtils.isNotEmpty(imageParams)) {

                imageParams.forEach((name, provider) -> {
                    metadata.addFieldAsImage(name);
                    context.put(name, provider);
                });
            }

            // Initialisation des classes utilisataires
            context.put("Message", (Function<String, String>) code -> messageService.getMessage("condreport", code));

            // Génération du document
            final Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM);
            final PdfOptions pdfOptions = PdfOptions.create();
            pdfOptions.fontProvider(new ITextFontRegistry() {

                @Override
                protected String resolveFamilyName(final String familyName, final int style) {
                    if (isItalic(style) && isBold(style)) {
                        return "Helvetica-BoldOblique";
                    } else if (isItalic(style)) {
                        return "Helvetica-Oblique";
                    } else if (isBold(style)) {
                        return "Helvetica-Bold";
                    } else {
                        return "Helvetica";
                    }
                }
            });
            options.subOptions(pdfOptions);

            report.convert(context, options, out);

        } catch (IOException | XDocReportException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Génération d'un document ODT
     *
     * @param templateStream
     * @param parameters
     * @param imageParams
     * @param out
     * @throws PgcnTechnicalException
     */
    private void generateDocumentODT(final InputStream templateStream,
                                     final Map<String, Object> parameters,
                                     final Map<String, IImageProvider> imageParams,
                                     final OutputStream out,
                                     final FieldsMetadata fieldsMetadata) throws PgcnTechnicalException {
        try {
            // set Velocity template engine and cache it to the registry
            final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateStream, TemplateEngineKind.Velocity);

            final FieldsMetadata metadata;
            // Initialisation des métadonnées des champs
            if (fieldsMetadata == null) {
                metadata = new FieldsMetadata();
            } else {
                metadata = fieldsMetadata;
            }
            report.setFieldsMetadata(metadata);

            // Initialisation du contexte
            final IContext context = report.createContext();
            if (MapUtils.isNotEmpty(parameters)) {
                parameters.forEach(context::put);
            }

            // Initialisation des images
            if (MapUtils.isNotEmpty(imageParams)) {

                imageParams.forEach((name, provider) -> {
                    metadata.addFieldAsImage(name);
                    context.put(name, provider);
                });
            }

            // Initialisation des classes utilisataires
            context.put("Message", (Function<String, String>) code -> messageService.getMessage("condreport", code));

            // Génération du document
            report.process(context, out);

        } catch (IOException | XDocReportException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Recherche du template parmi les sources configurées.
     * Le 1er trouvé est renvoyé
     *
     * @param name
     * @return
     */
    private InputStream getResourceStream(final ResourceName name) throws IOException {
        return resourceLoaders.stream().map(loader -> {
            try {
                return loader.getResourceStream(name);
            } catch (final ResourceNotFoundException e) {
                return null;
            }
        })
                              .filter(Objects::nonNull)
                              .findFirst()
                              .orElseThrow(() -> new IOException("Le template " + name
                                                                 + " n'a pas été trouvé"));
    }

    /**
     * FileImageProvider pour lequel le type peut être défini manuellement
     */
    public static class TypedFileImageProvider extends FileImageProvider {

        private final ImageFormat imageFormat;

        public TypedFileImageProvider(final File imageFile, final String imageType, final boolean useImageSize) {
            super(imageFile, useImageSize);
            this.imageFormat = ImageFormat.valueOf(imageType);
        }

        @Override
        public ImageFormat getImageFormat() {
            return imageFormat;
        }
    }
}
