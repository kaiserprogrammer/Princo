(ns home-sweet-home.main
  (:gen-class)
  (:use [home-sweet-home.routing :as routing])
  (:use ring.adapter.jetty)
  (:use [ring.util response])
  (:use [ring.middleware reload stacktrace params])
  (:import home_sweet_home.gateway.FileSystemDB)
  (:import home_sweet_home.gateway.InMemoryDB)
  (:import [java.io File]))

(def db-path (.getCanonicalPath (File. "db")))
(def db (FileSystemDB. db-path))

(defn web-handler [req]
  (let [handler (or (request-handlers (vec (map #(req %)
                                                [:uri :request-method])))
                    (:default request-handlers))]
   (handler-call handler req db)))

(def app
  (-> #'web-handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))

(defn -main [& args]
  (.mkdir (File. "db"))
  (run-jetty app {:port (Integer/parseInt (or (System/getenv "PORT") "8080"))}))