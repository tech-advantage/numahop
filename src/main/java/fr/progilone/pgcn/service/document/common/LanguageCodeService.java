package fr.progilone.pgcn.service.document.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.common.LanguageCode;
import fr.progilone.pgcn.repository.document.common.LanguageCodeRepository;

/**
 * Service de gestion des langages normalisé (ISO 639)
 * @author jbrunet
 * Créé le 22 févr. 2017
 */
@Service
public class LanguageCodeService {

    @Autowired
    private LanguageCodeRepository languageCodeRepository;

    /**
     * Récupération d'un {@link LanguageCode} en fonction d'un code
     * qui peut correspondre à
     * Valeur ISO 639 1
     * Valeur ISO 639 2(T)
     * Valeur ISO 639 2(B)
     *
     * @param code
     * @return
     */
    @Transactional(readOnly = true)
    public LanguageCode searchOneByCode(final String code) {
        
        return languageCodeRepository.getAllByNameOrIso6392tOrIso6392bOrIso6391(code, code, code, code).stream().findFirst().orElse(null);
    }

    /**
     * Retourne le code ISO 6393 2 du langage
     *
     * @param language
     * @return
     */
    @Transactional(readOnly = true)
    public String getIso6393TForLanguage(final String language) {
        final LanguageCode code = searchOneByCode(language);
        if(code == null) {
            return language;
        }
        return code.getIso6392t();
    }

    /**
     * Retourne le code ISO6393 2 B (Marc21) pour le langage si présent,
     * sinon le ISO6393 2
     *
     * @param language
     * @return
     */
    @Transactional(readOnly = true)
    public String getIso6393BForLanguage(final String language) {
        final LanguageCode code = searchOneByCode(language);
        if(code == null) {
            return language;
        }
        if(code.getIso6392b() == null || code.getIso6392b().isEmpty()) {
            return code.getIso6392t();
        } else {
            return code.getIso6392b();
        }
    }
    
    /**
     * Verifie s'il existe une correspondance dans la table des codes languages.
     * 
     * @param language
     * @return
     */
    public boolean checkCinesLangCodeExists(final String language) {
        
        if (StringUtils.isBlank(language) ) {
            return false;
        }
        return searchOneByCode(language) != null;
    }
    
}
