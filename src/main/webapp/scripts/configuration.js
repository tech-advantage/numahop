(function () {
    'use strict';

    /*
     * *** numaHop client configuration ***
     *
     * Languages codes are ISO_639-1 codes, see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
     */
    angular.module('numaHopApp').constant('CONFIGURATION', {
        numahop: {
            // url: "http://localhost:9000/"
            url: '',
            baseLanguage: 'fr',
            defaultLocale: 'fr',
            locales: [
                {
                    code: 'en',
                    label: 'Anglais',
                    flag: 'gb',
                },
                {
                    code: 'fr',
                    label: 'Français',
                    flag: 'fr',
                },
            ],
        },
    });

    /** Pager configuration **/
    angular.module('numaHopApp').run(function (gettextCatalog, uibPaginationConfig) {
        uibPaginationConfig.itemsPerPage = 10;
        uibPaginationConfig.maxSize = 5;
        uibPaginationConfig.boundaryLinks = true;
        uibPaginationConfig.directionLinks = true;
        uibPaginationConfig.firstText = '«'; // &laquo;
        uibPaginationConfig.previousText = '‹'; // &lsaquo;
        uibPaginationConfig.nextText = '›'; // &rsaquo;
        uibPaginationConfig.lastText = '»'; // &raquo;
        uibPaginationConfig.rotate = false;
    });

    /** jQuery timepicker **/
    angular.module('ui.timepicker').value('uiTimepickerConfig', {
        step: 15,
        timeFormat: 'H:i',
    });

    angular.module('numaHopApp').run(function () {
        //    config par défaut, évaluées dans cet ordre:
        //      escape: /<%-([\s\S]+?)%>/g
        //      interpolate: /<%=([\s\S]+?)%>/g
        //      evaluate: /<%([\s\S]+?)%>/g

        // ! utilisé pour l'évaluation des formules, et par clndr !
        // escape {{-var_html}}, interpolate {{var_string}}, evaluate {%var_js%}
        _.templateSettings = {
            escape: /\{\{\-(.+?)\}\}/g,
            interpolate: /\{\{(.+?)\}\}/g,
            evaluate: /\{\%(.+?)\%\}/g,
        };
    });

    /** Config */
    /*   angular.module('numaHopApp').config(function ($uibTooltipProvider, ChartJsProvider) {
        // Tooltips
        $uibTooltipProvider.options({
            placement: 'bottom',
            appendToBody: true
        });
        // Chart.js
        ChartJsProvider.setOptions({ global: { colors: ["#c92a2a", "#e8590c", "#f59f00", "#fcc419", "#ffe066", "#fff3bf", "#fff9db"] } });
    }); */

    /** Rôles * */
    angular.module('numaHopApp').constant('USER_ROLES', {
        all: '*',
        // LOT
        LOT_HAB0: 'LOT-HAB0', //Habilitation à la gestion des lots: créer
        LOT_HAB1: 'LOT-HAB1', //Habilitation à la gestion des lots: modifier
        LOT_HAB2: 'LOT-HAB2', //Habilitation à la gestion des lots: supprimer
        LOT_HAB3: 'LOT-HAB3', //Habilitation à la gestion des lots: visualiser
        LOT_HAB4: 'LOT-HAB4', //Habilitation à la gestion des lots: contrôler après réception et comptage (physique ou numérique): valider, rejeter…
        LOT_HAB5: 'LOT-HAB5', //Habilitation à la gestion des lots: exporter la liste / une fiche au format CSV
        LOT_HAB6: 'LOT-HAB6', //Habilitation à la gestion des lots: imprimer la liste / une fiche

        // TRAIN
        TRA_HAB0: 'TRA-HAB0', //Habilitation à la gestion des trains: créer
        TRA_HAB1: 'TRA-HAB1', //Habilitation à la gestion des trains: modifier
        TRA_HAB2: 'TRA-HAB2', //Habilitation à la gestion des trains: supprimer
        TRA_HAB3: 'TRA-HAB3', //Habilitation à la gestion des trains: visualiser
        TRA_HAB4: 'TRA-HAB4', //Habilitation à la gestion des trains: contrôler après réception et comptage (physique ou numérique): valider, rejeter…
        TRA_HAB5: 'TRA-HAB5', //Habilitation à la gestion des trains: exporter la liste / une fiche au format CSV
        TRA_HAB6: 'TRA-HAB6', //Habilitation à la gestion des trains: imprimer la liste / une fiche

        // PROJECT
        PROJ_HAB0: 'PROJ-HAB0', //Habilitation à la gestion des projets: créer
        PROJ_HAB1: 'PROJ-HAB1', //Habilitation à la gestion des projets: modifier
        PROJ_HAB2: 'PROJ-HAB2', //Habilitation à la gestion des projets: ajouter des utilisateurs
        PROJ_HAB3: 'PROJ-HAB3', //Habilitation à la gestion des projets: ajouter des bibliothèques
        PROJ_HAB4: 'PROJ-HAB4', //Habilitation à la gestion des projets: supprimer
        PROJ_HAB5: 'PROJ-HAB5', //Habilitation à la gestion des projets: suspendre
        PROJ_HAB6: 'PROJ-HAB6', //Habilitation à la gestion des projets: annuler
        PROJ_HAB7: 'PROJ-HAB7', //Habilitation à la gestion des projets: visualiser
        PROJ_HAB8: 'PROJ-HAB8', //Habilitation à la gestion des projets: exporter la liste / une fiche au format CSV
        PROJ_HAB9: 'PROJ-HAB9', //Habilitation à la gestion des projets: imprimer la liste / une fiche
        PROJ_HAB10: 'PROJ-HAB10', //Habilitation à la gestion des projets: scinder un projet en deux
        PROJ_HAB11: 'PROJ-HAB11', //Habilitation à la gestion des projets: fusionner des projets

        // LIBRARY
        LIB_HAB1: 'LIB-HAB1', //Habilitation à la gestion des bibliothèques: créer
        LIB_HAB2: 'LIB-HAB2', //Habilitation à la gestion des bibliothèques: modifier
        LIB_HAB3: 'LIB-HAB3', //Habilitation à la gestion des bibliothèques: supprimer
        LIB_HAB4: 'LIB-HAB4', //Habilitation à la gestion des bibliothèques: désactiver
        LIB_HAB5: 'LIB-HAB5', //Habilitation à la gestion des bibliothèques: visualiser (hors données de production)
        LIB_HAB6: 'LIB-HAB6', //Habilitation à la gestion des bibliothèques: visualiser les utilisateurs
        LIB_HAB7: 'LIB-HAB7', //Habilitation à la gestion des bibliothèques: visualiser les projets et les données de production
        LIB_HAB8: 'LIB-HAB8', //Habilitation à la gestion des bibliothèques: exporter la liste / une fiche au format CSV
        LIB_HAB9: 'LIB-HAB9', //Habilitation à la gestion des bibliothèques: imprime la liste / une fiche

        // USER
        USER_HAB0: 'USER-HAB0', // Habilitation à la gestion des utilisateurs : lecture
        USER_HAB1: 'USER-HAB1', // Habilitation à la gestion des utilisateurs : création
        USER_HAB2: 'USER-HAB2', // Habilitation à la gestion des utilisateurs : modification
        USER_HAB3: 'USER-HAB3', // Habilitation à la gestion des utilisateurs : suppression
        USER_HAB4: 'USER-HAB4', // Habilitation à la gestion des utilisateurs : désactivation
        USER_HAB5: 'USER-HAB5', // Habilitation à la gestion des utilisateurs : export CSV
        USER_HAB6: 'USER-HAB6', // Habilitation à la gestion des utilisateurs : modification de son compte

        // ROLE
        ROLE_HAB0: 'ROLE-HAB0', // Habilitation à la gestion des rôles : lecture
        ROLE_HAB1: 'ROLE-HAB1', // Habilitation à la gestion des rôles : création
        ROLE_HAB2: 'ROLE-HAB2', // Habilitation à la gestion des rôles : modification
        ROLE_HAB3: 'ROLE-HAB3', // Habilitation à la gestion des rôles : suppression
        ROLE_HAB4: 'ROLE-HAB4', // Habilitation à la gestion des rôles : désactivation
        ROLE_HAB5: 'ROLE-HAB5', // Habilitation à la gestion des rôles : export CSV

        // DELIVERY
        DEL_HAB0: 'DEL-HAB0', // Habilitation à la gestion des livraisons : lecture
        DEL_HAB1: 'DEL-HAB1', // Habilitation à la gestion des livraisons : création
        DEL_HAB2: 'DEL-HAB2', // Habilitation à la gestion des livraisons : modification
        DEL_HAB3: 'DEL-HAB3', // Habilitation à la gestion des livraisons : suppression
        DEL_HAB4: 'DEL-HAB4', // Habilitation à la gestion des livraisons : édition du bordereau de livraison
        DEL_HAB5: 'DEL-HAB5', // Habilitation à la gestion des livraisons : pré-livraison / livraison
        DEL_HAB5_2: 'DEL-HAB5-2', // Habilitation à la gestion des livraisons : pré-livraison / livraison avec creation UD
        DEL_HAB6: 'DEL-HAB6', // Habilitation à la gestion des livraisons : export CSV
        DEL_HAB7: 'DEL-HAB7', // Habilitation à la gestion des livraisons : impression
        DEL_HAB8: 'DEL-HAB8', // Habilitation à la gestion des livraisons : modification / suppr. des livraisons payées

        // DOC
        DOC_LIB: 'DOC-LIB', // Visualisation des données des autres bibliothèques
        DOC_UNIT_HAB0: 'DOC-UNIT-HAB0', // Habilitation à la gestion des unités documentaires : lecture
        DOC_UNIT_HAB1: 'DOC-UNIT-HAB1', // Habilitation à la gestion des unités documentaires : création
        DOC_UNIT_HAB2: 'DOC-UNIT-HAB2', // Habilitation à la gestion des unités documentaires : modification
        DOC_UNIT_HAB3: 'DOC-UNIT-HAB3', // Habilitation à la gestion des unités documentaires : suppression
        DOC_UNIT_HAB4: 'DOC-UNIT-HAB4', // Habilitation à la gestion des unités documentaires : export CSV
        DOC_UNIT_HAB5: 'DOC-UNIT-HAB5', // Habilitation à l'administration des pptés personnalisées

        // Contrôle
        CHECK_HAB0: 'CHECK-HAB0', // Habilitation configuration des contrôles: lecture
        CHECK_HAB1: 'CHECK-HAB1', // Habilitation configuration des contrôles: créer / modifier
        CHECK_HAB2: 'CHECK-HAB2', // Habilitation configuration des contrôles: supprimer
        CHECK_HAB3: 'CHECK-HAB3', // Habilitation contrôles: visualisation des documents contrôlés et du détail des contrôles effectués
        CHECK_HAB4: 'CHECK-HAB4', // Habilitation contrôles: action de contrôler des documents

        // OCRisation
        OCR_LANG_HAB0: 'OCR-LANG-HAB0', // Habilitation configuration des langages OCR: lecture
        OCR_LANG_HAB1: 'OCR-LANG-HAB1', // Habilitation configuration des langages OCR: creer / modifier
        OCR_LANG_HAB2: 'OCR-LANG-HAB2', // Habilitation configuration des langages OCR: supprimer

        // Constats d'état
        COND_REPORT_HAB0: 'COND-REPORT-HAB0', // Habilitation à la gestion des constats d'état: lecture
        COND_REPORT_HAB1: 'COND-REPORT-HAB1', // Habilitation à la gestion des constats d'état: création
        COND_REPORT_HAB2: 'COND-REPORT-HAB2', // Habilitation à la gestion des constats d'état: modification
        COND_REPORT_HAB3: 'COND-REPORT-HAB3', // Habilitation à la gestion des constats d'état: suppression
        COND_REPORT_HAB4: 'COND-REPORT-HAB4', // Habilitation à la gestion des constats d'état: export
        COND_REPORT_HAB5: 'COND-REPORT-HAB5', // Habilitation à la gestion des constats d'état: configuration des propriétés propres à chaque bib
        COND_REPORT_HAB6: 'COND-REPORT-HAB6', // Habilitation à la gestion des constats d'état: administration des valeurs communes

        // Import / Export
        EXC_HAB0: 'EXC-HAB0', // Habilitation import/export: lecture
        EXC_HAB1: 'EXC-HAB1', // Habilitation import/export: suppression
        EXC_HAB2: 'EXC-HAB2', // Habilitation import/export: exécution d'un import

        // Administration
        ADMINISTRATION_LIB: 'ADMINISTRATION-LIB', // Visualisation des données des autres bibliothèques
        CONF_INTERNET_ARCHIVE_HAB0: 'CONF-INTERNET-ARCHIVE-HAB0', // Habilitation configuration Internet Archive: lecture
        CONF_INTERNET_ARCHIVE_HAB1: 'CONF-INTERNET-ARCHIVE-HAB1', // Habilitation configuration Internet Archive: création / modification
        CONF_INTERNET_ARCHIVE_HAB2: 'CONF-INTERNET-ARCHIVE-HAB2', // Habilitation configuration Internet Archive: suppression
        CONF_DIFFUSION_OMEKA_HAB0: 'CONF-DIFFUSION-OMEKA-HAB0', // Habilitation configuration Omeka: lecture
        CONF_DIFFUSION_OMEKA_HAB1: 'CONF-DIFFUSION-OMEKA-HAB1', // Habilitation configuration Omeka: création / modification
        CONF_DIFFUSION_OMEKA_HAB2: 'CONF-DIFFUSION-OMEKA-HAB2', // Habilitation configuration Omeka: suppression
        CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0: 'CONF-DIFFUSION-DIGITAL-LIBRARY-HAB0', // Habilitation configuration bibliothèque numérique: lecture
        CONF_DIFFUSION_DIGITAL_LIBRARY_HAB1: 'CONF-DIFFUSION-DIGITAL-LIBRARY-HAB1', // Habilitation configuration bibliothèque numérique: création / modification
        CONF_DIFFUSION_DIGITAL_LIBRARY_HAB2: 'CONF-DIFFUSION-DIGITAL-LIBRARY-HAB2', // Habilitation configuration bibliothèque numérique: suppression
        EXPORT_INTERNET_ARCHIVE_HAB0: 'EXPORT-INTERNET-ARCHIVE-HAB0', // Autorisation export Internet Archive
        EXPORT_DIFFUSION_OMEKA_HAB0: 'EXPORT-DIFFUSION-OMEKA-HAB0', // Autorisation export Omeka
        EXPORT_DIFFUSION_DIGITAL_LIBRARY_HAB0: 'EXPORT-DIFFUSION-DIGITAL-LIBRARY-HAB0', // Autorisation export bibliothèque numérique
        FILES_GEST_HAB0: 'FILES-GEST-HAB0', // Habilitation gestion des fichiers apres archivage
        IMG_FORMAT_HAB0: 'IMG-FORMAT-HAB0', // Habilitation gestion des formats des vues: visualisation
        IMG_FORMAT_HAB1: 'IMG-FORMAT-HAB1', // Habilitation gestion des formats des vues: création/modification
        FTP_HAB0: 'FTP-HAB0', // Habilitation configuration FTP: lecture
        FTP_HAB1: 'FTP-HAB1', // Habilitation configuration FTP: création / modification
        FTP_HAB2: 'FTP-HAB2', // Habilitation configuration FTP: suppression
        EXP_FTP_HAB0: 'EXP-FTP-HAB0', // Habilitation configuration Export FTP: lecture
        EXP_FTP_HAB1: 'EXP-FTP-HAB1', // Habilitation configuration Export FTP: création / modification
        EXP_FTP_HAB2: 'EXP-FTP-HAB2', // Habilitation configuration Export FTP: suppression
        MAIL_HAB0: 'MAIL-HAB0', // Habilitation configuration email: lecture
        MAIL_HAB1: 'MAIL-HAB1', // Habilitation configuration email: créer / modifier
        MAIL_HAB2: 'MAIL-HAB2', // Habilitation configuration email: supprimer
        MAP_HAB0: 'MAP-HAB0', // Habilitation mapping: lecture
        MAP_HAB1: 'MAP-HAB1', // Habilitation mapping: création / modification
        MAP_HAB2: 'MAP-HAB2', // Habilitation mapping: suppression
        SFTP_HAB0: 'SFTP-HAB0', // Habilitation configuration SFTP: lecture
        SFTP_HAB1: 'SFTP-HAB1', // Habilitation configuration SFTP: création / modification
        SFTP_HAB2: 'SFTP-HAB2', // Habilitation configuration SFTP: suppression
        TPL_HAB0: 'TPL-HAB0', // Administration des templates
        Z3950_HAB0: 'Z3950-HAB0', // Habilitation serveurs Z39.50: lecture
        Z3950_HAB1: 'Z3950-HAB1', // Habilitation serveurs Z39.50: création / modification
        Z3950_HAB2: 'Z3950-HAB2', // Habilitation serveurs Z39.50: suppression

        // Workflow
        WORKFLOW_HAB1: 'WORKFLOW-HAB1', //Habilitation à la gestion des workflow: créer
        WORKFLOW_HAB2: 'WORKFLOW-HAB2', //Habilitation à la gestion des workflow: modifier
        WORKFLOW_HAB3: 'WORKFLOW-HAB3', //Habilitation à la gestion des workflow: supprimer
        WORKFLOW_HAB4: 'WORKFLOW-HAB4', //Habilitation à la gestion des workflow: visualiser

        // Super admin
        SUPER_ADMIN: 'SUPER_ADMIN',
    });
})();
