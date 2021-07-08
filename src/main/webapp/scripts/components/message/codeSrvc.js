(function () {
    'use strict';

    angular.module('numaHopApp')
        .factory('codeSrvc', function (gettext, gettextCatalog) {

            // Texte marqué à traduire, mais n'apparaissant pas dans le code source js et html
            gettext('Anglais');
            gettext('Français');

            // Traduction des codes renvoyés par le serveur
            return {
                //Liste des modules (pour les habilitations)
                "ADMINISTRATION": gettextCatalog.getString("Administration"),
                "CHECK": gettextCatalog.getString("Contrôle"),
                "DOCUMENT": gettextCatalog.getString("Document"),
                "LIBRARY": gettextCatalog.getString("Bibliothèque"),
                "LOT": gettextCatalog.getString("Lot"),
                "PROJECT": gettextCatalog.getString("Projet"),
                "ROLE": gettextCatalog.getString("Profils"),
                "USER": gettextCatalog.getString("Utilisateurs"),
                "DELIVERY": gettextCatalog.getString("Livraisons"),
                "TRAIN": gettextCatalog.getString("Trains"),
                "WORKFLOW": gettextCatalog.getString("Workflows"),

                // États des DocUnit
                "import.DocUnit.State.AVAILABLE": gettextCatalog.getString("Importé"),
                "import.DocUnit.State.NOT_AVAILABLE": gettextCatalog.getString("Pré-importé"),
                "import.DocUnit.State.DELETED": gettextCatalog.getString("Ignoré"),
                "import.DocUnit.State.CANCELED": gettextCatalog.getString("Annulé"),
                "import.DocUnit.State.CLOSED": gettextCatalog.getString("Clôturé"),
                // types de pages d'aide
                "helpPageType.PGCN": gettextCatalog.getString('Aide de PGCN'),
                "helpPageType.CUSTOM": gettextCatalog.getString('Pages d\'aide personnalisées'),
                // Statuts (workflow)
                "CREATED": gettextCatalog.getString('Créé'),
                "VALIDATED": gettextCatalog.getString('Validé'),
                // Statuts (cines)
                "EXPORTING": gettextCatalog.getString('Initialisation de l\'envoi'),
                "SENDING": gettextCatalog.getString('Envoi en cours'),
                "SENT": gettextCatalog.getString('Envoyé'),
                "ARCHIVED": gettextCatalog.getString('Archivé'),
                "FAILED": gettextCatalog.getString('Échec du traitement'),
                "AR_RECEIVED": gettextCatalog.getString('Accusé réception reçu'),
                "REJECTED": gettextCatalog.getString('Rejeté'),

                // Contrôles automatiques
                "CHECK-OK": gettextCatalog.getString('Ok'),
                "CHECK-KO": gettextCatalog.getString('Erreur'),
                "CHECK-OTHER": gettextCatalog.getString('Non effectué'),

                // Document Numérique
                "digitalDocument.CREATING": gettextCatalog.getString('En cours de création'),
                "digitalDocument.DELIVERING": gettextCatalog.getString('En cours de livraison'),
                "digitalDocument.TO_CHECK": gettextCatalog.getString('A contrôler'),
                "digitalDocument.CHECKING": gettextCatalog.getString('En cours de contrôle'),
                "digitalDocument.VALIDATED": gettextCatalog.getString('Validé'),
                "digitalDocument.PRE_REJECTED": gettextCatalog.getString('Pré-rejeté'),
                "digitalDocument.PRE_VALIDATED": gettextCatalog.getString('Pré-validé'),
                "digitalDocument.REJECTED": gettextCatalog.getString('Rejeté'),
                "digitalDocument.WAITING_FOR_REPAIR": gettextCatalog.getString('En attente de réfection'),
                "digitalDocument.RELIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('En attente de relivraison'),
                "digitalDocument.DELIVERING_ERROR": gettextCatalog.getString('Erreur de livraison'),
                "digitalDocument.CANCELED": gettextCatalog.getString('Annulé'),

                // Projets
                "project.CREATED": gettextCatalog.getString('Créé'),
                "project.ONGOING": gettextCatalog.getString('En cours'),
                "project.PENDING": gettextCatalog.getString('En attente'),
                "project.CANCELED": gettextCatalog.getString('Annulé'),
                "project.CLOSED": gettextCatalog.getString('Clôturé'),

                // Lots
                "lot.status.CREATED": gettextCatalog.getString("Créé"),
                "lot.status.ONGOING": gettextCatalog.getString("En cours"),
                "lot.status.PENDING": gettextCatalog.getString("En attente"),
                "lot.status.CANCELED": gettextCatalog.getString("Annulé"),
                "lot.status.CLOSED": gettextCatalog.getString("Clôturé"),

                // Trains
                "train.status.CREATED": gettextCatalog.getString('Créé'),
                "train.status.IN_PREPARATION": gettextCatalog.getString('En cours de préparation'),
                "train.status.IN_DIGITIZATION": gettextCatalog.getString('En cours de numérisation'),
                "train.status.RECEIVING_PHYSICAL_DOCUMENTS": gettextCatalog.getString('Réception des documents physiques'),
                "train.status.CLOSED": gettextCatalog.getString('Clôturé'),
                "train.status.CANCELED": gettextCatalog.getString('Annulé'),

                // Livraisons
                "delivery.SAVED": gettextCatalog.getString('Sauvegardé'),
                "delivery.DELIVERING": gettextCatalog.getString('En cours de livraison'),
                "delivery.DELIVERED": gettextCatalog.getString('Livré'),
                "delivery.TO_BE_CONTROLLED": gettextCatalog.getString('A contrôler'),
                "delivery.VALIDATED": gettextCatalog.getString('Validé'),
                "delivery.REJECTED": gettextCatalog.getString('Rejeté'),
                "delivery.BACK_TO_PROVIDER": gettextCatalog.getString('Retour au prestataire'),
                "delivery.AUTOMATICALLY_REJECTED": gettextCatalog.getString('Rejeté automatiquement'),
                "delivery.DELIVERED_AGAIN": gettextCatalog.getString('Re-livré'),
                "delivery.DELIVERING_ERROR": gettextCatalog.getString('Erreur de livraison'),
                "delivery.TREATED": gettextCatalog.getString('Traité'),
                "delivery.CLOSED": gettextCatalog.getString('Clôturé'),

                // formats fichiers
                "format.JP2": gettextCatalog.getString('JP2 (JPEG-2000 File Format Syntax)'),
                "format.JPEG": gettextCatalog.getString('JPEG (Joint Photographic Experts Group JFIF format)'),
                "format.JPG": gettextCatalog.getString('JPG'),
                "format.PNG": gettextCatalog.getString('PNG (Portable Network Graphics)'),
                "format.GIF": gettextCatalog.getString('GIF (Graphics Interchange Format)'),
                "format.SVG": gettextCatalog.getString('SVG (Scalable Vector Graphic)'),
                "format.TIFF": gettextCatalog.getString('TIFF (Tagged Image File Format)'),
                "format.TIF": gettextCatalog.getString('TIF (Tagged Image File Format)'),
                "format.PDF": gettextCatalog.getString('PDF'),

                // filtre statut workflow / docUnits
                "docUnit.filter.VALIDATION_CONSTAT_ETAT": gettextCatalog.getString('Validation des constats d\'état'),
                "docUnit.filter.LIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('Documents en attente de livraison'),
                "docUnit.filter.CONTROLE_QUALITE_EN_COURS": gettextCatalog.getString('Contrôles qualité'),
                "docUnit.filter.PREREJET_DOCUMENT": gettextCatalog.getString('Pré-rejet du document'),
                "docUnit.filter.PREVALIDATION_DOCUMENT": gettextCatalog.getString('Pré-validation du document'),
                "docUnit.filter.VALIDATION_DOCUMENT": gettextCatalog.getString('Vérification des pré-rejets/pré-validations'),
                "docUnit.filter.VALIDATION_NOTICES": gettextCatalog.getString('Notices en attente de validation'),
                "docUnit.filter.CLOTURE_DOCUMENT": gettextCatalog.getString('Fin'),

                // Workflows
                "workflow.GENERATION_BORDEREAU": gettextCatalog.getString('Génération du bordereau'),
                "workflow.VALIDATION_CONSTAT_ETAT": gettextCatalog.getString('Constats d\'état à réaliser et valider par la bibliothèque'),
                "workflow.VALIDATION_BORDEREAU_CONSTAT_ETAT": gettextCatalog.getString('Constats d\'état à valider par le prestataire'),
                "workflow.CONSTAT_ETAT_AVANT_NUMERISATION": gettextCatalog.getString('Constats d\'état avant numérisation à réaliser par le prestataire'),
                "workflow.CONSTAT_ETAT_APRES_NUMERISATION": gettextCatalog.getString('Constats d\'état après numérisation à réaliser par le prestataire'),
                "workflow.LIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('Documents en attente de livraison'),
                "workflow.CONTROLES_AUTOMATIQUES_EN_COURS": gettextCatalog.getString('Contrôles automatiques'),
                "workflow.CONTROLE_QUALITE_EN_COURS": gettextCatalog.getString('Documents à contrôler ou en cours de contrôle'),
                "workflow.PREREJET_DOCUMENT": gettextCatalog.getString('Pré-rejet du document'),
                "workflow.PREVALIDATION_DOCUMENT": gettextCatalog.getString('Pré-validation du document'),
                "workflow.VALIDATION_DOCUMENT": gettextCatalog.getString('Vérification des pré-rejets/pré-validations'),
                "workflow.VALIDATION_NOTICES": gettextCatalog.getString('Notices en attente de validation'),
                "workflow.RAPPORT_CONTROLES": gettextCatalog.getString('Rapport de contrôles en attente d\'envoi au prestataire'),
                "workflow.ARCHIVAGE_DOCUMENT": gettextCatalog.getString('Documents en attente d\'archivage CINES'),
                "workflow.DIFFUSION_DOCUMENT": gettextCatalog.getString('Documents en attente de diffusion IA'),
                "workflow.DIFFUSION_DOCUMENT_OMEKA": gettextCatalog.getString('Documents en attente de diffusion Omeka'),
                "workflow.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY": gettextCatalog.getString('Diffusion  du document sur une bibliothèque numérique'),
                "workflow.DIFFUSION_DOCUMENT_LOCALE": gettextCatalog.getString('Documents en attente de diffusion locale'),
                "workflow.INITIALISATION_DOCUMENT": gettextCatalog.getString('Initialisation'),
                "workflow.NUMERISATION_EN_ATTENTE": gettextCatalog.getString('En attente de numérisation'),
                "workflow.RELIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('Documents en attente de relivraison'),
                "workflow.CLOTURE_DOCUMENT": gettextCatalog.getString('Fin'),
                "workflow.status.NOT_STARTED": gettextCatalog.getString('Non démarré'),
                "workflow.status.PENDING": gettextCatalog.getString('En cours'),
                "workflow.status.CANCELED": gettextCatalog.getString('Annulé'),
                "workflow.status.FAILED": gettextCatalog.getString('Echoué'),
                "workflow.status.TO_WAIT": gettextCatalog.getString('Tâche à venir (attente)'),
                "workflow.status.TO_SKIP": gettextCatalog.getString('Tâche à venir (ignore)'),
                "workflow.status.WAITING": gettextCatalog.getString('En attente'),
                "workflow.status.WAITING_NEXT_COMPLETED": gettextCatalog.getString('En attente'),
                "workflow.status.SKIPPED": gettextCatalog.getString('Ignoré'),
                "workflow.status.FINISHED": gettextCatalog.getString('Terminé'),
                "workflow.type.REQUIRED": gettextCatalog.getString('étape requise'),
                "workflow.type.TO_SKIP": gettextCatalog.getString('étape non requise'),
                "workflow.type.TO_WAIT": gettextCatalog.getString('étape en attente'),
                "workflow.type.OTHER": gettextCatalog.getString('étape autre'),
                
                // Workflows Model
                "workflow.model.GENERATION_BORDEREAU": gettextCatalog.getString('Génération du bordereau'),
                "workflow.model.VALIDATION_CONSTAT_ETAT": gettextCatalog.getString('Réalisation et validation du constat d\'état par la bibliothèque'),
                "workflow.model.VALIDATION_BORDEREAU_CONSTAT_ETAT": gettextCatalog.getString('Validation bordereau et constat d\'état par le prestataire'),
                "workflow.model.CONSTAT_ETAT_AVANT_NUMERISATION": gettextCatalog.getString('Réalisation d\'un constat d\'état avant numérisation par le prestataire'),
                "workflow.model.CONSTAT_ETAT_APRES_NUMERISATION": gettextCatalog.getString('Réalisation d\'un constat d\'état après numérisation par le prestataire'),
                "workflow.model.LIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('Livraison des documents numérisés'),
                "workflow.model.CONTROLES_AUTOMATIQUES_EN_COURS": gettextCatalog.getString('Contrôles automatiques'),
                "workflow.model.CONTROLE_QUALITE_EN_COURS": gettextCatalog.getString('Contrôles qualité'),
                "workflow.model.PREREJET_DOCUMENT": gettextCatalog.getString('Pré-rejet du document'),
                "workflow.model.PREVALIDATION_DOCUMENT": gettextCatalog.getString('Pré-validation du document'),
                "workflow.model.VALIDATION_DOCUMENT": gettextCatalog.getString('Validation du document'),
                "workflow.model.VALIDATION_NOTICES": gettextCatalog.getString('Validation de la notice du document'),
                "workflow.model.RAPPORT_CONTROLES": gettextCatalog.getString('Rapport de contrôles pour le prestataire'),
                "workflow.model.ARCHIVAGE_DOCUMENT": gettextCatalog.getString('Archivage du document (export CINES)'),
                "workflow.model.DIFFUSION_DOCUMENT": gettextCatalog.getString('Diffusion IA du document'),
                "workflow.model.DIFFUSION_DOCUMENT_OMEKA": gettextCatalog.getString('Diffusion Omeka du document'),
                "workflow.model.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY": gettextCatalog.getString('Diffusion  du document sur une bibliothèque numérique'),
                "workflow.model.DIFFUSION_DOCUMENT_LOCALE": gettextCatalog.getString('Diffusion locale du document'),
                "workflow.model.INITIALISATION_DOCUMENT": gettextCatalog.getString('Initialisation'),
                "workflow.model.NUMERISATION_EN_ATTENTE": gettextCatalog.getString('En attente de numérisation'),
                "workflow.model.RELIVRAISON_DOCUMENT_EN_COURS": gettextCatalog.getString('En attente de relivraison'),
                "workflow.model.CLOTURE_DOCUMENT": gettextCatalog.getString('Fin'),
                
                // Types de diffusion
                "workflow.type.DIFFUSION_DOCUMENT": gettextCatalog.getString('IA'),
                "workflow.type.DIFFUSION_DOCUMENT_OMEKA": gettextCatalog.getString('Omeka'),
                "workflow.type.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY": gettextCatalog.getString('Diffusion  du document sur une bibliothèque numérique'),
                "workflow.type.DIFFUSION_DOCUMENT_LOCALE": gettextCatalog.getString('locale'),

                // Types de value (LibraryParam)
                "libParam.value.type.RIGHTS_DEFAULT_VALUE": gettextCatalog.getString('Droits'),
                "libParam.value.type.SUBJECT_DEFAULT_VALUE": gettextCatalog.getString('Sujet'),
                "libParam.value.type.CREATOR_DEFAULT_VALUE": gettextCatalog.getString('Créateur'),
                "libParam.value.type.TITLE_DEFAULT_VALUE": gettextCatalog.getString('Titre'),
                "libParam.value.type.FORMAT_DEFAULT_VALUE": gettextCatalog.getString('Format'),
                "libParam.value.type.TYPE_DEFAULT_VALUE": gettextCatalog.getString('Type'),
                "libParam.value.type.DESCRIPTION_DEFAULT_VALUE": gettextCatalog.getString('Description'),
                "libParam.value.type.PUBLISHER_DEFAULT_VALUE": gettextCatalog.getString('Publisher'),
                "libParam.value.type.LANGUAGE_DEFAULT_VALUE": gettextCatalog.getString('Language')

            };
        });
})();
