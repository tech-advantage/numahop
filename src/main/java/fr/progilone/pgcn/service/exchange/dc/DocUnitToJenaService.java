package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Sébastien on 23/12/2016.
 */
@Service
public class DocUnitToJenaService {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitToJenaService.class);

    @Value("${export.rdf.default_uri}")
    private String defaultUri;

    /**
     * Convertit l'unité documentaire en RDFXML, et écrit le résultat dans l'OutputStream passé en paramètre
     *
     * @param out
     * @param docUnit
     * @param rdfProperties
     * @param superType
     * @throws IOException
     */
    public void convert(final OutputStream out, final DocUnit docUnit, final Map<String, Property> rdfProperties, final DocPropertyType.DocPropertySuperType superType)
                                                                                                                                                                        throws IOException {

        final Model model = getModel(superType);
        final String uri = getUri(docUnit);

        // Alimentation du modèle RDF
        for (final BibliographicRecord record : docUnit.getRecords()) {
            final Resource resource = model.createResource(uri + record.getIdentifier());

            // Parcours des propriétés de l'unité documentaires
            for (final DocProperty docProp : record.getProperties()) {
                final Property rdfProperty = rdfProperties.get(docProp.getType().getIdentifier());

                if (rdfProperty != null) {
                    resource.addProperty(rdfProperty, docProp.getValue());
                }
            }
        }
        // Écriture du modèle RDF
        RDFDataMgr.write(out, model, RDFFormat.RDFXML);
        model.close();
    }

    /**
     * Convertit l'unité documentaire en RDFXML, et retourne le résultat sous la forme d'une chaîne de caractères
     *
     * @param docUnit
     * @param superType
     * @return
     * @throws IOException
     */
    public void convert(final OutputStream out, final DocUnit docUnit, final DocPropertyType.DocPropertySuperType superType) throws IOException {
        convert(out, docUnit, getRdfPropertiesByName(superType), superType);
    }

    /**
     * Convertit l'unité documentaire en RDFXML, et retourne le résultat sous la forme d'une chaîne de caractères
     *
     * @param docUnit
     * @param superType
     * @return
     * @throws IOException
     */
    public String convert(final DocUnit docUnit, final DocPropertyType.DocPropertySuperType superType) throws IOException {

        try (final OutputStream out = new ByteArrayOutputStream(); final OutputStream bufOut = new BufferedOutputStream(out)) {

            convert(bufOut, docUnit, superType);
            return out.toString();  // retourne le contenu du ByteArrayOutputStream sous forme de String
        }
    }

    private String getUri(DocUnit docUnit) {
        String uri = docUnit.getLibrary() != null ? docUnit.getLibrary().getWebsite()
                                                  : null;
        if (StringUtils.isBlank(uri)) {
            uri = defaultUri;
        }
        uri = StringUtils.appendIfMissing(uri, "/");
        return uri;
    }

    /**
     * Initialisation d'un modèle RDF Jena
     *
     * @param superType
     * @return
     */
    private Model getModel(final DocPropertyType.DocPropertySuperType superType) {
        final Model model = ModelFactory.createDefaultModel();
        if (superType == DocPropertyType.DocPropertySuperType.DCQ) {
            model.setNsPrefix("dcterms", DCTerms.getURI());
        }
        return model;
    }

    /**
     * Création d'une Map nom ppté => ppté à partir du super-type passé en paramètre
     *
     * @return
     */
    public static Map<String, Property> getRdfPropertiesByName(final DocPropertyType.DocPropertySuperType superType) {
        final Class<?>[] vocabularies;

        // Vocabulaires par super-type de doc-propriétés
        if (superType == DocPropertyType.DocPropertySuperType.DC) {
            vocabularies = new Class[] {DC.class};
        } else if (superType == DocPropertyType.DocPropertySuperType.DCQ) {
            vocabularies = new Class[] {DCTerms.class};
        } else {
            return new HashMap<>();
        }
        // Propriétés Jena
        return getRdfPropertiesByName(vocabularies);
    }

    /**
     * Création d'une Map nom ppté => ppté à partir des vocabulaires passés en paramètres (org.apache.jena.vocabulary)
     *
     * @param vocabularies
     * @return
     */
    public static Map<String, Property> getRdfPropertiesByName(final Class<?>... vocabularies) {
        final Map<String, Property> rdfProperties = new HashMap<>();

        for (final Class<?> vocabulary : vocabularies) {
            for (final Field field : vocabulary.getFields()) {
                if (field.getType() == Property.class) {
                    try {
                        rdfProperties.put(field.getName(), (Property) field.get(null));
                    } catch (IllegalAccessException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
        return rdfProperties;
    }
}
