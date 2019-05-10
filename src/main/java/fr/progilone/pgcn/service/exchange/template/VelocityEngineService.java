package fr.progilone.pgcn.service.exchange.template;

import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.template.loader.ResourceName;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service gérant la génération de documents avec le moteur de templates Velocity
 */
@Service
public class VelocityEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(VelocityEngineService.class);

    // Nom des configurations des ResourceLoader de Velocity
    private static final String PGCN_RSC_LOADER = "pgcn";
    private static final String DEFAULT_RSC_LOADER = "default";

    private final TemplateService templateService;

    @Autowired
    public VelocityEngineService(final TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostConstruct
    public void initialize() {
        /*
         * Chargement des templates depuis:
         * -> la base de données (exc_template + stockage local)
         * -> la source par défaut (classpath, répertoire /templates/)
         */
        Velocity.setProperty(Velocity.RESOURCE_LOADER, StringUtils.joinWith(",", PGCN_RSC_LOADER, DEFAULT_RSC_LOADER));
        Velocity.setProperty(PGCN_RSC_LOADER + ".resource.loader.class",
                             "fr.progilone.pgcn.service.exchange.template.velocity.TemplateResourceLoader");
        Velocity.setProperty(PGCN_RSC_LOADER + ".resource.loader.service", templateService);
        Velocity.setProperty(DEFAULT_RSC_LOADER + ".resource.loader.class",
                             "fr.progilone.pgcn.service.exchange.template.velocity.DefaultResourceLoader");
        Velocity.setProperty(DEFAULT_RSC_LOADER + ".resource.loader.basepath", "/templates/");
        Velocity.init();    // Singleton model
    }

    /**
     * Génération d'un document à partir d'un template
     * Le résultat est un {@link Reader}
     *
     * @param templateName
     *         nom du template
     * @param library
     *         bibliothèque
     * @param parameters
     * @return
     */
    public void generateDocument(final Name templateName, final Library library, final Map<String, Object> parameters, final OutputStream out) throws
                                                                                                                                               IOException {
        LOG.debug("Génération du document {}, bibliothèque {} avec les paramètres {}", templateName, library.getIdentifier(), parameters);

        // Initialisation du contexte
        final VelocityContext context = new VelocityContext();
        if (parameters != null) {
            parameters.forEach(context::put);
        }

        // Génération du document
        try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            final String resourceName = new ResourceName(templateName, library.getIdentifier()).getResourceName();
            Velocity.mergeTemplate(resourceName, StandardCharsets.UTF_8.name(), context, writer);
        }
    }

    /**
     * Génération d'un document à partir d'un template
     * Le résultat est une chaine de caractères
     *
     * @param templateName
     *         nom du template
     * @param library
     *         bibliothèque
     * @param parameters
     * @return
     */
    public String generateDocument(final Name templateName, final Library library, final Map<String, Object> parameters) throws IOException {
        LOG.debug("Génération du document {}, bibliothèque {} avec les paramètres {}", templateName, library.getIdentifier(), parameters);

        // Initialisation du contexte
        final VelocityContext context = new VelocityContext();
        if (parameters != null) {
            parameters.forEach(context::put);
        }

        // Génération du document
        try (StringWriter writer = new StringWriter()) {
            final String resourceName = new ResourceName(templateName, library.getIdentifier()).getResourceName();
            Velocity.mergeTemplate(resourceName, StandardCharsets.UTF_8.name(), context, writer);
            return writer.getBuffer().toString();
        }
    }

    /**
     * Génération d'un document à partir d'une expression
     * Le résultat est une chaine de caractères
     *
     * @param expression
     * @param parameters
     * @return
     */
    public String evaluateExpression(final String expression, final Map<String, Object> parameters) throws IOException {
        LOG.debug("Évaluation de l'expression {} avec les paramètres {}", expression, parameters);

        // Initialisation du contexte
        final VelocityContext context = new VelocityContext();
        if (parameters != null) {
            parameters.forEach(context::put);
        }

        // Évaluation de l'expression
        try (StringWriter out = new StringWriter()) {
            Velocity.evaluate(context, out, "expression", expression);
            return out.getBuffer().toString();
        }
    }
}
