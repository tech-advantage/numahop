package fr.progilone.pgcn.service.exchange.marc;

import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.service.exchange.ExchangeHelper;
import fr.progilone.pgcn.service.exchange.marc.mapping.MarcKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.CharConverter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.converter.impl.Iso5426ToUnicode;
import org.marc4j.converter.impl.Iso6937ToUnicode;
import org.marc4j.converter.impl.UnicodeToAnsel;
import org.marc4j.converter.impl.UnicodeToIso5426;
import org.marc4j.converter.impl.UnicodeToIso6937;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fonctions d'aide à la manipulation de notices MARC
 * <p>
 * Created by Sebastien on 01/12/2016.
 */
public class MarcUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MarcUtils.class);

    private MarcUtils() {
    }

    /**
     * Conversion d'une chaine de caractères à l'aide du {@link CharConverter} passé en paramètre
     *
     * @param value
     * @param charConverter
     * @return
     */
    public static String convert(final String value, final CharConverter charConverter) {
        if (charConverter != null) {
            return charConverter.convert(value);
        }
        return value;
    }

    /**
     * Convertit un record au format marc/XML en String
     *
     * @param record
     * @param encoding
     * @return
     */
    public static String getRecordInXml(Record record, DataEncoding encoding) {
        try (final ByteArrayOutputStream ostream = new ByteArrayOutputStream()) {
            final MarcWriter writer = new MarcXmlWriter(ostream, StandardCharsets.UTF_8.name());
            writer.setConverter(MarcUtils.getCharConverterToUnicode(encoding));
            writer.write(record);
            writer.close();
            return ostream.toString(StandardCharsets.UTF_8.name());

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Construit et retourne un record depuis une chaine xml
     *
     * @param marcXml
     * @return
     */
    public static Record getRecord(String marcXml) {
        try (final InputStream stream = new ByteArrayInputStream(marcXml.getBytes(StandardCharsets.UTF_8))) {
            final MarcReader marcXmlReader = new MarcXmlReader(stream);

            if (marcXmlReader.hasNext()) {
                return marcXmlReader.next();
            }

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retourne le premier sous champ apparaissant dans le premier tag si il existe
     *
     * @param record
     * @param tag
     * @param subFieldCode
     * @return
     */
    public static String getSubfieldFirstValue(final Record record, final String tag, final char subFieldCode) {
        final VariableField field = record.getVariableField(tag);

        if (field instanceof DataField) {
            final DataField dataField = (DataField) field;
            final Subfield subField = dataField.getSubfield(subFieldCode);

            if (subField != null) {
                return subField.getData();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Renvoie une instance de CharConverter correspondant générant une chaîne dans l'encodage spécifié, à partir d'une chaîne l'unicode
     *
     * @param dataEncoding
     * @return
     */
    public static CharConverter getCharConverterFromUnicode(final DataEncoding dataEncoding) {
        switch (dataEncoding) {
            case ANSEL:
                return new UnicodeToAnsel();
            case ISO_5426:
                return new UnicodeToIso5426();
            case ISO_6937:
                return new UnicodeToIso6937();
            default:
                return null;
        }
    }

    /**
     * Renvoie une instance de CharConverter correspondant à l'encodage spécifié, et générant de l'unicode
     *
     * @param dataEncoding
     * @return
     */
    public static CharConverter getCharConverterToUnicode(final DataEncoding dataEncoding) {
        switch (dataEncoding) {
            case ANSEL:
                return new AnselToUnicode();
            case ISO_5426:
                return new Iso5426ToUnicode();
            case ISO_6937:
                return new Iso6937ToUnicode();
            default:
                return null;
        }
    }

    /**
     * Construction d'une liste de notices, où chaque notice correspond à un exemplaire
     *
     * @param record
     * @return
     */
    public static List<Record> splitRecordByMarcKeys(final Record record, final List<MarcKey> marcKeys) {
        if (CollectionUtils.isEmpty(marcKeys)) {
            return Collections.singletonList(record);
        }

        // Séparation des champs exemplaire des champs communs
        final MarcFactory marcFactory = MarcFactory.newInstance();
        final Map<String, List<VariableField>> itemFields = new HashMap<>();
        final List<VariableField> otherFields = new ArrayList<>();

        // Parcours des valeurs de la notice
        for (final VariableField recordField : record.getVariableFields()) {
            final String recordTag = recordField.getTag();
            DataField copyOfDataField = null;   // on garde une référence au datafield, pour ne pas le dupliquer

            // Recherche des clés qui correspondent au tag du champ courant
            final List<MarcKey> matchedKeys = marcKeys.stream()
                                                      .filter(key -> key.isXX() ? key.getTag().charAt(0) == recordTag.charAt(0)
                                                                                : key.getTag().equals(recordTag))
                                                      .collect(Collectors.toList());
            if (matchedKeys.isEmpty()) {
                otherFields.add(recordField);
            } else {
                // Champs de contrôle, ou champ entiers (key.code == null)
                for (final MarcKey key : matchedKeys) {
                    // Pas de sous-champ => on prend directement le champ
                    if (key.getCode() == null || recordField instanceof ControlField) {
                        itemFields.computeIfAbsent(key.getTag(), k -> new ArrayList<>()).add(recordField);
                    }
                }
                // Champs de données
                if (recordField instanceof DataField) {
                    final DataField f = (DataField) recordField;

                    for (final Subfield subfield : f.getSubfields()) {
                        if (matchedKeys.stream().noneMatch(key -> key.getCode() != null && key.getCode() == subfield.getCode())) {
                            continue;
                        }
                        // Si on retrouve une nouvelle fois le sous-champ key.code, on recréé un nouveau datafield
                        if (copyOfDataField == null || copyOfDataField.getSubfield(subfield.getCode()) != null) {
                            copyOfDataField = marcFactory.newDataField(f.getTag(), f.getIndicator1(), f.getIndicator2());
                            itemFields.computeIfAbsent(f.getTag(), k -> new ArrayList<>()).add(copyOfDataField);
                        }
                        final DataField fCopyOfDataField = copyOfDataField;
                        // Sous-champ correspondant à la clé courante
                        copyOfDataField.addSubfield(marcFactory.newSubfield(subfield.getCode(), subfield.getData()));
                        // Sous-champs ne faisant partie d'aucune clé de marcKeys
                        f.getSubfields()
                         .stream()
                         .filter(sub -> matchedKeys.stream().noneMatch(oth -> Character.compare(sub.getCode(), oth.getCode()) == 0))
                         .forEach(sub -> fCopyOfDataField.addSubfield(marcFactory.newSubfield(sub.getCode(), sub.getData())));
                    }
                }
            }
        }
        // Combinaison des différentes valeurs trouvées pour les champs de marcKeys
        final List<Map<String, VariableField>> itemFieldCombos = ExchangeHelper.cartesianProduct(itemFields);

        return itemFieldCombos.stream().map(fieldCombo -> {
            final Record partialRecord = marcFactory.newRecord();

            Stream.concat(fieldCombo.values().stream(), otherFields.stream()).map(f -> {
                if (f instanceof DataField) {
                    final DataField dataField = (DataField) f;
                    // champ
                    final DataField copyOfField = marcFactory.newDataField(f.getTag(), dataField.getIndicator1(), dataField.getIndicator2());
                    // sous-champs
                    dataField.getSubfields().forEach(subfield -> copyOfField.addSubfield(marcFactory.newSubfield(subfield.getCode(), subfield.getData())));
                    return copyOfField;

                } else {
                    return marcFactory.newControlField(f.getTag(), ((ControlField) f).getData());
                }
            }).forEach(partialRecord::addVariableField);

            return partialRecord;
        }).collect(Collectors.toList());
    }
}
