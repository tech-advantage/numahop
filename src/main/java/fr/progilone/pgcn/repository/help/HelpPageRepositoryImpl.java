package fr.progilone.pgcn.repository.help;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import fr.progilone.pgcn.domain.dto.help.HelpPageDto;
import fr.progilone.pgcn.domain.dto.help.QHelpPageDto;
import fr.progilone.pgcn.domain.help.HelpPageType;
import fr.progilone.pgcn.domain.help.QHelpPage;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpPageRepositoryImpl implements HelpPageRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<HelpPageDto> search(final List<String> modules, final List<HelpPageType> types, final String search) {
        final JPQLQuery baseQuery = new JPAQuery(em);

        final QHelpPage qhp = QHelpPage.helpPage;

        final BooleanBuilder builder = new BooleanBuilder();

        if (modules != null && !modules.isEmpty()) {
            builder.and(qhp.module.in(modules));
        }
        if (types != null) {
            builder.and(qhp.type.in(types));
        }
        if (StringUtils.isNotBlank(search)) {
            builder.and(qhp.title.contains(search).or(qhp.content.contains(search)));
        } else {
            builder.and(qhp.parent.isNull());
        }

        final List<HelpPageDto> result = baseQuery.from(qhp)
                                                  .where(builder.getValue())
                                                  .orderBy(qhp.type.asc())
                                                  .orderBy(qhp.module.asc())
                                                  .orderBy(qhp.rank.asc())
                                                  .orderBy(qhp.title.asc())
                                                  .list(new QHelpPageDto(qhp.identifier,
                                                                         qhp.title,
                                                                         qhp.rank,
                                                                         qhp.module,
                                                                         qhp.type,
                                                                         qhp.parent.identifier));

        if (StringUtils.isBlank(search)) {
            fillChildrenOf(result);
        }
        return result;
    }

    private void fillChildrenOf(final List<HelpPageDto> parents) {
        final List<HelpPageDto> children = findChildrenOf(parents);

        if (!children.isEmpty()) {
            final Map<String, List<HelpPageDto>> map = children.stream().collect(Collectors.groupingBy(HelpPageDto::getParent, Collectors.toList()));
            parents.forEach(p -> {
                if (map.get(p.getIdentifier()) != null) {
                    p.getChildren().addAll(map.get(p.getIdentifier()));
                }
            });
            fillChildrenOf(children);
        }

    }

    private List<HelpPageDto> findChildrenOf(final List<HelpPageDto> parents) {
        final List<String> parentIds = parents.stream().map(HelpPageDto::getIdentifier).collect(Collectors.toList());
        final JPQLQuery baseQuery = new JPAQuery(em);

        final QHelpPage qhp = QHelpPage.helpPage;

        return baseQuery.from(qhp)
                        .where(qhp.parent.identifier.in(parentIds))
                        .orderBy(qhp.type.asc())
                        .orderBy(qhp.module.asc())
                        .orderBy(qhp.rank.asc())
                        .orderBy(qhp.title.asc())
                        .list(new QHelpPageDto(qhp.identifier, qhp.title, qhp.rank, qhp.module, qhp.type, qhp.parent.identifier));

    }

    @Override
    public List<HelpPageDto> searchByTag(final String tag) {
        final JPQLQuery baseQuery = new JPAQuery(em);

        final QHelpPage qhp = QHelpPage.helpPage;

        final List<HelpPageDto> result = baseQuery.from(qhp)
                                                  .where(qhp.tag.contains(tag))
                                                  .list(new QHelpPageDto(qhp.identifier,
                                                                         qhp.title,
                                                                         qhp.rank,
                                                                         qhp.module,
                                                                         qhp.type,
                                                                         qhp.parent.identifier));

        return result;
    }

}
