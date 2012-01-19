(defproject home-sweet-home "1.0.0-SNAPSHOT"
  :description "Home Sweet home is my homepage"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring/ring "1.0.0"]
                 [hiccup "0.3.8"]
                 [korma "0.2.1"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [clj-stacktrace "0.2.4"]
                 [commons-io/commons-io "2.1"]]
  :dev-dependencies [[clj-webdriver "0.5.0-alpha1"]
                     [ring/ring-devel "1.0.0"]]
  :main home-sweet-home.main)