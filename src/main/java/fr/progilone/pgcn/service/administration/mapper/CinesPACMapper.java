package fr.progilone.pgcn.service.administration.mapper;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author jbrunet
 *         Créé le 19 avr. 2017
 */
@Mapper()
public interface CinesPACMapper {

    CinesPACMapper INSTANCE = Mappers.getMapper(CinesPACMapper.class);

    CinesPACDTO cinesPACToDto(CinesPAC conf);

    Set<CinesPACDTO> cinesPACToDtos(Set<CinesPAC> conf);
}
