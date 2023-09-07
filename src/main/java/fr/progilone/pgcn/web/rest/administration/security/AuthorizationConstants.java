package fr.progilone.pgcn.web.rest.administration.security;

import fr.progilone.pgcn.web.util.AuthorizationManager;

@AuthorizationManager.Init
public final class AuthorizationConstants {

    /**
     * Autorisation ajoutée automatiquement au super administrateur : ne correspond pas à une authorisation en BD
     */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * Autorisation ajoutée automatiquement au super administrateur : ne correspond pas à une authorisation en BD
     * => donne acces aux management protected endPoints
     */
    public static final String ACTUATOR = "ACTUATOR";

    /**
     * Accès aux données des autres bibliothèques, en lecture seule
     */
    public static final String ADMINISTRATION_LIB = "ADMINISTRATION-LIB";

    /**
     * Habilitation configuration gestion fichiers apres archivage
     */
    public static final String FILES_GEST_HAB0 = "FILES-GEST-HAB0";

    /**
     * Habilitation configuration gestion des formats des vues : visualisation
     */
    public static final String IMG_FORMAT_HAB0 = "IMG-FORMAT-HAB0";

    /**
     * Habilitation configuration gestion des formats des vues: création/modification
     */
    public static final String IMG_FORMAT_HAB1 = "IMG-FORMAT-HAB1";

    /**
     * Habilitation configuration email: lecture
     */
    public static final String MAIL_HAB0 = "MAIL-HAB0";

    /**
     * Habilitation configuration email: créer / modifier
     */
    public static final String MAIL_HAB1 = "MAIL-HAB1";

    /**
     * Habilitation configuration email: supprimer
     */
    public static final String MAIL_HAB2 = "MAIL-HAB2";

    /**
     * Habilitation configuration FTP: lecture
     */
    public static final String FTP_HAB0 = "FTP-HAB0";

    /**
     * Habilitation configuration FTP: créer / modifier
     */
    public static final String FTP_HAB1 = "FTP-HAB1";

    /**
     * Habilitation configuration FTP: supprimer
     */
    public static final String FTP_HAB2 = "FTP-HAB2";
    /**
     * Habilitation configuration Export FTP: lecture
     */
    public static final String EXP_FTP_HAB0 = "EXP-FTP-HAB0";

    /**
     * Habilitation configuration Export FTP: créer / modifier
     */
    public static final String EXP_FTP_HAB1 = "EXP-FTP-HAB1";

    /**
     * Habilitation configuration Export FTP: supprimer
     */
    public static final String EXP_FTP_HAB2 = "EXP-FTP-HAB2";
    /**
     * Habilitation configuration SFTP: lecture
     */
    public static final String SFTP_HAB0 = "SFTP-HAB0";

    /**
     * Habilitation configuration SFTP: créer / modifier
     */
    public static final String SFTP_HAB1 = "SFTP-HAB1";

    /**
     * Habilitation configuration SFTP: supprimer
     */
    public static final String SFTP_HAB2 = "SFTP-HAB2";

    /**
     * Habilitation configuration IA: lecture
     */
    public static final String CONF_INTERNET_ARCHIVE_HAB0 = "CONF-INTERNET-ARCHIVE-HAB0";

    /**
     * Habilitation configuration IA: créer / modifier
     */
    public static final String CONF_INTERNET_ARCHIVE_HAB1 = "CONF-INTERNET-ARCHIVE-HAB1";

    /**
     * Habilitation configuration IA: supprimer
     */
    public static final String CONF_INTERNET_ARCHIVE_HAB2 = "CONF-INTERNET-ARCHIVE-HAB2";

    /**
     * Habilitation configuration OMEKA: lecture
     */
    public static final String CONF_DIFFUSION_OMEKA_HAB0 = "CONF-DIFFUSION-OMEKA-HAB0";

    /**
     * Habilitation configuration OMEKA: créer / modifier
     */
    public static final String CONF_DIFFUSION_OMEKA_HAB1 = "CONF-DIFFUSION-OMEKA-HAB1";

    /**
     * Habilitation configuration OMEKA: supprimer
     */
    public static final String CONF_DIFFUSION_OMEKA_HAB2 = "CONF-DIFFUSION-OMEKA-HAB2";

    /**
     * Habilitation configuration bibliothèque numérique: lecture
     */
    public static final String CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0 = "CONF-DIFFUSION-DIGITAL-LIBRARY-HAB0";

    /**
     * Habilitation configuration bibliothèque numérique: créer / modifier
     */
    public static final String CONF_DIFFUSION_DIGITAL_LIBRARY_HAB1 = "CONF-DIFFUSION-DIGITAL-LIBRARY-HAB1";
    /**
     * Habilitation configuration bibliothèque numérique: supprimer
     */
    public static final String CONF_DIFFUSION_DIGITAL_LIBRARY_HAB2 = "CONF-DIFFUSION-DIGITAL-LIBRARY-HAB2";

    /**
     * Habilitation serveurs Z39.50: lecture
     */
    public static final String Z3950_HAB0 = "Z3950-HAB0";

    /**
     * Habilitation serveurs Z39.50: créer / modifier
     */
    public static final String Z3950_HAB1 = "Z3950-HAB1";

    /**
     * Habilitation serveurs Z39.50: supprimer
     */
    public static final String Z3950_HAB2 = "Z3950-HAB2";

    // ne pas oublier de placer l'annotation @AuthorizationManager.Init sur la classe pour initialiser les relations entre autorisations
    static {
        AuthorizationManager.setRequirements(IMG_FORMAT_HAB1, IMG_FORMAT_HAB0);
        AuthorizationManager.setRequirements(MAIL_HAB1, MAIL_HAB0);
        AuthorizationManager.setRequirements(MAIL_HAB2, MAIL_HAB0);
        AuthorizationManager.setRequirements(FTP_HAB1, FTP_HAB0);
        AuthorizationManager.setRequirements(FTP_HAB2, FTP_HAB0);
        AuthorizationManager.setRequirements(SFTP_HAB1, SFTP_HAB0);
        AuthorizationManager.setRequirements(SFTP_HAB2, SFTP_HAB0);
        AuthorizationManager.setRequirements(CONF_INTERNET_ARCHIVE_HAB1, CONF_INTERNET_ARCHIVE_HAB0);
        AuthorizationManager.setRequirements(CONF_INTERNET_ARCHIVE_HAB2, CONF_INTERNET_ARCHIVE_HAB0);
        AuthorizationManager.setRequirements(CONF_DIFFUSION_OMEKA_HAB1, CONF_DIFFUSION_OMEKA_HAB0);
        AuthorizationManager.setRequirements(CONF_DIFFUSION_OMEKA_HAB2, CONF_DIFFUSION_OMEKA_HAB0);
        AuthorizationManager.setRequirements(CONF_DIFFUSION_DIGITAL_LIBRARY_HAB1, CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0);
        AuthorizationManager.setRequirements(CONF_DIFFUSION_DIGITAL_LIBRARY_HAB2, CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0);
        AuthorizationManager.setRequirements(Z3950_HAB1, Z3950_HAB0);
        AuthorizationManager.setRequirements(Z3950_HAB2, Z3950_HAB0);
    }

    private AuthorizationConstants() {
    }
}
