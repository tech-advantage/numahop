package fr.progilone.pgcn.service.sample.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.document.mapper.DocPageMapper;

@Mapper(uses = {DocPageMapper.class, DeliveryMapper.class})
public interface SampleMapper {
    
    SampleMapper INSTANCE = Mappers.getMapper(SampleMapper.class);

    SampleDTO sampleToSampleDTO(Sample sample);

}
