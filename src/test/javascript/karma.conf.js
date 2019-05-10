// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function (config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '../../',

        // testing framework to use (jasmine/mocha/qunit/...)
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            // bower:js
            'main/webapp/bower_components/modernizr/modernizr.js',
            'main/webapp/bower_components/jquery/dist/jquery.js',
            'main/webapp/bower_components/angular/angular.js',
            'main/webapp/bower_components/angular-animate/angular-animate.js',
            'main/webapp/bower_components/angular-aria/angular-aria.js',
            'main/webapp/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'main/webapp/bower_components/bootstrap/dist/js/bootstrap.js',
            'main/webapp/bower_components/bootstrap-switch/dist/js/bootstrap-switch.js',
            'main/webapp/bower_components/angular-bootstrap-switch/dist/angular-bootstrap-switch.js',
            'main/webapp/bower_components/Chart.js/Chart.js',
            'main/webapp/bower_components/angular-chart.js/dist/angular-chart.js',
            'main/webapp/bower_components/angular-cookies/angular-cookies.js',
            'main/webapp/bower_components/Sortable/Sortable.js',
            'main/webapp/bower_components/Sortable/ng-sortable.js',
            'main/webapp/bower_components/Sortable/knockout-sortable.js',
            'main/webapp/bower_components/Sortable/react-sortable-mixin.js',
            'main/webapp/bower_components/angular-dashboard-framework/dist/angular-dashboard-framework-tpls.js',
            'main/webapp/bower_components/jquery-ui/jquery-ui.js',
            'main/webapp/bower_components/angular-dragdrop/src/angular-dragdrop.js',
            'main/webapp/bower_components/angular-dynamic-locale/src/tmhDynamicLocale.js',
            'main/webapp/bower_components/angular-file-upload/dist/angular-file-upload.min.js',
            'main/webapp/bower_components/angular-gettext/dist/angular-gettext.js',
            'main/webapp/bower_components/jquery-timepicker-jt/jquery.timepicker.js',
            'main/webapp/bower_components/angular-jquery-timepicker/src/timepickerdirective.js',
            'main/webapp/bower_components/angular-local-storage/dist/angular-local-storage.js',
            'main/webapp/bower_components/angular-loading-bar/build/loading-bar.js',
            'main/webapp/bower_components/pdfjs-dist/build/pdf.js',
            'main/webapp/bower_components/pdfjs-dist/build/pdf.worker.js',
            'main/webapp/bower_components/angular-pdf/dist/angular-pdf.js',
            'main/webapp/bower_components/angular-resource/angular-resource.js',
            'main/webapp/bower_components/angular-route/angular-route.js',
            'main/webapp/bower_components/angular-sanitize/angular-sanitize.js',
            'main/webapp/bower_components/angular-smart-table/dist/smart-table.js',
            'main/webapp/bower_components/angular-tree-control/angular-tree-control.js',
            'main/webapp/bower_components/angular-ui-select/dist/select.js',
            'main/webapp/bower_components/angular-xeditable/dist/js/xeditable.js',
            'main/webapp/bower_components/bootstrap-sass-official/assets/javascripts/bootstrap.js',
            'main/webapp/bower_components/json3/lib/json3.js',
            'main/webapp/bower_components/lrInfiniteScroll/index.js',
            'main/webapp/bower_components/moment/moment.js',
            'main/webapp/bower_components/naturalSort.js/dist/naturalSort.js',
            'main/webapp/bower_components/select2/select2.js',
            'main/webapp/bower_components/sockjs-client/dist/sockjs.js',
            'main/webapp/bower_components/stomp-websocket/lib/stomp.min.js',
            'main/webapp/bower_components/underscore/underscore.js',
            'main/webapp/bower_components/angular-svg-round-progressbar/build/roundProgress.js',
            'main/webapp/bower_components/angular-mocks/angular-mocks.js',
            // endbower
            'main/webapp/scripts/app/app.js',
            'main/webapp/scripts/app/**/*.js',
            'main/webapp/scripts/components/**/*.+(js|html)',
            //'test/javascript/spec/helpers/module.js',
            //'test/javascript/spec/helpers/httpBackend.js',
            'test/javascript/**/!(karma.conf).js'
        ],


        // list of files / patterns to exclude
        exclude: [],

        preprocessors: {
            './**/*.js': ['coverage']
        },

        reporters: ['dots', 'jenkins', 'coverage', 'progress'],

        jenkinsReporter: {
            
            outputFile: '../target/test-results/karma/TESTS-results.xml'
        },

        coverageReporter: {
            
            dir: '../target/test-results/coverage',
            reporters: [
                {type: 'lcov', subdir: 'report-lcov'}
            ]
        },

        // web server port
        port: 9876,

        // level of logging
        // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,

        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - PhantomJS
        // - IE (only Windows)
        browsers: ['PhantomJS'],

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false,

        // to avoid DISCONNECTED messages when connecting to slow virtual machines
        browserDisconnectTimeout : 10000, // default 2000
        browserDisconnectTolerance : 1, // default 0
        browserNoActivityTimeout : 4*60*1000 //default 10000
    });
};
