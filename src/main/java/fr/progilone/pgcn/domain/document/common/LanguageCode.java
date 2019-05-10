package fr.progilone.pgcn.domain.document.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Liste des langages pour la conversion des exports
 *
 * @author jbrunet
 *         Créé le 21 févr. 2017
 */
@Entity
@Table(name = LanguageCode.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "com_language_code", value = LanguageCode.class)})
public class LanguageCode extends AbstractDomainObject {

    public static final String TABLE_NAME = "com_language_code";

    /**
     * Valeur
     */
    @Column(name = "name")
    private String name;

    /**
     * Tous les noms en français
     */
    @Column(name = "all_names_french")
    private String allNamesFrench;

    /**
     * Tous les noms en anglais
     */
    @Column(name = "all_names_english")
    private String allNamesEnglish;

    /**
     * ISO 639-2 (Terminology)
     */
    @Column(name = "iso_639_2_t")
    private String iso6392t;

    /**
     * ISO 639-2 (Bibliographic)
     * (équivalence anglaise sur certaines langues
     * Ex: fre au lieu de fra pour le français)
     */
    @Column(name = "iso_639_2_b")
    private String iso6392b;

    /**
     * ISO 639-1 (Deux caractères)
     */
    @Column(name = "iso_639_1")
    private String iso6391;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllNamesFrench() {
        return allNamesFrench;
    }

    public void setAllNamesFrench(String allNamesFrench) {
        this.allNamesFrench = allNamesFrench;
    }

    public String getAllNamesEnglish() {
        return allNamesEnglish;
    }

    public void setAllNamesEnglish(String allNamesEnglish) {
        this.allNamesEnglish = allNamesEnglish;
    }

    public String getIso6392t() {
        return iso6392t;
    }

    public void setIso6392t(String iso639_2t) {
        this.iso6392t = iso639_2t;
    }

    public String getIso6392b() {
        return iso6392b;
    }

    public void setIso6392b(String iso639_2b) {
        this.iso6392b = iso639_2b;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso639_1) {
        this.iso6391 = iso639_1;
    }
}
