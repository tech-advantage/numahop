package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.SimpleListBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.service.lot.mapper.SimpleLotMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.train.mapper.SimpleTrainMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(uses = {SimpleDocUnitMapper.class, SimpleLotMapper.class, SimpleProjectMapper.class})
public abstract class SimpleBibliographicRecordMapper {

    public static final SimpleBibliographicRecordMapper INSTANCE = Mappers.getMapper(SimpleBibliographicRecordMapper.class);
    private static final SimpleTrainMapper INSTANCE_TRAIN = Mappers.getMapper(SimpleTrainMapper.class);

    @Mappings({@Mapping(target = "project", source = "docUnit.project"), @Mapping(target = "lot", source = "docUnit.lot")})
    public abstract SimpleListBibliographicRecordDTO docUnitToSimpleListDocUnitDTO(BibliographicRecord record);

    /**
     * Set train
     *
     * @param record
     * @param dto
     */
    @AfterMapping
    protected void updateDto(final BibliographicRecord record, @MappingTarget final SimpleListBibliographicRecordDTO dto) {
        final DocUnit doc = record.getDocUnit();
        if (doc != null) {
            // train
            final Set<PhysicalDocument> physDoc = doc.getPhysicalDocuments();
            if (!physDoc.isEmpty()) {
                SimpleTrainDTO stdto = INSTANCE_TRAIN.trainToSimpleTrainDTO(physDoc.iterator().next().getTrain());
                dto.setTrain(stdto);
            }
        }
    }
}
