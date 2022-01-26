package fr.progilone.pgcn.service.exchange;

import static fr.progilone.pgcn.domain.document.DocUnit.RightsEnum.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord.PropertyOrder;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.InternetArchiveCollectionService;
import fr.progilone.pgcn.service.administration.omeka.OmekaListService;
import fr.progilone.pgcn.service.document.DocUnitService;

/**
 * Méthodes générales pour l'import d'unités documentaires
 * <p>
 * Created by Sébastien on 23/12/2016.
 */
public abstract class AbstractImportConvertService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImportConvertService.class);

    @Autowired
    protected DocUnitService docUnitService;

    @Autowired
    protected OmekaListService omekaListService;

    @Autowired
    protected InternetArchiveCollectionService iaCollectionService;

    @Autowired
    protected CinesPACService cinesPACService;

    // Taille maximale des chaînes de caratères mappées sur des champs de DocUnit et BibliographicRecord
    protected static final int COL_MAX_WIDTH = 255;

    // Taille maximale des chaînes de caratères indexées, mappées sur des champs de DocUnit et BibliographicRecord
    protected static final int COL_MAX_WIDTH_FOR_KEYS = 191;

    protected static final String FIELD_DIGITAL_ID = "digitalId";
    protected static final String FIELD_IDENTIFIER = "identifier";
    protected static final String FIELD_LABEL = "label";
    protected static final String FIELD_PGCN_ID = "pgcnId";
    protected static final String FIELD_TITLE = "title";

    /**
     * Initialisation d'un {@link DocUnit}
     *
     * @param library
     * @param propertyOrder
     * @return
     */
    protected DocUnit initDocUnit(final Library library, final PropertyOrder propertyOrder) {
        final DocUnit docUnit = new DocUnit();
        docUnit.setLibrary(library);
        docUnit.setState(DocUnit.State.NOT_AVAILABLE);

        final PhysicalDocument physDoc = new PhysicalDocument();
        docUnit.getPhysicalDocuments().add(physDoc);
        physDoc.setDocUnit(docUnit);

        final BibliographicRecord bibRecord = new BibliographicRecord();
        bibRecord.setLibrary(library);
        bibRecord.setPropertyOrder(propertyOrder);
        docUnit.addRecord(bibRecord);

        return docUnit;
    }

    /**
     * Assignation des valeurs par défaut
     *
     * @param docUnit
     */
    protected void setDefaultValues(final DocUnit docUnit) {
        if (!docUnit.getRecords().isEmpty()) {
            final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
            // Copie du type
            bibRecord.getProperties()
                     .stream()
                     .filter(p -> StringUtils.equals("type", p.getType().getIdentifier()))
                     .map(DocProperty::getValue)
                     .findAny()
                     .ifPresent(docUnit::setType);
            // Copie des droits
            final DocUnit.RightsEnum rights =
                bibRecord.getProperties().stream().filter(p -> StringUtils.equals("rights", p.getType().getIdentifier())).map(p -> {
                    try {
                        return DocUnit.RightsEnum.valueOf(p.getValue());
                    } catch (final IllegalArgumentException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).findAny()
                         // par défaut
                         .orElse(TO_CHECK);
            docUnit.setRights(rights);
        }
    }

    /**
     * Initialisation des rangs de propriétés
     *
     * @param bibRecord
     */
    protected void setRanks(final BibliographicRecord bibRecord) {
        if (bibRecord.getPropertyOrder() == PropertyOrder.BY_PROPERTY_TYPE) {
            bibRecord.getProperties().stream().collect(Collectors.groupingBy(DocProperty::getType)).forEach((type, properties) -> {
                final int typeRank = type.getRank() != null ? type.getRank() : 0;
                for (int i = 0; i < properties.size(); i++) {
                    properties.get(i).setRank(typeRank * 10000 + i);
                }
            });
        }
    }

    /**
     * Création des {@link DocProperty} de bibRecord en fonction de la règle de mapping, et des valeurs récupérées
     *
     * @param bibRecord
     * @param typeId
     * @param propertyTypes
     * @param values
     * @param baseRank
     */
    protected void setDocProperty(final BibliographicRecord bibRecord,
                                  final String typeId,
                                  final Map<String, DocPropertyType> propertyTypes,
                                  final List<String> values,
                                  final Integer baseRank) {
        // Type de propriété
        final DocPropertyType propertyType = propertyTypes.get(typeId);
        if (propertyType == null) {
            LOG.error("Le type de propriété {} n'a pas été trouvé.", typeId);
        } else {
            setDocProperty(bibRecord, propertyType, values, baseRank);
        }
    }

    protected void setDocProperty(final BibliographicRecord bibRecord,
                                  final DocPropertyType propertyType,
                                  final List<String> values,
                                  final Integer baseRank) {
        int rank = 0;
        if (bibRecord.getPropertyOrder() == PropertyOrder.BY_PROPERTY_TYPE) {
            if (baseRank != null) {
                rank = baseRank * 10000;
            }
            rank +=
                bibRecord.getProperties().stream().filter(p -> StringUtils.equals(p.getType().getIdentifier(), propertyType.getIdentifier())).count();

        } else if (bibRecord.getPropertyOrder() == PropertyOrder.BY_CREATION) {
            rank += bibRecord.getProperties().size();
        }

        // Création des propriétés
        for (final String value : values) {
            final DocProperty property = new DocProperty();
            property.setType(propertyType);
            property.setValue(deleteUnwantedCrLf(value));
            property.setRank(rank++);
            bibRecord.addProperty(property);
        }
    }
    
    /**
     * tentative de fix pb saut de ligne 
     * notamment pour qq caracteres arabes entre crochets !!
     * 
     * @param value
     * @return
     */
    protected String deleteUnwantedCrLf(final String value) {
        return (value.replace("\n", " ")).replace("\r", "");
    }

    /**
     * Alimentation des champs d'un objet en fonction de la règle de mapping, et des valeurs récupérées
     *
     * @param object
     * @param fieldName
     * @param values
     */
    protected void setObjectField(final Object object, final String fieldName, final List<String> values) {
        if (StringUtils.isBlank(fieldName) || values.isEmpty()) {
            return;
        }
        try {
            // Vérification de la valeur actuelle (si la valeur est un type primitif, elle n'est jamais null...)
            final Object current = PropertyUtils.getSimpleProperty(object, fieldName);
            if (current != null && !PropertyUtils.getPropertyDescriptor(object, fieldName).getPropertyType().isPrimitive()) {
                LOG.debug("Le champ {} est déjà défini avec la valeur {} pour l'objet {}",
                          fieldName,
                          String.valueOf(current),
                          object.getClass().getName());
                return;
            }
            // Vérification des valeurs ignorées
            if (values.size() > 1) {
                LOG.debug("Il y a plus d'une valeur trouvée pour le champ {} ({}); seule la première sera récupérée.",
                          fieldName,
                          StringUtils.join(values, ", "));
            }
            final String firstValue = values.get(0);
            LOG.trace("{}.{} = {}", object.getClass().getName(), fieldName, firstValue);

            // Assignation de la nouvelle valeur
            final Class<?> fieldType = PropertyUtils.getPropertyType(object, fieldName);
            // String
            if (String.class.isAssignableFrom(fieldType)) {
                // largeur spécifique pour le PgcnID
                final int colMaxWidth = getColMaxWidth(fieldName);
                PropertyUtils.setSimpleProperty(object, fieldName, StringUtils.abbreviate(firstValue, colMaxWidth));
            }
            // Enum
            else if (Enum.class.isAssignableFrom(fieldType)) {
                if (firstValue != null) {
                    try {
                        final Enum value = Enum.valueOf((Class<? extends Enum>) fieldType, firstValue);
                        PropertyUtils.setSimpleProperty(object, fieldName, value);

                    } catch (final IllegalArgumentException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            // OmekaList
            else if (OmekaList.class.isAssignableFrom(fieldType)) {
                if (firstValue != null) {
                    try {
                        final OmekaList omekaList = omekaListService.findByName(firstValue);
                        if (omekaList != null) {
                            PropertyUtils.setSimpleProperty(object, fieldName, omekaList);
                        }
                    } catch (final IllegalArgumentException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            // InternetArchiveCollection
            else if (InternetArchiveCollection.class.isAssignableFrom(fieldType)) {
                if (firstValue != null) {
                    try {
                        final InternetArchiveCollection iaCollection = iaCollectionService.findByName(firstValue);
                        if (iaCollection != null) {
                            PropertyUtils.setSimpleProperty(object, fieldName, iaCollection);
                        }
                    } catch (final IllegalArgumentException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            // CinesPAC
            else if (CinesPAC.class.isAssignableFrom(fieldType)) {
                if (firstValue != null) {
                    try {
                        final CinesPAC cinesPAC = cinesPACService.findByName(firstValue);
                        if (cinesPAC != null) {
                            PropertyUtils.setSimpleProperty(object, fieldName, cinesPAC);
                        }
                    } catch (final IllegalArgumentException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            // Boolean
            else if (Objects.equals(Boolean.TYPE, fieldType) || Boolean.class.isAssignableFrom(fieldType)) {
                PropertyUtils.setSimpleProperty(object, fieldName, Boolean.parseBoolean(firstValue));
            }
            // Non géré
            else {
                LOG.warn("Le type {} de la propriété {} n'est pas géré dans le mapping", fieldType.getName(), fieldName);
            }

        } catch (final ReflectiveOperationException | IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    protected int getColMaxWidth(final String fieldName) {
        if (StringUtils.isNotBlank(fieldName)) {
            switch (fieldName) {
                case FIELD_DIGITAL_ID:
                case FIELD_PGCN_ID:
                    return COL_MAX_WIDTH_FOR_KEYS;
            }
        }
        return COL_MAX_WIDTH;
    }

    /**
     * Le champ de l'objet est-il renseigné ?
     *
     * @param object
     * @param fieldName
     */
    protected boolean hasObjectField(final Object object, final String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        try {
            // Vérification de la valeur actuelle
            final Object current = PropertyUtils.getSimpleProperty(object, fieldName);
            if (current != null) {
                final Class<?> fieldType = PropertyUtils.getPropertyType(object, fieldName);
                // String: pas vide
                return !String.class.isAssignableFrom(fieldType) || StringUtils.isNotBlank((String) current);
            }
        } catch (final ReflectiveOperationException | IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * La propriété est-elle renseignée ?
     *
     * @param bibRecord
     * @param typeId
     * @return
     */
    protected boolean hasDocProperty(final BibliographicRecord bibRecord, final String typeId) {
        return bibRecord.getProperties().stream().anyMatch(p -> StringUtils.equals(p.getType().getIdentifier(), typeId));
    }
}
