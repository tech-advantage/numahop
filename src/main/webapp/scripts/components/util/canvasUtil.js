(function () {
    'use strict';

    angular.module('numaHopApp.utils').factory('canvasUtil', canvasUtil);

    function canvasUtil($document, codeSrvc) {
        var stateElements = [];
        initCanvas();

        /**
         * Draws a rounded rectangle using the current state of the canvas.
         * If you omit the last three params, it will draw a rectangle
         * outline with a 5 pixel border radius
         * @param {CanvasRenderingContext2D} ctx
         * @param {Number} x The top left x coordinate
         * @param {Number} y The top left y coordinate
         * @param {Number} width The width of the rectangle
         * @param {Number} height The height of the rectangle
         * @param {Number} [radius = 5] The corner radius; It can also be an object
         *                 to specify different radii for corners
         * @param {Number} [radius.tl = 0] Top left
         * @param {Number} [radius.tr = 0] Top right
         * @param {Number} [radius.br = 0] Bottom right
         * @param {Number} [radius.bl = 0] Bottom left
         * @param {Boolean} [fill = false] Whether to fill the rectangle.
         * @param {Boolean} [stroke = true] Whether to stroke the rectangle.
         */
        var roundRect = function (ctx, x, y, width, height, radius, fill, stroke) {
            if (angular.isUndefined(stroke)) {
                stroke = true;
            }
            if (angular.isUndefined(radius)) {
                radius = 5;
            }
            if (angular.isNumber(radius)) {
                radius = { tl: radius, tr: radius, br: radius, bl: radius };
            } else {
                var defaultRadius = { tl: 0, tr: 0, br: 0, bl: 0 };
                for (var side in defaultRadius) {
                    radius[side] = radius[side] || defaultRadius[side];
                }
            }
            ctx.beginPath();
            ctx.moveTo(x + radius.tl, y);
            ctx.lineTo(x + width - radius.tr, y);
            ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
            ctx.lineTo(x + width, y + height - radius.br);
            ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
            ctx.lineTo(x + radius.bl, y + height);
            ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
            ctx.lineTo(x, y + radius.tl);
            ctx.quadraticCurveTo(x, y, x + radius.tl, y);
            ctx.closePath();
            if (fill) {
                ctx.fill();
            }
            if (stroke) {
                ctx.stroke();
            }
        };

        /**
         * Dessine le workflow
         */
        var drawCanvas = function (canvas) {
            if (stateElements) {
                var context = canvas.getContext('2d');
                stateElements.forEach(function (state) {
                    // Taille apr défaut
                    state.width = 200;
                    switch (state.type) {
                        case 'START':
                            context.fillStyle = '#7CFC00';
                            state.width = 100;
                            break;
                        case 'STATE_OPTIONAL':
                            context.fillStyle = '#7FFFD4';
                            break;
                        case 'STATE_MANDATORY':
                            context.fillStyle = '#1E90FF';
                            break;
                        case 'STATE_WAIT':
                            context.fillStyle = '#C0C0C0';
                            break;
                        case 'STATE_CONDITIONAL':
                            context.fillStyle = '#F4A460';
                            break;
                        case 'END':
                            context.fillStyle = '#F08080';
                            state.width = 100;
                            break;
                    }

                    roundRect(context, state.left, state.top, state.width, state.height, 15, true);
                    //context.fillRect(state.left, state.top, state.width, state.height);
                    if (state.text) {
                        context.fillStyle = 'blue';
                        context.font = '12px Arial';
                        var i = 0;
                        var lines = [];
                        var lineHeightChange = 0;
                        stringDivider(state.text, 33, lines);
                        if (lines.length > 1) {
                            lineHeightChange = 10;
                        }
                        lines.forEach(function (line) {
                            context.fillText(line, state.left + 5 + i * 5, state.top + 30 + i * 15 - lineHeightChange);
                            i++;
                        });
                    }
                });
            }
        };

        function stringDivider(str, width, lines) {
            if (str.length > width) {
                var p = width;
                while (p > 0 && str[p] !== ' ') {
                    p--;
                }
                if (p > 0) {
                    lines.push(str.substring(0, p));
                    var right = str.substring(p + 1);
                    stringDivider(right, width, lines);
                }
            } else {
                lines.push(str);
            }
        }

        /**
         * Initialisation des états
         */
        function initCanvas() {
            stateElements.push({
                key: 'INITIALISATION_DOCUMENT',
                text: '⓪',
                height: 50,
                top: 20,
                left: 350,
                type: 'START',
            });
            stateElements.push({
                key: 'GENERATION_BORDEREAU',
                text: '①',
                height: 50,
                top: 90,
                left: 175,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'VALIDATION_CONSTAT_ETAT',
                text: '②',
                height: 50,
                top: 90,
                left: 425,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'VALIDATION_BORDEREAU_CONSTAT_ETAT',
                text: '③',
                height: 50,
                top: 160,
                left: 300,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'CONSTAT_ETAT_AVANT_NUMERISATION',
                text: '④',
                height: 50,
                top: 230,
                left: 300,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'NUMERISATION_EN_ATTENTE',
                text: '⓪',
                height: 50,
                top: 300,
                left: 300,
                type: 'STATE_WAIT',
            });
            stateElements.push({
                key: 'CONSTAT_ETAT_APRES_NUMERISATION',
                text: '⑤',
                height: 50,
                top: 370,
                left: 300,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'LIVRAISON_DOCUMENT_EN_COURS',
                text: '⑥',
                height: 50,
                top: 440,
                left: 300,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'CONTROLES_AUTOMATIQUES_EN_COURS',
                text: '⑦',
                height: 50,
                top: 510,
                left: 300,
                type: 'STATE_MANDATORY',
            });
            //        	stateElements.push({
            //        		key: 'RELIVRAISON_DOCUMENT_EN_COURS',
            //        		text: "⓪",
            //        		height: 50,
            //        		top: 440,
            //        		left: 550,
            //        		type: 'STATE_OPTIONAL'
            //        	});
            stateElements.push({
                key: 'CONTROLE_QUALITE_EN_COURS',
                text: '⑧',
                height: 50,
                top: 580,
                left: 300,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'PREREJET_DOCUMENT',
                text: '⑩',
                height: 50,
                top: 650,
                left: 300,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'PREVALIDATION_DOCUMENT',
                text: '⑪',
                height: 50,
                top: 650,
                left: 550,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'VALIDATION_REJET',
                text: '⑫',
                height: 50,
                top: 720,
                left: 300,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'VALIDATION_DOCUMENT',
                text: '⑬',
                height: 50,
                top: 720,
                left: 550,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'VALIDATION_NOTICES',
                text: '⑭',
                height: 50,
                top: 790,
                left: 175,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'RAPPORT_CONTROLES',
                text: '⑮',
                height: 50,
                top: 790,
                left: 425,
                type: 'STATE_MANDATORY',
            });
            stateElements.push({
                key: 'ARCHIVAGE_DOCUMENT',
                text: '⑯',
                height: 50,
                top: 860,
                left: 175,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'DIFFUSION_DOCUMENT',
                text: '⑰',
                height: 50,
                top: 860,
                left: 425,
                type: 'STATE_OPTIONAL',
            });
            stateElements.push({
                key: 'CLOTURE_DOCUMENT',
                text: '⓪',
                height: 50,
                top: 930,
                left: 350,
                type: 'END',
            });
            stateElements.forEach(function (element) {
                element.text = element.text + ' ' + codeSrvc['workflow.' + element.key];
            });
        }

        var initListener = function (canvas) {
            if (stateElements) {
                var fiche = $document.getElementById('workflowModelEdit');
                var elemLeft = canvas.offsetLeft + fiche.offsetLeft;
                var elemTop = canvas.offsetTop + fiche.offsetTop;
                canvas.addEventListener(
                    'click',
                    function (event) {
                        var x = event.x - elemLeft,
                            y = event.y - elemTop;
                        //console.log(x, y);
                        stateElements.forEach(function (element) {
                            if (y > element.top && y < element.top + element.height && x > element.left && x < element.left + element.width) {
                                //console.debug('clicked an element : ' + element.key);
                            }
                        });
                    },
                    false
                );
            }
        };

        var getStates = function () {
            return stateElements;
        };

        return {
            roundRect: roundRect,
            initCanvasListener: initListener,
            drawCanvas: drawCanvas,
            getStates: getStates,
        };
    }
})();
