(defproject om-tutorial "0.1.0-SNAPSHOT"
  :description "My first Om program!"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [cljsjs/react "0.14.0-1"]
                 [cljsjs/react-dom "0.14.0-1" :exclusions [cljsjs/react]]
                 [org.omcljs/om "1.0.0-alpha22" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [sablono "0.4.0" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [datascript "0.13.3"]
                 [rum-reforms "0.4.3"]
                 [figwheel-sidecar "0.5.0-1" :scope "provided"]
                 [cljs-log "0.2.2"]
                 [shodan "0.4.2"]
                 [com.cognitect/transit-cljs "0.8.232"]]

  ;:clean-targets ["target/"]

  :plugins [[lein-cljsbuild "1.1.0"]]

  :clean-targets ^{:protect false} ["target/" "resources/public/js/"]

  ;:source-paths ["src"]


  ;:hooks [leiningen.cljsbuild]
  ;:prep-tasks ["compile" ["cljsbuild" "once"]]

  ;:profiles {:dev {:source-paths ["dev"]
  ;                 :dependencies [;[org.clojure/clojurescript "1.7.48"]
  ;                                ;[thheller/shadow-build "1.0.0-123"]
  ;                                ]}}

  :cljsbuild {
              :builds [

                       ;{:id           "srender"
                       ; :jar true
                       ; :source-paths ["src"]
                       ; ;:resource-paths ["resources" "dev-resources"]
                       ; :main 'om-tutorial.core
                       ; :compiler     {:output-to     "resources/public/js/renderable.js"
                       ;                :output-dir    "resources/public/js/out-server-side"
                       ;                ;:preamble      ["dev-resources/react.min.js"]
                       ;                :pretty-print  true
                       ;                :warnings      true
                       ;                :optimizations :advanced}}

                       {:id           "modules"
                        ;:jar true
                        :source-paths ["src"]
                        :compiler
                                      {:optimizations :none
                                       ;:pretty-print  true
                                       ;:warnings      true
                                       ;:source-map    false
                                       :main       "om-tutorial.core"
                                       :asset-path "js/out"
                                       :output-dir    "resources/public/js/out"
                                       :output-to     "resources/public/main.js"}}]})
                                       ;:modules       {
                                       ;                :cljs-base {
                                       ;                            :output-to "resources/public/js/base.js"
                                       ;                            }
                                       ;                :core
                                       ;                           {:output-to "resources/public/js/core.js"
                                       ;                            :entries   #{"om-tutorial.core"}}
                                       ;                :foo
                                       ;                           {:output-to "resources/public/js/foo.js"
                                       ;                            :entries   #{"om-tutorial.foo"}
                                       ;                            ;:depends-on #{:core}
                                       ;                            }
                                       ;                :render
                                       ;                           {:output-to  "resources/public/js/render.js"
                                       ;                            :entries    #{"om-tutorial.render"}
                                       ;                            :depends-on #{:core :foo}
                                       ;                            }}
