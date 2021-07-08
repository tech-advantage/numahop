package fr.progilone.pgcn.service.exchange.ead;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Chronitem;
import fr.progilone.pgcn.domain.jaxb.ead.Did;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import fr.progilone.pgcn.domain.jaxb.ead.Eventgrp;
import fr.progilone.pgcn.domain.jaxb.ead.Note;
import fr.progilone.pgcn.domain.jaxb.ead.Publicationstmt;

/**
 * Created by Sébastien on 16/05/2017.
 */
public class EadCParser {

    private static final Logger LOG = LoggerFactory.getLogger(EadCParser.class);

    public static final String FIELD_C = "c"; // élément C courant
    public static final String FIELD_EADHEADER = "eadheader";   // entête du fichier EAD
    public static final String FIELD_ROOT = "root"; // élément C racine

    public static final String ATTR_CONTENT = "content";
    public static final String ATTR_LIST = "addressOrChronlistOrList";
    public static final String ATTR_NORMAL = "normal";

    public static final String PREFIX_ALL = "all";

    /**
     * Entête du fichier EAD
     */
    private final Eadheader eadheader;
    /**
     * Élément {@link C} principal, de plus haut niveau
     */
    private final C root;
    /**
     * Éléments {@link C} enfant
     */
    private final List<C> cLeaves = new ArrayList<>();
    /**
     * Parent / identifiant de l'enfant
     */
    private final Map<String, C> parentMap = new HashMap<>();

    public EadCParser(final Eadheader eadheader, final C root) {
        this.eadheader = eadheader;
        this.root = root;
        parseC(this.root);
    }

    public Eadheader getEadheader() {
        return eadheader;
    }

    public C getRoot() {
        return root;
    }

    public List<C> getcLeaves() {
        return cLeaves;
    }

    public Map<String, C> getParentMap() {
        return parentMap;
    }

    /**
     * Retourne l'élément racine de la branche allant jusqu'à c
     *
     * @param c
     * @return
     */
    public C getBranch(final C c) {
        if (c == null) {
            return null;
        }
        C current = c;
        C newC = null;

        do {
            final C copy = copy(current);
            if (newC != null) {
                copy.getTheadAndC().add(newC);
            }
            newC = copy;

            current = parentMap.get(current.getId());
        } while (current != null);

        return newC;
    }

    /**
     * Créé une copie de c, avec les même attributs que l'original, mais sans sa hiérarchie
     *
     * @param c
     * @return
     */
    private C copy(final C c) {
        final C newC = new C();
        newC.setHead(c.getHead());
        newC.setDid(c.getDid());
        newC.getMDescFull().addAll(c.getMDescFull());
        newC.setLevel(c.getLevel());
        newC.setOtherlevel(c.getOtherlevel());
        newC.setEncodinganalog(c.getEncodinganalog());
        newC.setId(c.getId());
        newC.setAltrender(c.getAltrender());
        newC.setAudience(c.getAudience());
        newC.setTpattern(c.getTpattern());
        return newC;
    }

    /**
     * Recherche des valeurs de fullField dans l'objet c ou dans l'entête EAD
     * Si aucune valeur n'est trouvée dans c, on recherche dans son parent
     *
     * @param c
     * @param fullField
     *         si le champ commence par "all:", recherche des valeur dans toute la hiérarchie des éléments C, sans se limiter au premier niveau renseigné
     * @return
     */
    public List<?> getValues(final C c, final String fullField) {
        final boolean allFlag = fullField.startsWith(PREFIX_ALL + ":");
        return getValues(c, allFlag ? fullField.substring(PREFIX_ALL.length() + 1) : fullField, allFlag);
    }

    /**
     * Recherche des valeurs de fullField dans l'objet c ou dans l'entête EAD
     * Si aucune valeur n'est trouvée dans c, on recherche dans son parent
     *
     * @param c
     * @param fullField
     * @param allFlag
     *         recherche des valeur dans toute la hiérarchie des éléments C, sans se limiter au premier niveau renseigné
     * @return
     */
    private List<?> getValues(final C c, final String fullField, final boolean allFlag) {
        // Entête EAD
        if (StringUtils.equals(FIELD_EADHEADER, fullField)) {
            return Collections.singletonList(eadheader);
        }
        // Élément C principal
        if (StringUtils.equals(FIELD_ROOT, fullField)) {
            return Collections.singletonList(root);
        }
        // Valeurs de l'entête du fichier EAD
        if (StringUtils.startsWith(fullField, FIELD_EADHEADER + ".")) {
            return EadCParser.getObjectValues(eadheader, fullField.substring(FIELD_EADHEADER.length() + 1));
        }
        // Valeurs de l'élément C principal
        if (StringUtils.startsWith(fullField, FIELD_ROOT + ".")) {
            return EadCParser.getObjectValues(root, fullField.substring(FIELD_ROOT.length() + 1));
        }
        if (c == null) {
            return Collections.emptyList();
        }
        // Élément C courant
        if (StringUtils.equals(FIELD_C, fullField)) {
            return Collections.singletonList(c);
        }
        // Valeurs de l'élément C courant
        if (StringUtils.startsWith(fullField, FIELD_C + ".")) {
            return getValues(c, fullField.substring(FIELD_C.length() + 1), allFlag);
        }
        // On recherche une valeur dans la hiérarchie de l'élément C courant:
        // - Recherche des valeurs sur l'objet C courant
        List<?> values = EadCParser.getObjectValues(c, fullField);
        // - Recherche des valeurs sur l'objet C parent
        if ((allFlag || values.isEmpty()) && parentMap.containsKey(c.getId())) {
            final C parent = parentMap.get(c.getId());
            final List<?> parentValues = getValues(parent, fullField, allFlag);
            // si allFlag, on concatène les valeurs courante avec celles des parents
            values = allFlag ? ListUtils.union(parentValues, values) : parentValues;
        }
        return values;
    }

    /**
     * Appelle getObjectValues, et renvoie:
     * - null si aucune valeur n'a été trouvée
     * - l'objet d'indice 0 si la liste ne contient qu'un élément
     * - la liste elle même sinon
     *
     * @param o
     * @param fullField
     * @return
     */
    public static Object getObjectValue(final Object o, final String fullField) {
        final List<?> values = getObjectValues(o, fullField);
        if (values.isEmpty()) {
            return null;
        } else if (values.size() == 1) {
            return values.get(0);
        } else {
            return values;
        }
    }

    /**
     * Recherche les valeurs de fullField dans l'objet o
     *
     * @param o
     * @param fullField
     * @return
     */
    public static List<?> getObjectValues(final Object o, final String fullField) {
        // Valeurs incorrectes
        if (o == null) {
            return Collections.emptyList();
        }
        // Champ vide
        else if (StringUtils.isBlank(fullField)) {
            return Collections.singletonList(o);
        }
        try {
            // Champ, sous-champ
            final int pos = fullField.indexOf('.');
            final String field;
            final String subField;
            if (pos < 0) {
                field = fullField;
                subField = null;
            } else {
                field = fullField.substring(0, pos);
                subField = fullField.substring(pos + 1);
            }

            // Attribut de l'objet
            if (PropertyUtils.isReadable(o, field)) {
                final Object value = PropertyUtils.getSimpleProperty(o, field);

                if (value != null) {
                    // on renvoie bien une liste d'objets
                    List<?> values = value instanceof List ? (List) value : Collections.singletonList(value);
                    // extraction des valeurs de JAXBElements, filtrage des valeurs valides
                    values = values.stream().map(EadCParser::unwrapJAXBElement).filter(Objects::nonNull).collect(Collectors.toList());
                    // Valeurs de enfants
                    return getListValues(values, subField);
                }
                return Collections.emptyList();
            }

            // Recherche des valeurs dans des attributs spécifiques
            final List<?> values = EadCParser.getNextValues(o);

            // Valeurs des enfants
            if (!values.isEmpty()) {
                return getListValues(values, field, subField);
            }
        } catch (final ReflectiveOperationException | IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * Renvoie les éléments de o sur lesquels chercher les valeurs
     *
     * @param o
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static List<?> getNextValues(final Object o) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (o == null) {
            return Collections.emptyList();
        }

        final List<?> values;
        // C
        if (o instanceof C) {
            values = ((C) o).getMDescFull();
        }
        // Chronitem
        else if (o instanceof Chronitem) {
            final Chronitem chronitem = (Chronitem) o;
            final List<Object> l = new ArrayList<>();
            l.add(chronitem.getDate());
            l.add(chronitem.getEvent());
            l.add(chronitem.getEventgrp());
            values = l;
        }
        // Did
        else if (o instanceof Did) {
            values = ((Did) o).getMDid();
        }
        // Eventgrp
        else if (o instanceof Eventgrp) {
            values = ((Eventgrp) o).getEvent();
        }
        //  List
        else if (o instanceof fr.progilone.pgcn.domain.jaxb.ead.List) {
            values = ((fr.progilone.pgcn.domain.jaxb.ead.List) o).getItem();
        }
        // Note
        else if (o instanceof Note) {
            values = ((Note) o).getMBlocks();
        }
        // Publicationstmt
        else if (o instanceof Publicationstmt) {
            values = ((Publicationstmt) o).getPublisherOrDateOrAddress();
        }
        // Classe possédant une propriété addressOrChronlistOrList
        else if (PropertyUtils.isReadable(o, ATTR_LIST)) {
            values = (List<?>) PropertyUtils.getSimpleProperty(o, ATTR_LIST);
        }
        // Classe possédant une propriété content (content étant une liste d'éléments sérialisables: texte, jaxbelement
        else if (PropertyUtils.isReadable(o, ATTR_CONTENT)) {
            values = (List<?>) PropertyUtils.getSimpleProperty(o, ATTR_CONTENT);
        }
        // Cas non géré
        else {
            values = Collections.emptyList();
        }
        return values;
    }

    /**
     * Si o est un {@link JAXBElement}, extraction de sa valeur
     *
     * @param o
     * @return
     */
    public static Object unwrapJAXBElement(final Object o) {
        if (o instanceof JAXBElement) {
            final JAXBElement jaxbElement = (JAXBElement) o;
            return jaxbElement.isNil() ? null : jaxbElement.getValue();
        }
        return o;
    }

    /**
     * Recherche les valeurs de subField dans les sous-object field pour chaque élément de objects
     *
     * @param objects
     * @param field
     * @param subField
     * @return
     */
    private static List<?> getListValues(final List<?> objects, final String field, final String subField) {
        return objects.stream()
                      .map(EadCParser::unwrapJAXBElement)
                      .filter(Objects::nonNull)
                      .filter(f -> StringUtils.equalsIgnoreCase(f.getClass().getSimpleName(), field))
                      .flatMap(f -> EadCParser.getObjectValues(f, subField).stream())
                      .collect(Collectors.toList());
    }

    /**
     * Recherche les valeurs de fullField dans les sous-object field pour chaque élément de objects
     *
     * @param objects
     * @param fullField
     * @return
     */
    private static List<?> getListValues(final List<?> objects, final String fullField) {
        return objects.stream()
                      .map(EadCParser::unwrapJAXBElement)
                      .filter(Objects::nonNull)
                      .flatMap(f -> EadCParser.getObjectValues(f, fullField).stream())
                      .collect(Collectors.toList());
    }

    /**
     * Initialisation des attributs de cette classe
     *
     * @param c
     */
    private void parseC(final C c) {
        final List<C> cChildren = c.getTheadAndC().stream().filter(C.class::isInstance).map(C.class::cast).collect(Collectors.toList());
        if (cChildren.isEmpty()) {
            cLeaves.add(c);
        } else {
            cChildren.forEach(child -> {
                parentMap.put(child.getId(), c);
                parseC(child);
            });
        }
    }
}
