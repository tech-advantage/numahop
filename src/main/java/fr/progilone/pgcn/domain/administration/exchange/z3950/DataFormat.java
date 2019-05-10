package fr.progilone.pgcn.domain.administration.exchange.z3950;

/**
 * http://www.bnf.fr/fr/professionnels/recuperation_donnees_bnf_boite_outils/a.profil_z3950_bnf.html
 */
public enum DataFormat {
    INTERMARC("1.2.840.10003.5.2"),
    UNIMARC("1.2.840.10003.5.1");

    private String formatCode;

    DataFormat(String formatCode) {
        this.formatCode = formatCode;
    }

    public String getFormatCode() {
        return formatCode;
    }
}
