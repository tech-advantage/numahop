(function () {
    'use strict';

    angular
        .module('sem.toggle-informations', [])
        .run(function ($rootScope, $semInfos) {
            $rootScope.$semInfos = $semInfos;

            $rootScope.$on('$viewContentLoaded', function () {
                $('#sem-infos a[data-toggle]').each(function (index, value) {
                    var id = $(value).attr('href');
                    $(id).on('show.bs.collapse', $semInfos.closeItems);
                });
            });
        })
        .provider('$semInfos', function () {
            this.$get = [
                function () {
                    var menuFn = {};

                    menuFn.show = function show() {
                        $("#sem-infos a[data-toggle='dropdown']")
                            .attr('data-toggle', 'collapse')
                            .each(function (index, value) {
                                var id = $(value).attr('href');
                                $(id).removeClass().addClass('nav nav-pills nav-stacked collapse');
                            });
                        $('#sem-infos .notif-info-point').addClass('hide');

                        var menu = angular.element(document.querySelector('#sem-infos'));
                        menu.addClass('show');
                    };

                    menuFn.hide = function hide() {
                        $("#sem-infos a[data-toggle='collapse']")
                            .attr('data-toggle', 'dropdown')
                            .each(function (index, value) {
                                var id = $(value).attr('href');
                                $(id).removeClass().addClass('dropdown-menu dropright').css('height', 'auto');
                            });
                        $('#sem-infos .notif-info-point').removeClass('hide');

                        var menu = angular.element(document.querySelector('#sem-infos'));
                        menu.removeClass('show');
                        menuFn.closeItems();
                    };

                    menuFn.toggle = function toggle() {
                        if ($('#sem-infos').hasClass('show')) {
                            menuFn.hide();
                        } else {
                            menuFn.show();
                        }
                    };

                    menuFn.closeItems = function closeItems() {
                        $('#sem-infos .in').collapse('hide');
                    };

                    return menuFn;
                },
            ];
        });
})();
