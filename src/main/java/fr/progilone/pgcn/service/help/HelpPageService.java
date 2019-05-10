package fr.progilone.pgcn.service.help;

import fr.progilone.pgcn.domain.dto.help.HelpPageDto;
import fr.progilone.pgcn.domain.dto.help.ModuleDto;
import fr.progilone.pgcn.domain.help.HelpPage;
import fr.progilone.pgcn.domain.help.HelpPageType;
import fr.progilone.pgcn.repository.help.HelpPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HelpPageService {

    private final HelpPageRepository helpPageRepository;

    @Autowired
    public HelpPageService(final HelpPageRepository helpPageRepository) {
        super();
        this.helpPageRepository = helpPageRepository;
    }

    public List<ModuleDto> search(final List<String> modules, final List<HelpPageType> types, final String search) {
        final List<HelpPageDto> pages = helpPageRepository.search(modules, types, search);
        final Map<ModuleDto, List<HelpPageDto>> map = pages.stream().collect(Collectors.groupingBy(p -> new ModuleDto(p.getModule(), p.getType()),
                                                                                                   Collectors.toList()));
        map.forEach((key, value) -> key.getPages().addAll(value));
        return map.keySet().stream().sorted(Comparator.comparing(ModuleDto::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }

    public HelpPage save(final HelpPage page) {
        return helpPageRepository.save(page);
    }

    public HelpPage findOneByIdentifier(final String id) {
        return helpPageRepository.findOneWithParent(id);
    }

    public List<String> findAllModules() {
        return helpPageRepository.findAllModules();
    }

    public void delete(final String id) {
        helpPageRepository.delete(id);
    }

    public HelpPageDto searchByTag(final String tag) {
        HelpPageDto result = null;
        final List<HelpPageDto> pages = helpPageRepository.searchByTag(tag);
        if (pages.size() > 1) {
            result = pages.stream().filter(p -> p.getType() == HelpPageType.CUSTOM).findAny().orElse(null);
            if (result == null) {
                result = pages.get(0);
            }
        } else if (!pages.isEmpty()) {
            result = pages.get(0);
        }
        return result;
    }

}
