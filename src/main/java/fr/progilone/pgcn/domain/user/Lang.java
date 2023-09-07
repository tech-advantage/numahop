package fr.progilone.pgcn.domain.user;

import java.util.Locale;

/**
 * Langues supportées par l'application
 */
public enum Lang {

    /**
     * Français
     */
    FR(Locale.FRANCE),
    /**
     * Anglais
     */
    EN(Locale.ENGLISH);

    private Locale locale;

    Lang(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }
}