package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.es.jackson.MappingJacksonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExchangeMappingService {

    private static final MappingJacksonMapper MAPPER = new MappingJacksonMapper();

    private final DocPropertyTypeService docPropertyTypeService;
    private final MappingService mappingService;

    @Autowired
    public ExchangeMappingService(final DocPropertyTypeService docPropertyTypeService, final MappingService mappingService) {
        this.docPropertyTypeService = docPropertyTypeService;
        this.mappingService = mappingService;
    }

    @Transactional(readOnly = true)
    public String exportMapping(final String mappingId) throws PgcnTechnicalException {
        final Mapping mapping = mappingService.findOne(mappingId);
        try {
            return MAPPER.mapToString(mapping);

        } catch (IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    @Transactional
    public Mapping importMapping(final MultipartFile file, final String toMappingId) throws PgcnTechnicalException {
        // Mapping destination
        final Mapping toMapping = mappingService.findOne(toMappingId);
        if (toMapping == null) {
            return null;
        }
        // Mapping à importer
        final Mapping importedMapping = readMapping(file);

        // Copie du mapping à importer dans le mapping destination
        toMapping.setJoinExpression(importedMapping.getJoinExpression());
        copyRules(importedMapping, toMapping);

        // Sauvegarde sans validation
        return mappingService.save(toMapping, false);
    }

    @Transactional
    public Mapping importNewMapping(final MultipartFile file, final String libraryId) throws PgcnTechnicalException {
        // Mapping destination
        final Mapping toMapping = new Mapping();
        // Mapping à importer
        final Mapping importedMapping = readMapping(file);

        final Library library = new Library();
        library.setIdentifier(libraryId);
        toMapping.setLibrary(library);

        // Copie du mapping à importer dans le mapping destination
        copyProperties(importedMapping, toMapping);
        copyRules(importedMapping, toMapping);

        // Sauvegarde sans validation
        return mappingService.save(toMapping, false);
    }

    private Mapping readMapping(final MultipartFile file) throws PgcnTechnicalException {
        final Mapping importedMapping;
        try (InputStream in = file.getInputStream()) {
            final String mappingJson = IOUtils.toString(in, StandardCharsets.UTF_8);
            importedMapping = MAPPER.mapToMapping(mappingJson);

        } catch (IOException e) {
            throw new PgcnTechnicalException(e);
        }
        return importedMapping;
    }

    private void copyProperties(final Mapping source, final Mapping destination) {
        destination.setLabel(source.getLabel());
        destination.setType(source.getType());
        destination.setJoinExpression(source.getJoinExpression());
    }

    private void copyRules(final Mapping source, final Mapping destination) {
        final List<DocPropertyType> properties = docPropertyTypeService.findAll();

        destination.getRules().clear();
        source.getRules()
              .stream()
              .sorted(Comparator.comparing(MappingRule::getPosition))
              // màj des DocPropertyType avec celles trouvée en bd
              .peek(rule -> {
                  final DocPropertyType property = rule.getProperty();
                  if (property != null) {
                      properties.stream()
                                // On importe les pptés à partir de leur libellé, l'identifiant pouvant être différent entre 2 installations
                                // différentes
                                .filter(p -> StringUtils.equalsIgnoreCase(p.getLabel(), property.getLabel()))
                                .findAny()
                                .ifPresent(rule::setProperty);
                  }
              })
              // Ajout des règles au mapping cible
              .forEach(rule -> {
                  rule.setMapping(destination);
                  destination.getRules().add(rule);
              });
    }
}
