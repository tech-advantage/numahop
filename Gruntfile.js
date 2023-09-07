// Generated on 2015-06-12 using generator-jhipster 2.16.0
'use strict';
var fs = require('fs');

var parseString = require('xml2js').parseString;
// Returns the second occurence of the version number
var parseVersionFromPomXml = function () {
    var version;
    var pomXml = fs.readFileSync('pom.xml', 'utf8');
    parseString(pomXml, function (err, result) {
        version = result.project.version[0];
    });
    return version;
};

var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;
var serveStatic = require('serve-static');

module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        devUpdate: {
            main: {
                options: {
                    updateType: 'report', // just report outdated packages
                    reportUpdated: false, // don't report up-to-date packages
                },
            },
        },
        checkDependencies: {
            this: {},
        },
        yeoman: {
            app: require('./package.json').appPath || 'app',
            dist: 'src/main/webapp/dist',
            webapp: 'src/main/webapp',
        },
        watch: {
            compass: {
                files: ['src/main/scss/**/*.{scss,sass}'],
                tasks: ['compass:server', 'autoprefixer'],
            },
            styles: {
                files: ['styles/**/*.css'],
                tasks: ['copy:styles', 'autoprefixer'],
            },
            livereload: {
                options: {
                    livereload: 35729,
                    interval: 1000,
                },
                files: [
                    'src/main/webapp/*.html',
                    'src/main/webapp/**/*.html',
                    '.tmp/assets/styles/**/*.css',
                    'src/main/webapp/scripts/**/*.js',
                    'src/main/webapp/scripts/**/**/*.js',
                    'src/main/webapp/assets/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
                ],
            },
        },
        autoprefixer: {
            options: ['last 1 version'],
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/styles/',
                        src: '**/*.css',
                        dest: '.tmp/styles/',
                    },
                ],
            },
        },
        wiredep: {
            app: {
                src: ['src/main/webapp/index.html'],
                exclude: [
                    /angular-i18n/, // localizations are loaded dynamically
                    /swagger-ui/,
                ],
            },
        },
        browserSync: {
            dev: {
                bsFiles: {
                    src: [
                        'src/main/webapp/**/*.html',
                        'src/main/webapp/**/*.json',
                        'src/main/webapp/assets/styles/**/*.css',
                        'src/main/webapp/scripts/**/*.js',
                        'src/main/webapp/assets/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
                        'tmp/**/*.{css,js}',
                    ],
                },
            },
            options: {
                watchTask: true,
                proxy: 'localhost:8080',
            },
        },
        connect: {
            proxies: [
                {
                    context: '/api',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/generated-views',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/websocket',
                    host: 'localhost',
                    ws: true,
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/metrics',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/health',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/configprops',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
                {
                    context: '/dump',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false,
                },
            ],
            options: {
                port: 9000,
                // Change this to 'localhost' to deny access to the server from outside.
                hostname: 'localhost',
                livereload: 35729,
            },
            livereload: {
                options: {
                    open: true,
                    base: ['.tmp', 'src/main/webapp'],
                    middleware: function (connect) {
                        return [proxySnippet, serveStatic('.tmp'), serveStatic(require('path').resolve('src/main/webapp')), serveStatic(require('path').resolve(''))];
                    },
                },
            },
            test: {
                options: {
                    port: 9001,
                    base: ['src/main/webapp/.tmp', 'test', ''],
                },
            },
            dist: {
                options: {
                    base: '<%= yeoman.dist %>',
                    middleware: function (connect) {
                        return [proxySnippet, serveStatic(require('path').resolve('src/main/webapp/dist'))];
                    },
                },
            },
        },
        clean: {
            files: {
                dot: true,
                src: ['.sass-cache', 'src/main/webapp/.tmp', '.tmp', 'src/main/webapp/assets/styles/main.css', '<%= yeoman.dist %>/*', '!<%= yeoman.dist %>/.git*'],
            },
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc',
            },
            all: ['Gruntfile.js', 'scripts/{,*/}*.js'],
        },
        compass: {
            options: {
                sassDir: 'src/main/scss',
                cssDir: '.tmp/assets/styles',
                generatedImagesDir: 'src/main/webapp/assets/images/generated',
                imagesDir: 'images',
                javascriptsDir: 'scripts',
                fontsDir: 'src/main/webapp/assets/fonts',
                httpImagesPath: '/assets/images',
                httpGeneratedImagesPath: '/assets/images/generated',
                httpFontsPath: '/assets/fonts',
                relativeAssets: false,
                raw: 'Sass::Script::Number.precision = 9\n',
                debugInfo: false,
            },
            server: {
                options: {
                    debugInfo: true,
                },
            },
        },
        concat: {
            // src and dest is configured in a subtask called "generated" by usemin
        },
        uglifyjs: {
            // src and dest is configured in a subtask called "generated" by usemin
        },
        useminPrepare: {
            html: 'src/main/webapp/index.html',
            options: {
                dest: '<%= yeoman.dist %>',
            },
        },
        usemin: {
            html: '<%= yeoman.dist %>/**/*.html',
            css: '<%= yeoman.dist %>/assets/styles/*.css',
            js: '<%= yeoman.dist %>/scripts/**/*.js',
            options: {
                assetsDirs: ['<%= yeoman.dist %>', '<%= yeoman.dist %>/assets/images', '<%= yeoman.dist %>/assets/fonts', '<%= yeoman.dist %>/assets/styles'],
                patterns: {
                    js: [
                        [/(assets\/images\/[\w\/\s-]+\.(?:png|jpg|jpeg|gif|webp|svg))/gm, 'Update the JS to reference our revved images'],
                        [/(scripts\/[\w\/\s-]+\.html)/gm, 'Update the JS to reference our revved HTML views'],
                    ],
                    html: [[/(scripts\/[\w\/\s-]+\.html)/gm, 'Update the HTML to reference our revved HTML views']],
                },
            },
        },
        imagemin: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: 'src/main/webapp/assets/images',
                        src: '**/*.{jpg,jpeg}',
                        dest: '<%= yeoman.dist %>/assets/images',
                    },
                ],
            },
        },
        svgmin: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: 'src/main/webapp/assets/images',
                        src: '**/*.svg',
                        dest: '<%= yeoman.dist %>/assets/images',
                    },
                ],
            },
        },
        htmlmin: {
            dist: {
                options: {
                    removeCommentsFromCDATA: true,
                    // https://github.com/yeoman/grunt-usemin/issues/44
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    conservativeCollapse: true,
                    removeAttributeQuotes: false,
                    removeRedundantAttributes: false,
                    useShortDoctype: true,
                    removeEmptyAttributes: true,
                    keepClosingSlash: true,
                },
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.dist %>',
                        src: ['*.html', 'src/main/webapp/*.html', 'src/main/webapp/**/*.html'],
                        dest: '<%= yeoman.dist %>',
                    },
                ],
            },
        },
        cssmin: {
            cms: {
                files: {
                    '<%= yeoman.dist %>/cms/syrtis.min.css': ['.tmp/styles/main_cms.css'],
                },
            },
        },
        ngtemplates: {
            dist: {
                cwd: 'src/main/webapp',
                src: ['scripts/app/**/*.html', 'scripts/components/**/*.html'],
                dest: '.tmp/templates/templates.js',
                options: {
                    module: 'numahopApp',
                    usemin: 'scripts/app.js',
                    htmlmin: {
                        removeCommentsFromCDATA: true,
                        // https://github.com/yeoman/grunt-usemin/issues/44
                        collapseWhitespace: true,
                        collapseBooleanAttributes: true,
                        conservativeCollapse: true,
                        removeAttributeQuotes: true,
                        removeRedundantAttributes: true,
                        useShortDoctype: true,
                        removeEmptyAttributes: true,
                    },
                },
            },
        },
        filerev: {
            files: {
                src: [
                    '<%= yeoman.dist %>/scripts/**/*.js',
                    '<%= yeoman.dist %>/assets/styles/**/*.css',
                    '<%= yeoman.dist %>/assets/fonts/*',
                    '!<%= yeoman.dist %>/assets/styles/font/*',
                    '<%= yeoman.dist %>/assets/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
                    '<%= yeoman.dist %>/assets/styles/**/*.{png,jpg,jpeg,gif,webp,svg}',
                    '<%= yeoman.dist %>/scripts/**/*.html',
                    '!<%= yeoman.dist %>/scripts/app/dashboard/templates/*.html',
                ],
            },
        },
        // Put files not handled in other tasks here
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: 'src/main/webapp',
                        dest: '<%= yeoman.dist %>',
                        src: ['*.html', 'scripts/**/*.html', 'assets/images/**/*.{png,gif,webp,jpg,jpeg,svg}', 'assets/fonts/**/*', 'assets/styles/**/*.css', 'build-date.json'],
                    },
                    {
                        expand: true,
                        cwd: '.tmp/assets/images',
                        dest: '<%= yeoman.dist %>/assets/images',
                        src: ['generated/*'],
                    },
                    {
                        expand: true,
                        cwd: '.tmp/i18n',
                        dest: '<%= yeoman.dist %>/i18n',
                        src: ['**'],
                    },
                    {
                        expand: true,
                        cwd: '.tmp/assets/styles/font',
                        dest: '<%= yeoman.dist %>/assets/styles/font',
                        src: ['**'],
                    },
                    {
                        expand: true,
                        cwd: '.tmp/libs',
                        dest: '<%= yeoman.dist %>/libs',
                        src: ['**'],
                    },
                    {
                        expand: true,
                        cwd: '.tmp/styles',
                        dest: '<%= yeoman.dist %>/styles',
                        src: ['famfamfam-flags.png'],
                    },
                ],
            },
            directives: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '',
                        dest: '<%= yeoman.dist %>/offline',
                        src: [
                            'views/directives/accordion/**/*.html',
                            'views/directives/datepicker/**/*.html',
                            'views/directives/semField/**/*.html',
                            'views/directives/semValue/**/*.html',
                            'views/modals/deleteWarning.html',
                        ],
                    },
                    {
                        expand: true,
                        dot: true,
                        cwd: '',
                        dest: '.tmp/offline',
                        src: [
                            'views/directives/accordion/**/*.html',
                            'views/directives/datepicker/**/*.html',
                            'views/directives/semField/**/*.html',
                            'views/directives/semValue/**/*.html',
                            'views/modals/deleteWarning.html',
                        ],
                    },
                ],
            },
            images: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '',
                        dest: '<%= yeoman.dist %>/offline',
                        src: ['images/Picto32.png', 'images/syrtis-sid-180.png'],
                    },
                    {
                        expand: true,
                        dot: true,
                        cwd: '',
                        dest: '.tmp/offline',
                        src: ['images/Picto32.png', 'images/syrtis-sid-180.png'],
                    },
                ],
            },
            summernote_fonts: {
                expand: true,
                cwd: 'node_modules/summernote/dist/font',
                dest: '.tmp/assets/styles/font',
                src: ['*'],
            },
            i18n: {
                files: [
                    {
                        expand: true,
                        cwd: 'node_modules',
                        dest: '.tmp/i18n/',
                        src: ['angular-i18n/*'],
                    },
                ],
            },
            styles: {
                files: [
                    {
                        expand: true,
                        cwd: 'src/main/webapp',
                        dest: '.tmp/',
                        src: ['assets/styles/**/*.css', 'node_modules/**/*.css'],
                    },
                    {
                        expand: true,
                        cwd: 'node_modules/famfamfam-flags/dist/sprite',
                        dest: '.tmp/styles',
                        src: ['famfamfam-flags.png'],
                    },
                ],
            },
            mediaelement: {
                expand: true,
                cwd: 'node_modules',
                dest: '.tmp/styles/',
                src: ['mediaelement/build/*'],
            },
            libs: {
                expand: true,
                cwd: 'node_modules',
                dest: '.tmp/libs/',
                src: ['pdfjs-dist/build/*', 'angular-pdf/dist/*'],
            },
            mirador: {
                expand: true,
                cwd: 'src/main/webapp/libs',
                dest: '.tmp/libs/',
                src: ['mirador/**'],
            },
        },
        concurrent: {
            server: ['compass:server', 'copy:styles', 'copy:i18n', 'copy:libs', 'copy:mirador', 'copy:summernote_fonts'],
            test: ['copy:libs', 'copy:mirador', 'copy:mediaelement'],
            dist: ['compass', 'imagemin', 'copy:styles', 'copy:i18n', 'copy:libs', 'copy:mirador', 'copy:summernote_fonts'],
        },
        nggettext_extract: {
            pot: {
                options: {
                    extensions: {
                        htm: 'html',
                        html: 'html',
                        js: 'js',
                    },
                },
                files: {
                    'po/template.pot': ['src/main/webapp/*.html', 'src/main/webapp/scripts/{,**/}*.html', 'src/main/webapp/scripts/{,**/}*.js'],
                },
            },
        },
        nggettext_compile: {
            all: {
                options: {
                    format: 'json',
                    module: 'numaHop',
                },
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: 'po',
                        dest: '.tmp/i18n',
                        src: ['*.po'],
                        ext: '.json',
                    },
                ],
            },
        },
        ngAnnotate: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/concat/scripts',
                        src: '*.js',
                        dest: '.tmp/concat/scripts',
                    },
                ],
            },
        },
        babel: {
            options: {
                sourceMap: false,
                presets: ['@babel/preset-env'],
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/concat/scripts',
                        src: ['sem.js', 'epub-viewer.js'],
                        dest: '.tmp/concat/scripts',
                    },
                    {
                        expand: true,
                        cwd: '.tmp/concat/offline/scripts',
                        src: ['sem.js', 'common.js', 'offline.js'],
                        dest: '.tmp/concat/offline/scripts',
                    },
                ],
            },
        },
        replace: {
            date: {
                src: ['<%= yeoman.dist %>/build-date.json', '<%= yeoman.dist %>/scripts/*.js'],
                overwrite: true,
                replacements: [
                    {
                        from: '%build-date%',
                        to: "<%= grunt.template.today('dd/mm/yyyy HH:MM') %>",
                    },
                ],
            },
        },
    });

    grunt.registerTask('server', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['build', 'configureProxies', 'connect:dist:keepalive']);
        } else if (target == 'fast') {
            grunt.task.run(['checkDependencies', 'compass:server', 'autoprefixer', 'configureProxies', 'connect:livereload', 'watch']);
        } else {
            grunt.task.run(['checkDependencies', 'clean', 'nggettext_compile', 'concurrent:server', 'autoprefixer', 'copy:directives', 'configureProxies', 'connect:livereload', 'watch']);
        }
    });

    grunt.registerTask('build', ['clean', 'buildWithoutClean']);

    grunt.registerTask('buildWithoutClean', [
        'checkDependencies',
        'useminPrepare',
        'nggettext_compile',
        'concurrent:dist',
        'autoprefixer',
        'concat:generated',
        'babel',
        'copy:dist',
        'copy:directives',
        'copy:images',
        'ngAnnotate',
        'cssmin:generated',
        'uglify:generated',
        'filerev',
        'usemin',
        'htmlmin',
        'replace:date',
    ]);

    grunt.registerTask('default', ['clean', 'test', 'buildWithoutClean']);

    grunt.registerTask('serve', ['clean:server', 'wiredep', 'ngconstant:dev', 'browserSync', 'watch']);
};
