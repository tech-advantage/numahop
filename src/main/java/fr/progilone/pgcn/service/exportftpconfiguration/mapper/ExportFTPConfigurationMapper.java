package fr.progilone.pgcn.service.exportftpconfiguration.mapper;

import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.SimpleExportFTPConfDTO;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleLibraryMapper.class})
public interface ExportFTPConfigurationMapper {

    ExportFTPConfigurationMapper INSTANCE = Mappers.getMapper(ExportFTPConfigurationMapper.class);

    ExportFTPConfigurationDTO objectToDto(ExportFTPConfiguration config);

    ExportFTPConfiguration dtoToObject(ExportFTPConfigurationDTO dto);

    SimpleExportFTPConfDTO objectToSimpleDto(ExportFTPConfiguration config);

}
