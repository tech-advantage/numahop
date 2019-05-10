package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocSibling;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.User;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.io.IOException;

/**
 * Mapper personnalisé pour l'indexation dans le moteur de recherche
 */
public class PgcnEntityMapper implements EntityMapper {

    /**
     * {@link ObjectMapper} utilisé dans la sérialization / désérialisation dans le moteur de recherche
     */
    private final ObjectMapper objectMapper;

    public PgcnEntityMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);    // 1970-01-01T00:00:00.000+0000

        final SimpleModule module = new SimpleModule();
        // Serializer
        module.addSerializer(BibliographicRecord.class, new BibliographiRecordSerializer());
        module.addSerializer(CinesPAC.class, new CinesPACSerializer());
        module.addSerializer(CinesReport.class, new CinesReportSerializer());
        module.addSerializer(ConditionReportDetail.class, new ConditionReportDetailSerializer());
        module.addSerializer(ConditionReport.class, new ConditionReportSerializer());
        module.addSerializer(Delivery.class, new DeliverySerializer());
        module.addSerializer(Description.class, new DescriptionSerializer());
        module.addSerializer(DescriptionProperty.class, new DescriptionPropertySerializer());
        module.addSerializer(DescriptionValue.class, new DescriptionValueSerializer());
        module.addSerializer(DocProperty.class, new DocPropertySerializer());
        module.addSerializer(DocPropertyType.class, new DocPropertyTypeSerializer());
        module.addSerializer(DocUnit.class, new DocUnitSerializer());
        module.addSerializer(InternetArchiveCollection.class, new InternetArchiveCollectionSerializer());
        module.addSerializer(InternetArchiveReport.class, new InternetArchiveReportSerializer());
        module.addSerializer(Library.class, new LibrarySerializer());
        module.addSerializer(Lot.class, new LotSerializer());
        module.addSerializer(PhysicalDocument.class, new PhysicalDocumentSerializer());
        module.addSerializer(Project.class, new ProjectSerializer());
        module.addSerializer(Train.class, new TrainSerializer());
        module.addSerializer(User.class, new UserSerializer());
        // Deserializer
        module.addDeserializer(DocSibling.class, new NullDeserializer<>(DocSibling.class));

        objectMapper.registerModule(module)
                    // LocalDate, ...
                    .registerModule(new JavaTimeModule());
    }

    /**
     * Mapping Object -> String
     *
     * @param object
     * @return
     * @throws IOException
     */
    @Override
    public String mapToString(final Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Mapping String -> Object
     *
     * @param source
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T mapToObject(final String source, final Class<T> clazz) throws IOException {
        return objectMapper.readValue(source, clazz);
    }
}
