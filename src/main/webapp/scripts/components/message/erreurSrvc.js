(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('ErreurSrvc', function (gettextCatalog) {
            var errors = {
                "AUTO_CHECK_TYPE_LABEL_MANDATORY": gettextCatalog.getString("Le label est obligatoire"),
                "CONDREPORT_DETAIL_DESC_EMPTY": gettextCatalog.getString("Veuillez renseigner les champs manquants"),
                "CONDREPORT_DETAIL_EMPTY": gettextCatalog.getString("Veuillez renseigner le constat d'état"),
                "CONF_CHECK_DEL_EXITS_LIB": gettextCatalog.getString("Cette configuration est active sur au moins une bibliothèque"),
                "CONF_CHECK_DEL_EXITS_LOT": gettextCatalog.getString("Cette configuration est active sur au moins un lot"),
                "CONF_CHECK_DEL_EXITS_PROJECT": gettextCatalog.getString("Cette configuration est active sur au moins un projet"),
                "CONF_FTP_DEL_EXITS_LIB": gettextCatalog.getString("Cette configuration est active sur au moins une bibliothèque"),
                "CONF_FTP_DEL_EXITS_LOT": gettextCatalog.getString("Cette configuration est active sur au moins un lot"),
                "CONF_FTP_DEL_EXITS_PROJECT": gettextCatalog.getString("Cette configuration est active sur au moins un projet"),
                "CONF_IA_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "CONF_IA_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "CONF_OMEKA_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "CONF_OMEKA_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "CONF_SFTP_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "CONF_SFTP_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "DATA_INTEGRITY_VIOLATION": gettextCatalog.getString("Une erreur s'est produite lors de l'enregistrement des modifications"),
                "DELIVERY_DUPLICATE_LABEL": gettextCatalog.getString("Ce libellé est déjà utilisé"),
                "DELIVERY_LOT_MANDATORY": gettextCatalog.getString("Le lot est obligatoire"),
                "DELIVERY_NO_MASTER_FOUND": gettextCatalog.getString("Aucun fichier master ne correspond à cette livraison"),
                "DELIVERY_NOT_ENOUGH_AVAILABLE_SPACE": gettextCatalog.getString("Espace disque insuffisant pour cette livraison"),
                "DELIVERY_NO_MATCHING_PREFIX": gettextCatalog.getString("Certains dossiers ne correspondent à aucun document"),
                "DELIVERY_WRONG_FOLDER": gettextCatalog.getString("Le dossier de livraison n'existe pas"),
                "DESC_PROPERTY_DEL_EXISTS_DESC": gettextCatalog.getString("Cette propriété est utilisée sur au moins un constat d'état"),
                "DESC_PROPERTY_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "DESC_PROPERTY_TYPE_MANDATORY": gettextCatalog.getString("Le type est obligatoire"),
                "DESC_VALUE_DEL_EXISTS_DESC": gettextCatalog.getString("Cette valeur est utilisée sur au moins un constat d'état"),
                "DESC_VALUE_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "DOC_PROP_TYPE_DEL_USED_MAPPING": gettextCatalog.getString("Le type de propriété est utilisé dans une règle de mapping"),
                "DOC_PROP_TYPE_DEL_USED_PROP": gettextCatalog.getString("Le type de propriété est utilisé sur une notice"),
                "DOC_PROP_TYPE_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "DOC_UNIT_DUPLICATE_PGCN_ID": gettextCatalog.getString("L'identifiant PGCN est déjà utilisé"),
                "DOC_UNIT_IN_LIBRARY": gettextCatalog.getString("Une ou plusieurs unités documentaires sont rattachées à cette bibliothèque"),
                "DOC_UNIT_IN_PROJECT": gettextCatalog.getString("L'unité documentaire appartient à un projet"),
                "DOC_UNIT_IN_ONGOING_LOT": gettextCatalog.getString("L'unité documentaire appartient à un lot en cours"),
                "DOC_UNIT_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "DOC_UNIT_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "DOC_UNIT_PGCN_ID_MANDATORY": gettextCatalog.getString("L'identifiant PGCN est obligatoire"),
                "DOC_UNIT_RIGHT_MANDATORY": gettextCatalog.getString("Les droits de diffusion sont obligatoires"),
                "FAILED": gettextCatalog.getString("Echec du traitement"),
                "IMPORT_FORMAT_MANDATORY": gettextCatalog.getString("Le format de fichier n'est pas renseigné"),
                "IMPORT_MAPPING_MANDATORY": gettextCatalog.getString("Le mapping n'est pas renseigné"),
                "LIBRARY_DEL_EXITS_DOCUNIT": gettextCatalog.getString("Une ou plusieurs unités documentaires sont rattachées à cette bibliothèque"),
                "LIBRARY_DEL_EXITS_PROJ": gettextCatalog.getString("Un ou plusieurs projets sont rattachés à cette bibliothèque"),
                "LIBRARY_DEL_EXITS_RECORD": gettextCatalog.getString("Une ou plusieurs notices sont rattachées à cette bibliothèque"),
                "LIBRARY_DEL_EXITS_USER": gettextCatalog.getString("Un ou plusieurs utilisateurs sont rattachés à cette bibliothèque"),
                "LIBRARY_DUPLICATE_NAME": gettextCatalog.getString("Ce nom est déjà utilisé"),
                "LOT_DEL_EXITS_DELIVERY": gettextCatalog.getString("Ce lot contient des livraisons"),
                "LOT_DEL_EXITS_DOCUNIT": gettextCatalog.getString("Ce lot contient des unités documentaires"),
                "LOT_DUPLICATE_LABEL": gettextCatalog.getString("Ce libellé est déjà utilisé par un autre lot de ce projet"),
                "MAPPING_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "MAPPING_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "MAPPING_RULE_FIELD_MANDATORY": gettextCatalog.getString("Au moins un champs doit être renseigné par règle de mapping"),
                "MAPPING_RULE_LABEL_MANDATORY": gettextCatalog.getString("Aucune règle ne définit le champ <b>Libellé</b> de l'unité documentaire"),
                "MAPPING_RULE_PGCNID_MANDATORY": gettextCatalog.getString("Aucune règle ne définit le champ <b>Pgcn Id</b> de l'unité documentaire"),
                "MAPPING_RULE_RIGHTS_MANDATORY": gettextCatalog.getString("Aucune règle ne définit le champ <b>Droits</b> de l'unité documentaire"),
                "MAPPING_TYPE_MANDATORY": gettextCatalog.getString("Le type est obligatoire"),
                "PROJECT_DEL_EXITS_DOCUNITS": gettextCatalog.getString("Ce projet contient des unités documentaires"),
                "PROJECT_DEL_EXITS_LOTS": gettextCatalog.getString("Ce projet contient des lots"),
                "PROJECT_DEL_EXITS_TRAINS": gettextCatalog.getString("Ce projet contient des trains"),
                "PROJECT_DUPLICATE_NAME": gettextCatalog.getString("Ce nom est déjà utilisé"),
                "PROJECT_IN_LIBRARY": gettextCatalog.getString("Un ou plusieurs projets sont rattachés à cette bibliothèque"),
                "ROLE_DEL_EXITS_LIB": gettextCatalog.getString("Une ou plusieurs bibliothèques utilisent ce profil par défaut"),
                "ROLE_DEL_EXITS_USER": gettextCatalog.getString("Un ou plusieurs utilisateurs sont rattachées à ce profil"),
                "TPL_DUPLICATE": gettextCatalog.getString("Il ne peut y avoir qu'un seul template portant le même nom pour une bibliothèque"),
                "TPL_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "TPL_NAME_MANDATORY": gettextCatalog.getString("Le nom est obligatoire"),
                "USER_DEL_EXITS_LOT": gettextCatalog.getString("Cet utilisateur est défini comme prestataire sur un ou plusieurs lots"),
                "USER_DEL_EXITS_PROJ": gettextCatalog.getString("Cet utilisateur est défini comme prestataire sur un ou plusieurs projets"),
                "USER_DUPLICATE_LOGIN": gettextCatalog.getString("Ce login est déjà utilisé"),
                "USER_EMAIL_INVALID": gettextCatalog.getString("Le format du courriel n'est pas valide"),
                "USER_EMAIL_MANDATORY": gettextCatalog.getString("Le courriel est obligatoire"),
                "USER_IN_LIBRARY": gettextCatalog.getString("Un ou plusieurs utilisateurs sont rattachés à cette bibliothèque"),
                "USER_LIBRARY_MANDATORY": gettextCatalog.getString("La bibliothèque est obligatoire"),
                "USER_LOGIN_MANDATORY": gettextCatalog.getString("Le login est obligatoire"),
                "USER_ROLE_CODE_MANDATORY": gettextCatalog.getString("Le code est obligatoire"),
                "USER_ROLE_LABEL_MANDATORY": gettextCatalog.getString("Le libellé est obligatoire"),
                "USER_ROLE_MANDATORY": gettextCatalog.getString("Le profil est obligatoire"),
                "USER_ROLE_UNIQUE_CODE_VIOLATION": gettextCatalog.getString("Ce code est déjà utilisé par un autre profil"),
                "USER_ROLE_UNIQUE_LABEL_VIOLATION": gettextCatalog.getString("Ce libellé est déjà utilisé par un autre profil"),
                "USER_SURNAME_MANDATORY": gettextCatalog.getString("Le nom est obligatoire"),
                "USER_IN_WORKFLOW_GROUP": gettextCatalog.getString("Cet utilisateur appartient à un groupe de workflow"),
                "WORKFLOW_GROUP_DUPLICATE_NAME": gettextCatalog.getString("Ce nom est déjà utilisé"),
                "WORKFLOW_GROUP_IS_IN_FUTURE_STATE": gettextCatalog.getString("Ce groupe est impliqué dans une étape en cours ou à venir"),
                "WORKFLOW_GROUP_MIXED_USERS": gettextCatalog.getString("Les utilisateurs de ce groupe proviennent de bibliothèques différentes"),
                "WORKFLOW_GROUP_NAME_MANDATORY": gettextCatalog.getString("Le nom du groupe est obligatoire"),
                "WORKFLOW_MODEL_DUPLICATE_NAME": gettextCatalog.getString("Ce nom est déjà utilisé"),
                "WORKFLOW_MODEL_MISSING": gettextCatalog.getString("Un modèle de workflow doit être rattaché"),
                "WORKFLOW_MODEL_NAME_MANDATORY": gettextCatalog.getString("Le nom du modèle est obligatoire"),
                "WORKFLOW_MODEL_STATE_GROUP_MANDATORY": gettextCatalog.getString("Un groupe doit obligatoirement être affecté à chaque étape requise"),
                "WORKFLOW_PROCESS_NO_RIGHTS": gettextCatalog.getString("Vous n'avez pas les droits suffisants"),
                "WORKFLOW_LOT_DOCUNIT_MISSING": gettextCatalog.getString("Ce lot ne contient pas de documents"),
                "WORKFLOW_MODEL_DEL_EXITS_PROJ": gettextCatalog.getString("Ce modèle est rattaché à un ou plusieurs projets"),
                "WORKFLOW_MODEL_DEL_EXITS_LOT": gettextCatalog.getString("Ce modèle est rattaché à un ou plusieurs lots"),
                "WORKFLOW_MODEL_DEL_EXITS_DOC": gettextCatalog.getString("Ce modèle est rattaché à un ou plusieurs documents"),
                "Z3950SERVER_NAME_MANDATORY": gettextCatalog.getString("Le nom est obligatoire"),
                "Z3950SERVER_UNIQUE_NAME_VIOLATION": gettextCatalog.getString("Ce nom est déjà utilisé"),
                "Z3950_CONNECTION_FAILURE": gettextCatalog.getString("Erreur de connexion au serveur Z39.50"),
                "Z3950_SEARCH_FAILURE": gettextCatalog.getString("Erreur lors de la recherche Z39.50")
                
            };

            var getMessage = function (codeMessage, complements) {
                if (_.isEmpty(complements)) {
                    return errors[codeMessage] || codeMessage;
                } else {
                    var complementsObj = {};
                    _.each(complements, function (c, index) {
                        complementsObj["$" + index] = c;
                    });
                    var messageTemplate = errors[codeMessage];
                    return _.template(messageTemplate)(complementsObj) || codeMessage;
                }
            };

            return {
                getMessage: getMessage
            };
        });
})();
