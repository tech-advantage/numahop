package fr.progilone.pgcn.repository.help;

import fr.progilone.pgcn.domain.dto.help.HelpPageDto;
import fr.progilone.pgcn.domain.help.HelpPageType;

import java.util.List;

public interface HelpPageRepositoryCustom {

    List<HelpPageDto> search(final List<String> modules, final List<HelpPageType> types, String search);

    List<HelpPageDto> searchByTag(String tag);
}
