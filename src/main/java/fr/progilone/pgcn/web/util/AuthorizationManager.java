package fr.progilone.pgcn.web.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * Classes gérant les relations de dépendance entre autorisations
 * <p/>
 * Définir les relations à l'aide de AuthorizationManager.setRequirements(mon_autorisation, mon_prérequis_1, mon_prérequis_2, ...)
 * <p/>
 * Created by Sebastien on 06/11/2015.
 */
public class AuthorizationManager {

    private static final String BASE_PACKAGE = "fr.progilone.pgcn.web.rest";

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationManager.class);

    /**
     * Clé: autorisation
     * Valeurs: liste des autorisations nécessaires pour bénéficier de l'autorisation clé
     */
    private static Map<String, List<String>> requirements = Collections.synchronizedMap(new HashMap<>());

    // Initialisation des constantes (définition des liens de dépendances entre les droits)
    static {
        try {
            LOG.debug("Initialisation des autorisations à partir du package {}", BASE_PACKAGE);
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(Init.class));

            final Set<BeanDefinition> beanDefs = provider.findCandidateComponents(BASE_PACKAGE);
            for (final BeanDefinition beanDef : beanDefs) {
                LOG.debug("Initialisation de la classe {}", beanDef.getBeanClassName());
                Class.forName(beanDef.getBeanClassName());
            }
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Définit la liste des autorisations nécessaires pour bénéficier de authorization
     *
     * @param authorization
     * @param requirements
     */
    public static void setRequirements(String authorization, String... requirements) {
        AuthorizationManager.requirements.put(authorization, Arrays.asList(requirements));
    }

    /**
     * @param authorization
     * @return la liste des autorisations requises pour bénéficier de <i>authorization</i>
     */
    public static List<String> getRequirements(String authorization) {
        return AuthorizationManager.requirements.getOrDefault(authorization, Collections.emptyList());
    }

    /**
     * @param authorization
     * @return la liste des autorisations dépendant de <i>authorization</i>
     */
    public static List<String> getDependencies(String authorization) {
        return AuthorizationManager.requirements.entrySet().stream().filter(e -> e.getValue().contains(authorization)).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Annotation à placer sur les classes à initialiser pour la gestion des droits
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Init {
    }
}
