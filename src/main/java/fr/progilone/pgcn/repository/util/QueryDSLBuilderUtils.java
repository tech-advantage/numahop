package fr.progilone.pgcn.repository.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.security.SecurityUtils;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public final class QueryDSLBuilderUtils {

    private static final String INITIAL_OTHER = "OTHER";

    private QueryDSLBuilderUtils() {
    }

    /**
     * Filtrage par initiale
     */
    public static void addFilterForInitiale(final BooleanBuilder builder, final String initiale, final StringPath path) {
        if (StringUtils.isNotBlank(initiale)) {
            BooleanExpression initialeFilter;

            if (StringUtils.equals(initiale, INITIAL_OTHER)) {
                IntStream.range('a', 'z').mapToObj(ch -> path.startsWithIgnoreCase(String.valueOf(ch))).forEach(builder::andNot);
            } else {
                initialeFilter = path.startsWithIgnoreCase(initiale);
                builder.and(initialeFilter);
            }
        }
    }

    /**
     * Filtrage des prestataires (si l'utilisateur courant est un prestataire)
     */
    public static void addAccessFilters(final BooleanBuilder builder,
                                        final QLibrary qLibrary,
                                        final QLot qLot,
                                        final QProject qProject,
                                        final QLibrary qAssociatedLibrary,
                                        final QUser qAssociatedUser,
                                        final List<String> libraries,
                                        final List<String> providers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();

        // Prestataire: voit les projets / lots sur lesquels il est prestataires + les données de sa bibliothèque
        if (currentUser.getCategory() == User.Category.PROVIDER) {
            builder.and(qLot.provider.identifier.eq(currentUser.getIdentifier())
                                                .or(qProject.provider.identifier.eq(currentUser.getIdentifier())
                                                                                .and(qLot.provider.identifier.isNull())
                                                                                .and(qLibrary.identifier.eq(currentUser.getLibraryId()))));
        }
        // Sinon on applique les filtres demandés
        else {
            if (CollectionUtils.isNotEmpty(libraries)) {
                builder.and(qLibrary.identifier.in(libraries)
                                               // bibliothèque du projet
                                               .or(qProject.library.identifier.in(libraries)
                                                                              // bibliothèque partenaire
                                                                              .or(qAssociatedLibrary.identifier.in(libraries)
                                                                                                               // pas d'intervenant défini
                                                                                                               .and(qAssociatedUser.isNull()
                                                                                                                                   // l'utilisateur
                                                                                                                                   // fait partie des
                                                                                                                                   // intervenants
                                                                                                                                   .or(qAssociatedUser.identifier.contains(currentUser.getIdentifier()))))));
            }
            if (CollectionUtils.isNotEmpty(providers)) {
                builder.and(qLot.provider.identifier.in(providers).or(qProject.provider.identifier.in(providers).and(qLot.provider.identifier.isNull())));
            }
        }
    }

    /**
     * Filtrage des prestataires (si l'utilisateur courant est un prestataire)
     */
    public static void addAccessFilters(final BooleanBuilder builder,
                                        final QLibrary qLibrary,
                                        final QProject qProject,
                                        final List<String> libraries,
                                        final List<String> providers) {
        final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();

        // Prestataire: voit les projets sur lesquels il est prestataires + les données de sa bibliothèque
        if (currentUser != null && currentUser.getCategory() == User.Category.PROVIDER) {
            builder.and(qProject.provider.identifier.eq(currentUser.getIdentifier()).and(qLibrary.identifier.eq(currentUser.getLibraryId())));
        }
        // Sinon on applique les filtres demandés
        else {
            if (CollectionUtils.isNotEmpty(libraries)) {
                builder.and(qLibrary.identifier.in(libraries));
            }
            if (CollectionUtils.isNotEmpty(providers)) {
                builder.and(qProject.provider.identifier.in(providers));
            }
        }
    }
}
