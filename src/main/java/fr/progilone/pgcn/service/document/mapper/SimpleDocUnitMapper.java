package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.MinimalListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.lot.mapper.SimpleLotMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.train.mapper.SimpleTrainMapper;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleLibraryMapper.class,
                SimpleProjectMapper.class,
                SimpleLotMapper.class})
public abstract class SimpleDocUnitMapper {

    public static final SimpleDocUnitMapper INSTANCE = Mappers.getMapper(SimpleDocUnitMapper.class);
    private static final SimpleTrainMapper INSTANCE_TRAIN = Mappers.getMapper(SimpleTrainMapper.class);

    public abstract SimpleDocUnitDTO docUnitToSimpleDocUnitDTO(DocUnit doc);

    public abstract SimpleListDocUnitDTO docUnitToSimpleListDocUnitDTO(DocUnit doc);

    public abstract MinimalListDocUnitDTO docUnitToMinimalListDocUnitDTO(DocUnit doc);

    /**
     * Alimentation des infos concernant la hiérarchie au statut AVAILABLE
     *
     * @param doc
     * @param dto
     */
    @AfterMapping
    protected void updateDto(final DocUnit doc, @MappingTarget final SimpleListDocUnitDTO dto) {
        // train
        final Set<PhysicalDocument> physDoc = doc.getPhysicalDocuments();
        if (!physDoc.isEmpty()) {
            final SimpleTrainDTO stdto = INSTANCE_TRAIN.trainToSimpleTrainDTO(physDoc.iterator().next().getTrain());
            dto.setTrain(stdto);
        }
        // parent
        final DocUnit parent = doc.getParent();
        if (parent != null && parent.getState() == DocUnit.State.AVAILABLE) {
            dto.setParentIdentifier(parent.getIdentifier());
            dto.setParentLabel(parent.getLabel());
            dto.setParentPgcnId(parent.getPgcnId());
        }
        // presence d'une notice
        dto.setHasRecord(!doc.getRecords().isEmpty());
        // statut du digitalDocument
        if (!doc.getDigitalDocuments().isEmpty()) {
            dto.setDigitalDocumentStatus(doc.getDigitalDocuments().stream().findFirst().get().getStatus().name());
        }

    }

    @AfterMapping
    protected void updateSimpleDto(final DocUnit doc, @MappingTarget final SimpleDocUnitDTO dto) {

        // parent
        final DocUnit parent = doc.getParent();
        if (parent != null && parent.getState() == DocUnit.State.AVAILABLE) {
            dto.setParentIdentifier(parent.getIdentifier());
        }

    }

}
