(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('NumaSearchControlCtrl', NumaSearchControlCtrl);

    function NumaSearchControlCtrl(LotSrvc, NumaSearchControlSrvc, ProjectSrvc) {

        var ctrl = this;
        ctrl.clearText = clearText;
        ctrl.initText = initText;
        ctrl.isGroupVisible = isGroupVisible;
        ctrl.getIndex = getIndex;
        ctrl.setGroup = setGroup;
        ctrl.setIndex = setIndex;
        ctrl.setOperator = setOperator;
        ctrl.setText = setText;
        ctrl.setTextInterval = setTextInterval;
        ctrl.setTextObj = setTextObj;

        ctrl.config = angular.copy(NumaSearchControlSrvc.config);


        /**
         * Initialisation du contrôleur
         */
        ctrl.$onInit = function () {
            ctrl.uiconfig = ctrl.controlCtrl.uiconfig;
            ctrl.reportValueConfig = ctrl.controlCtrl.reportValueConfig;
            ctrl._filesize_unit = "1048576";

            initIndexes();

            setOperator(getOrDefault(ctrl.controlModel.operator, ctrl.config.operators, "SHOULD"));
            setIndex(getOrDefault(ctrl.controlModel.index,
                _.chain(ctrl.config.index).values().flatten().value(),
                "default"));
            initTextObj(ctrl.controlModel.text, ctrl._index);
        };

        function initIndexes() {
            // recopie du groupe dans chaque index
            _.each(ctrl.config.index, function (indexes, group) {
                _.each(indexes, function (index) {
                    index.group = group;
                });
            });
            // pptés des notices
            if (ctrl.uiconfig && ctrl.uiconfig.properties) {
                _.chain(ctrl.uiconfig.properties)
                    .sortBy("rank")
                    .map(function (p) {
                        return {
                            "identifier": "property-" + p.identifier,
                            "label": p.label,
                            "group": p.superType.toLowerCase()
                        };
                    })
                    .each(function (index) {
                        if(ctrl.config.index[index.group]) {
                            ctrl.config.index[index.group].push(index);
                        }

                    });
            }
            // pptés des constats d'état
            if (ctrl.uiconfig && ctrl.uiconfig.reportProperties) {
                _.chain(ctrl.uiconfig.reportProperties)
                    .map(function (p) {
                        return {
                            "identifier": "condreport-" + p.identifier,
                            "label": p.label,
                            "group": "rep-" + p.type.toLowerCase(),
                            "type": "uiselect:descriptionValue",
                            "uiselectKey": p.identifier
                        };
                    })
                    .each(function (index) {
                        if(ctrl.config.index[index.group]) {
                            ctrl.config.index[index.group].push(index);
                        }
                    });
            }
        }

        function setOperator(operator) {
            ctrl._operator = operator;
            ctrl.controlModel.operator = ctrl._operator ? ctrl._operator.identifier : null;
        }

        function setGroup(group, event) {
            ctrl._group = group;
            event.stopPropagation();
        }

        function setIndex(index) {
            var oldIndex = ctrl._index;

            ctrl._index = index;
            ctrl.controlModel.index = ctrl._index ? ctrl._index.identifier : null;

            // set group
            if (!ctrl._group || ctrl._group.identifier !== index.group) {
                ctrl._group = getOrDefault(index.group, ctrl.config.group, "general");
            }
            // clear text
            if (angular.isDefined(oldIndex)) {
                delete ctrl._text;
                delete ctrl._text_from;
                delete ctrl._text_to;
                delete ctrl.controlModel.text;
            }
        }

        function getIndex() {
            return ctrl._index;
        }

        function setText(text) {
            ctrl._text = text;
            ctrl.controlModel.text = ctrl._text;
        }

        function setTextObj(text) {
            ctrl._text = text;
            ctrl.controlModel.text = ctrl._text ? ctrl._text.identifier : null;
        }

        function setTextInterval(from, to, coeff) {
            var text;

            if (angular.isDefined(coeff)) {
                text = (from || to) ? (from ? from * coeff : "") + ":" + (to ? to * coeff : "") : "";
            }
            else {
                text = (from || to) ? (from || "") + ":" + (to || "") : "";
            }
            setText(text);
        }

        /**
         * Initialisation du contrôle une fois la liste de valeurs chargées (uiselect)
         * @param {*} values 
         */
        function initText(values) {
            if (ctrl._text && angular.isString(ctrl._text)) {
                ctrl._text = findByIdentifier(ctrl._text, values);
                ctrl.controlModel.text = ctrl._text && ctrl._text.identifier ? ctrl._text.identifier : "";
            }
        }

        /**
         * Initialisation du contrôle en fonction du type de données
         * @param {*} text 
         * @param {*} index 
         */
        function initTextObj(text, index) {
            var textArr;

            if (!text) {
                text = "";
            }
            switch (index.type) {
                case "select:collectionIA":
                    ctrl._text = findByIdentifier(text, ctrl.uiconfig.collectionIA);
                    break;
                case "select:planClassementPAC":
                    ctrl._text = findByIdentifier(text, ctrl.uiconfig.planClassementPAC);
                    break;
                case "uiselect:project":
                    ProjectSrvc.get({ id: text }).$promise.then(setTextObj);
                    break;
                case "uiselect:lot":
                    LotSrvc.get({ id: text }).$promise.then(setTextObj);
                    break;
                case "datepicker":
                case "interval":
                    textArr = text.split(':');
                    ctrl._text_from = textArr.length > 0 ? textArr[0] : "";
                    ctrl._text_to = textArr.length > 1 ? textArr[1] : "";
                    ctrl._text = text;
                    break;
                case "filesize":
                    textArr = text.split(':');
                    ctrl._text_from = textArr.length > 0 ? textArr[0] / ctrl._filesize_unit : "";
                    ctrl._text_to = textArr.length > 1 ? textArr[1] / ctrl._filesize_unit : "";
                    ctrl._text = text;
                    break;
                default:
                    ctrl._text = text;
            }
        }

        function clearText() {
            setText("");
        }

        function getOrDefault(value, list, def) {
            var found = findByIdentifier(value, list);
            if (!found) {
                found = findByIdentifier(def, list);
            }
            return found;
        }

        function findByIdentifier(value, list) {
            return _.find(list, function (elt) {
                return elt.identifier === value;
            });
        }

        /**
         * Le groupe de recherche est-il visible ?
         * @param {*} group 
         */
        function isGroupVisible(group) {
            return !group.entities
                || _.some(group.entities, function (e) {
                    return e === ctrl.target;
                });
        }
    }
})();