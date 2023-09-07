package fr.progilone.pgcn.service.filesgestion;

import fr.progilone.pgcn.domain.dto.filesgestion.FilesGestionConfigDTO;
import fr.progilone.pgcn.domain.filesgestion.FilesGestionConfig;
import fr.progilone.pgcn.service.exportftpconfiguration.mapper.ExportFTPConfigurationMapper;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleLibraryMapper.class,
                ExportFTPConfigurationMapper.class})
public interface FilesGestionConfigMapper {

    FilesGestionConfigMapper INSTANCE = Mappers.getMapper(FilesGestionConfigMapper.class);

    FilesGestionConfigDTO configToConfigDto(FilesGestionConfig filesGestionConfig);

    List<FilesGestionConfigDTO> configsToConfigsDTO(List<FilesGestionConfig> configs);

    FilesGestionConfig configDtoToObj(FilesGestionConfigDTO dto);

}
