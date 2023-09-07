package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.DocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitWithLotDTO;
import fr.progilone.pgcn.service.administration.mapper.CinesPACMapper;
import fr.progilone.pgcn.service.administration.mapper.InternetArchiveCollectionMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaConfigurationMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaListMapper;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckResultMapper;
import fr.progilone.pgcn.service.library.mapper.LibraryMapper;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.project.mapper.ProjectMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.workflow.mapper.WorkflowMapper;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AutomaticCheckResultMapper.class,
                BibliographicRecordMapper.class,
                DigitalDocumentMapper.class,
                LibraryMapper.class,
                LotMapper.class,
                PhysicalDocumentMapper.class,
                SimpleProjectMapper.class,
                ProjectMapper.class,
                InternetArchiveCollectionMapper.class,
                CinesPACMapper.class,
                WorkflowMapper.class,
                OmekaListMapper.class,
                OmekaConfigurationMapper.class,
                OcrLanguageMapper.class})
public abstract class DocUnitMapper {

    public static final DocUnitMapper INSTANCE = Mappers.getMapper(DocUnitMapper.class);

    @Mappings({@Mapping(target = "parentIdentifier", ignore = true),
               @Mapping(target = "parentPgcnId", ignore = true),
               @Mapping(target = "parentLabel", ignore = true),
               @Mapping(target = "nbChildren", ignore = true),
               @Mapping(target = "omekaConfiguration", source = "doc.lot.omekaConfiguration")})
    public abstract DocUnitDTO docUnitToDocUnitDTO(DocUnit doc);

    public abstract SummaryDocUnitDTO docUnitToDocUnitSummaryDTO(DocUnit doc);

    public abstract SummaryDocUnitWithLotDTO docUnitToDocUnitSummaryWithLotDTO(DocUnit doc);

    /**
     * Alimentation des infos concernant la hiérarchie au statut AVAILABLE
     *
     * @param doc
     * @param dto
     */
    @AfterMapping
    protected void updateHierarchy(final DocUnit doc, @MappingTarget final DocUnitDTO dto) {
        final DocUnit parent = doc.getParent();
        if (parent != null && parent.getState() == DocUnit.State.AVAILABLE) {
            dto.setParentIdentifier(parent.getIdentifier());
            dto.setParentLabel(parent.getLabel());
            dto.setParentPgcnId(parent.getPgcnId());
        }
        final long nbChildren = doc.getChildren().stream().filter(ch -> ch.getState() == DocUnit.State.AVAILABLE).count();
        dto.setNbChildren((int) nbChildren);
        final long nbSiblings = doc.getSibling() == null ? 0
                                                         : doc.getSibling()
                                                              .getDocUnits()
                                                              .stream()
                                                              .filter(ch -> ch.getState() == DocUnit.State.AVAILABLE && !StringUtils.equals(ch.getIdentifier(),
                                                                                                                                            doc.getIdentifier()))
                                                              .count();
        dto.setNbSiblings((int) nbSiblings);
        // on en profite pour recuperer le statut
        dto.setState(doc.getState().name());
    }
}
