package fr.progilone.pgcn.service.sample.mapper;

import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.document.mapper.DocPageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DocPageMapper.class,
                DeliveryMapper.class})
public interface SampleMapper {

    SampleMapper INSTANCE = Mappers.getMapper(SampleMapper.class);

    SampleDTO sampleToSampleDTO(Sample sample);

}
