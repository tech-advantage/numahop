(function () {

    var findElem = function (elem, array) {
        // elem n'est pas un jsonid, on n'a rien à chercher
        if (!elem || !(_.isString(elem) || _.isNumber(elem))) {
            return elem;
        }
        // console.debug("Search for " + elem + " in " + _.pluck(array, "@jsonid"));

        // recherche de elem dans le tableau courant
        var foundElem = _.find(array, function (arrayElem) {
            if (!_.isObject(arrayElem)) {
                return false;
            }
            if (_.has(arrayElem, "@jsonid") && arrayElem["@jsonid"] === elem) {
                return true;
            }
        });
        // elem n'est pas dans le tableau courant: recherche récursive sur les propriétés de chaque élément
        if (_.isUndefined(foundElem)) {
            _.each(array, function (arrayElem) {
                if (_.isUndefined(foundElem)) {
                    _.each(_(arrayElem).keys(), function (key) {
                        if (_.isUndefined(foundElem)) {
                            if (_.has(arrayElem, key)) {
                                if (_.isArray(arrayElem[key])) {
                                    foundElem = findElem(elem, arrayElem[key]);
                                } else if (_.isObject(arrayElem[key])) {
                                    foundElem = findElem(elem, [arrayElem[key]]);
                                }
                            }
                        }
                    });
                }
            });
        }
        // console.debug("Find " + elem + " = " + foundElem);
        return foundElem;
    };
    var findElems = function (elems, array) {
        var foundElems = [];
        _(elems).each(function (elem) {
            var foundElem = findElem(elem, array);
            if (!_.isUndefined(elem)) {
                foundElems.push(foundElem);
            }
        });
        return foundElems;
    };

    function isDefined(value) { return angular.isDefined(value) && value !== null; }

    var setObjectErrors = function (mainObj, referenceObj, timestamp) {
        var fldErrors = "errors";
        var clearErrors = _.isUndefined(referenceObj) || !_.has(referenceObj, fldErrors);
        if (angular.isUndefined(timestamp)) {
            timestamp = Date.now();
        }

        if (mainObj.alreadyCheckedForErrors !== timestamp || (isDefined(referenceObj) && referenceObj.alreadyCheckedForErrors !== timestamp)) {
            mainObj.alreadyCheckedForErrors = timestamp;
            if (isDefined(referenceObj)) {
                referenceObj.alreadyCheckedForErrors = timestamp;
            }

            // set errors on current main object
            if (clearErrors) {
                if (_.has(mainObj, fldErrors)) {
                    delete mainObj[fldErrors];
                }
            } else {
                mainObj[fldErrors] = referenceObj[fldErrors];
            }
            // set errors on children
            _.chain(mainObj).pairs().filter(function (pair) {
                var firstChar = pair[0].charAt(0);
                return firstChar !== "$" && firstChar !== "@" && firstChar !== "_" && pair[0] !== fldErrors;
            }).each(function (pair) {
                var key = pair[0];
                var value = pair[1];
                var refSubObj = referenceObj ? referenceObj[key] : undefined;

                if (_.isArray(value)) {
                    // tri des listes pour que les erreurs soient reportées sur les bons éléments
                    value = _.sortBy(value, "identifier");
                    // éléments mis à jour
                    var sortedRefSubObj = _.chain(value)
                        .pluck("identifier")
                        .filter(function (id) { return !!id; })
                        .map(function (id) {
                            return _.find(refSubObj, function (oth) {
                                return oth.identifier === id;
                            });
                        })
                        .filter(angular.isDefined)
                        .value();
                    // éléments (pas) créés
                    var createdRefSubObj = _.filter(refSubObj, function (refOth) {
                        return !refOth.identifier;
                    });
                    sortedRefSubObj = sortedRefSubObj.concat(createdRefSubObj);

                    _.each(value, function () {
                        setObjectErrors(value, sortedRefSubObj, timestamp);
                    });
                } else if (_.isObject(value)) {
                    setObjectErrors(value, refSubObj, timestamp);
                }
            });
        }

        return mainObj;
    };

    var cleanObjectErrors = function (mainObj) {
        return setObjectErrors(mainObj);
    };

    window.ObjectTools = {
        findElem: findElem,
        findElems: findElems,
        cleanObjectErrors: cleanObjectErrors,
        setObjectErrors: setObjectErrors
    };

    return window.ObjectTools;
}).call(this);
