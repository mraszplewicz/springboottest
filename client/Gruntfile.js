module.exports = function(grunt) {

  grunt.initConfig({
      tsd: {
          refresh: {
              options: {
                  command: "reinstall",
                  config: "tsd.json",
              }
          }
      },
      ts: {
        default : {
          src: ["src/main/ts/**/*.ts", "extlibs/ts/**/*.ts"],
          dest: "build/dist/js"
        }
      },
      sass: {
          dist: {
            files: [{
              src: ["src/main/sass/**/*.scss"],
              dest: "build/dist/application.css"
            }]
          }
       },
       bower: {
           install: {
             options: {
               copy: false
             }
           }
       },
       copy: {
         dist: {
           files: [
             {expand: true, cwd: "src/main/webapp", src: "**/*", dest: "build/dist/"},
             {expand: true, cwd: "extlibs/js", src: "**/*", dest: "build/dist/js/libs/"}
           ]
         },
       },
       clean: {
         build: ["build"]
       }
    });

    
    grunt.loadNpmTasks("grunt-ts");
    grunt.loadNpmTasks("grunt-tsd");
    grunt.loadNpmTasks("grunt-sass");
    grunt.loadNpmTasks("grunt-bower-task");
    grunt.loadNpmTasks("grunt-contrib-copy");
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.registerTask("build", ["sass", "tsd", "ts", "bower", "copy"]);
    grunt.registerTask("default", ["build"]);

};