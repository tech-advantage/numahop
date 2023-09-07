package fr.progilone.pgcn.service.administration.mapper;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by Sébastien on 30/12/2016.
 */
@Mapper(uses = {SimpleLibraryMapper.class})
public interface MailboxConfigurationMapper {

    MailboxConfigurationMapper INSTANCE = Mappers.getMapper(MailboxConfigurationMapper.class);

    MailboxConfigurationDTO mailboxToDto(MailboxConfiguration mailboxConfiguration);

    List<MailboxConfigurationDTO> mailboxToDtos(Collection<MailboxConfiguration> mailboxConfiguration);
}
