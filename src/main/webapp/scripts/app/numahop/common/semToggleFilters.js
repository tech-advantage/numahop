(function () {
    'use strict';

    angular
        .module('sem.toggle-filters', [])
        .run(function ($rootScope, $semFilters) {
            $rootScope.$semFilters = $semFilters;

            $rootScope.$on('$viewContentLoaded', function () {
                $('#sem-filters a[data-toggle]').each(function (index, value) {
                    var id = $(value).attr('href');
                    $(id).on('show.bs.collapse', $semFilters.closeItems);
                });
            });
        })
        .provider('$semFilters', function () {
            this.$get = [
                function () {
                    var menuFn = {};

                    menuFn.show = function show() {
                        $("#sem-filters a[data-toggle='dropdown']")
                            .attr('data-toggle', 'collapse')
                            .each(function (index, value) {
                                var id = $(value).attr('href');
                                $(id).removeClass().addClass('nav nav-pills nav-stacked collapse');
                            });

                        var menu = angular.element(document.querySelector('#sem-filters'));
                        menu.addClass('show');
                    };

                    menuFn.hide = function hide() {
                        $("#sem-filters a[data-toggle='collapse']")
                            .attr('data-toggle', 'dropdown')
                            .each(function (index, value) {
                                var id = $(value).attr('href');
                                $(id).removeClass().addClass('dropdown-menu dropright').css('height', 'auto');
                            });

                        var menu = angular.element(document.querySelector('#sem-filters'));
                        menu.removeClass('show');
                        menuFn.closeItems();
                    };

                    menuFn.toggle = function toggle() {
                        if ($('#sem-filters').hasClass('show')) {
                            menuFn.hide();
                        } else {
                            menuFn.show();
                        }
                    };

                    menuFn.closeItems = function closeItems() {
                        $('#sem-filters .in').collapse('hide');
                    };

                    return menuFn;
                },
            ];
        });
})();
