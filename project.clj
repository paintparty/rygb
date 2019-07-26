(defproject rygb-demo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.8"]
                 [garden "1.3.9"]
                 [ns-tracker "0.4.0"]
                 [rygb "0.1.0-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-garden "0.2.8"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "resources/public/css"
                                    "docs/css"
                                    "docs/js/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj" "src/cljc"]
                     :stylesheet   rygb-demo.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}
                    {:id           "min"
                     :source-paths ["src/clj" "src/cljc"]
                     :stylesheet   rygb-demo.css/screen
                     :compiler     {:output-to     "docs/css/screen.css"
                                    :pretty-print? true}}]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]
    :plugins      [[lein-figwheel "0.5.18"]]}
   :prod {}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src/cljc"]
     :figwheel     {:on-jsload "rygb-demo.core/mount-root"
                    :websocket-host "localhost"}
     :compiler     {:main                 rygb-demo.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}

    {:id           "min"
     :source-paths ["src/cljs" "src/cljc"]
     :compiler     {:main            rygb-demo.core
                    :output-to       "docs/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})