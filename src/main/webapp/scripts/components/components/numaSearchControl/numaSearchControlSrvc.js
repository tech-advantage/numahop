(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('NumaSearchControlSrvc', NumaSearchControlSrvc);

    function NumaSearchControlSrvc(gettextCatalog) {
        var service = {};

        service.config = {
            operators: [{
                "identifier": "MUST",
                "label": gettextCatalog.getString("Et")
            }, {
                "identifier": "SHOULD",
                "label": gettextCatalog.getString("Ou")
            }, {
                "identifier": "MUST_NOT",
                "label": gettextCatalog.getString("Sauf")
            }],
            group: [{
                "identifier": "general",
                "label": gettextCatalog.getString("Général")
            }, {
                "identifier": "docunit",
                "label": gettextCatalog.getString("Unité documentaire"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "record",
                "label": gettextCatalog.getString("Notices"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "dc",
                "label": gettextCatalog.getString("Propriétés DC"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "dcq",
                "label": gettextCatalog.getString("Propriétés DCQ"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "custom",
                "label": gettextCatalog.getString("Propriétés personalisées"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "physdoc",
                "label": gettextCatalog.getString("Documents physiques"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "cines",
                "label": gettextCatalog.getString("Archivage"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "ia",
                "label": gettextCatalog.getString("Diffusion"),
                "entities": ["DOCUNIT"]
            }, {
                "identifier": "delivery",
                "label": gettextCatalog.getString("Livraison"),
                "entities": ["DELIVERY"]
            }, {
                "identifier": "lot",
                "label": gettextCatalog.getString("Lot"),
                "entities": ["LOT"]
            }, {
                "identifier": "project",
                "label": gettextCatalog.getString("Projet"),
                "entities": ["PROJECT"]
            }, {
                "identifier": "train",
                "label": gettextCatalog.getString("Train"),
                "entities": ["TRAIN"]
            }, {
                "identifier": "rep-description",
                "label": gettextCatalog.getString("Description du document"),
                "entities": ["CONDREPORT"]
            }, {
                "identifier": "rep-numbering",
                "label": gettextCatalog.getString("Numérotation"),
                "entities": ["CONDREPORT"]
            }, {
                "identifier": "rep-binding",
                "label": gettextCatalog.getString("Éat de la reliure"),
                "entities": ["CONDREPORT"]
            }, {
                "identifier": "rep-vigilance",
                "label": gettextCatalog.getString("Points de vigilance"),
                "entities": ["CONDREPORT"]
            }],
            index: {
                "general": [{
                    "identifier": "default",
                    "label": gettextCatalog.getString("Recherche simple")
                }],
                // Unités documentaires
                "docunit": [{
                    "identifier": "docunit-pgcnId",
                    "label": gettextCatalog.getString("Identifiant PGCN")
                }, {
                    "identifier": "docunit-label",
                    "label": gettextCatalog.getString("Libellé")
                }, {
                    "identifier": "docunit-type",
                    "label": gettextCatalog.getString("Type")
                }, {
                    "identifier": "docunit-collectionIA",
                    "label": gettextCatalog.getString("Collection IA"),
                    "type": "select:collectionIA"
                }, {
                    "identifier": "docunit-planClassementPAC",
                    "label": gettextCatalog.getString("Plan de classement PAC"),
                    "type": "select:planClassementPAC"
                }, {
                    "identifier": "docunit-archivable",
                    "label": gettextCatalog.getString("Archivable"),
                    "type": "boolean"
                }, {
                    "identifier": "docunit-distributable",
                    "label": gettextCatalog.getString("Diffusable"),
                    "type": "boolean"
                }, {
                    "identifier": "docunit-rights",
                    "label": gettextCatalog.getString("Droits"),
                    "type": "select:rights"
                }, {
                    "identifier": "docunit-embargo",
                    "label": gettextCatalog.getString("Date d'embargo"),
                    "type": "datepicker"
                }, {
                    "identifier": "docunit-checkDelay",
                    "label": gettextCatalog.getString("Délai avant contrôle (jours)"),
                    "type": "interval"
                }, {
                    "identifier": "docunit-checkEndTime",
                    "label": gettextCatalog.getString("Date de fin de contrôle prévue"),
                    "type": "datepicker"
                }, {
                    "identifier": "docunit-digitalId",
                    "label": gettextCatalog.getString("Radical")
                }, {
                    "identifier": "docunit-library",
                    "label": gettextCatalog.getString("Bibliothèque"),
                    "type": "select:libraries"
                }, {
                    "identifier": "docunit-project",
                    "label": gettextCatalog.getString("Projet"),
                    "type": "uiselect:project"
                }, {
                    "identifier": "docunit-lot",
                    "label": gettextCatalog.getString("Lot"),
                    "type": "uiselect:lot"
                }, {
                    "identifier": "docunit-nbDigitalDocuments",
                    "label": gettextCatalog.getString("Présence de fichiers numériques"),
                    "type": "boolean"
                }, {
                    "identifier": "docunit-workflowState",
                    "label": gettextCatalog.getString("État d'avancement"),
                    "type": "select:workflowState"
                }, {
                    "identifier": "docunit-createdDate",
                    "label": gettextCatalog.getString("Date de création"),
                    "type": "datepicker"
                }, {
                    "identifier": "docunit-lastModifiedDate",
                    "label": gettextCatalog.getString("Date de dernière modification"),
                    "type": "datepicker"
                }, {
                    "identifier": "docunit-latestDeliveryDate",
                    "label": gettextCatalog.getString("Date de livraison"),
                    "type": "datepicker"
                }, {
                    "identifier": "docunit-masterSize",
                    "label": gettextCatalog.getString("Taille des fichiers numériques"),
                    "type": "filesize"
                }],
                "record": [{
                    "identifier": "record-title",
                    "label": gettextCatalog.getString("Titre")
                }],
                // Propriétés des notices
                "dc": [],
                "dcq": [],
                "custom": [],
                // Documents physiques
                "physdoc": [{
                    "identifier": "physdoc-totalPage",
                    "label": gettextCatalog.getString("Nombre de pages"),
                    "type": "interval"
                }],
                // Archivage
                "cines": [{
                    "identifier": "cines-dateSent",
                    "label": gettextCatalog.getString("Date d'archivage"),
                    "type": "datepicker"
                }, {
                    "identifier": "cines-status",
                    "label": gettextCatalog.getString("Statut de l'archivage"),
                    "type": "select:cinesStatus"
                }],
                // Export
                "ia": [{
                    "identifier": "ia-dateSent",
                    "label": gettextCatalog.getString("Date d'export"),
                    "type": "datepicker"
                }, {
                    "identifier": "ia-status",
                    "label": gettextCatalog.getString("Statut de l'export"),
                    "type": "select:iaStatus"
                }],
                // Livraisons
                "delivery": [{
                    "identifier": "delivery-label",
                    "label": gettextCatalog.getString("Libellé")
                }, {
                    "identifier": "delivery-method",
                    "label": gettextCatalog.getString("Mode de livraison"),
                    "type": "select:deliveryMethod"
                }, {
                    "identifier": "delivery-payment",
                    "label": gettextCatalog.getString("Paiement"),
                    "type": "select:deliveryPayment"
                }, {
                    "identifier": "delivery-receptionDate",
                    "label": gettextCatalog.getString("Date de réception"),
                    "type": "datepicker"
                }, {
                    "identifier": "delivery-status",
                    "label": gettextCatalog.getString("Statut"),
                    "type": "select:deliveryStatus"
                }],
                // Lot
                "lot": [{
                    "identifier": "lot-label",
                    "label": gettextCatalog.getString("Libellé")
                }, {
                    "identifier": "lot-provider",
                    "label": gettextCatalog.getString("Prestataire"),
                    "type": "select:provider"
                }, {
                    "identifier": "lot-active",
                    "label": gettextCatalog.getString("Actif"),
                    "type": "boolean"
                }, {
                    "identifier": "lot-status",
                    "label": gettextCatalog.getString("Statut"),
                    "type": "select:lotStatus"
                }, {
                    "identifier": "lot-requiredFormat",
                    "label": gettextCatalog.getString("Format des documents"),
                    "type": "select:fileFormat"
                }],
                // Projet
                "project": [{
                    "identifier": "project-name",
                    "label": gettextCatalog.getString("Nom")
                }, {
                    "identifier": "project-startDate",
                    "label": gettextCatalog.getString("Date de commencement"),
                    "type": "datepicker"
                }, {
                    "identifier": "project-forecastEndDate",
                    "label": gettextCatalog.getString("Date de fin prévue"),
                    "type": "datepicker"
                }, {
                    "identifier": "project-realEndDate",
                    "label": gettextCatalog.getString("Date de fin réelle"),
                    "type": "datepicker"
                }, {
                    "identifier": "project-status",
                    "label": gettextCatalog.getString("Statut"),
                    "type": "select:projectStatus"
                }, {
                    "identifier": "project-active",
                    "label": gettextCatalog.getString("Actif"),
                    "type": "boolean"
                }, {
                    "identifier": "project-associatedLibraries",
                    "label": gettextCatalog.getString("Bibliothèque partenaire"),
                    "type": "select:libraries"
                }],
                // Train
                "train": [{
                    "identifier": "train-label",
                    "label": gettextCatalog.getString("Libellé")
                }, {
                    "identifier": "train-status",
                    "label": gettextCatalog.getString("Statut"),
                    "type": "select:trainStatus"
                }, {
                    "identifier": "train-active",
                    "label": gettextCatalog.getString("Actif"),
                    "type": "boolean"
                }, {
                    "identifier": "train-providerSendingDate",
                    "label": gettextCatalog.getString("Date d'envoi au prestataire"),
                    "type": "datepicker"
                }, {
                    "identifier": "train-returnDate",
                    "label": gettextCatalog.getString("Date de retour"),
                    "type": "datepicker"
                }],
                // Constats d'état
                "rep-description": [],
                "rep-numbering": [],
                "rep-binding": [],
                "rep-vigilance": []
            }
        };
        return service;
    }
})();