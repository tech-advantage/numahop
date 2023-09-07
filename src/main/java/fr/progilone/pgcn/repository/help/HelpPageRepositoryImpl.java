package fr.progilone.pgcn.repository.help;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.dto.help.HelpPageDto;
import fr.progilone.pgcn.domain.dto.help.QHelpPageDto;
import fr.progilone.pgcn.domain.help.HelpPageType;
import fr.progilone.pgcn.domain.help.QHelpPage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class HelpPageRepositoryImpl implements HelpPageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public HelpPageRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<HelpPageDto> search(final List<String> modules, final List<HelpPageType> types, final String search) {
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

        final List<HelpPageDto> result = queryFactory.select(new QHelpPageDto(qhp.identifier, qhp.title, qhp.rank, qhp.module, qhp.type, qhp.parent.identifier))
                                                     .from(qhp)
                                                     .where(builder.getValue())
                                                     .orderBy(qhp.type.asc())
                                                     .orderBy(qhp.module.asc())
                                                     .orderBy(qhp.rank.asc())
                                                     .orderBy(qhp.title.asc())
                                                     .fetch();

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

        final QHelpPage qhp = QHelpPage.helpPage;

        return queryFactory.select(new QHelpPageDto(qhp.identifier, qhp.title, qhp.rank, qhp.module, qhp.type, qhp.parent.identifier))
                           .from(qhp)
                           .where(qhp.parent.identifier.in(parentIds))
                           .orderBy(qhp.type.asc())
                           .orderBy(qhp.module.asc())
                           .orderBy(qhp.rank.asc())
                           .orderBy(qhp.title.asc())
                           .fetch();

    }

    @Override
    public List<HelpPageDto> searchByTag(final String tag) {
        final QHelpPage qhp = QHelpPage.helpPage;

        final List<HelpPageDto> result = queryFactory.select(new QHelpPageDto(qhp.identifier, qhp.title, qhp.rank, qhp.module, qhp.type, qhp.parent.identifier))
                                                     .from(qhp)
                                                     .where(qhp.tag.contains(tag))
                                                     .fetch();

        return result;
    }

}
