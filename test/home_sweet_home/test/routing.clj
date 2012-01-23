(ns home-sweet-home.test.routing
  (:use [home-sweet-home.routing])
  (:use [clojure.test]))

(def test-handler {:interactor :interact
                   :controller :control
                   :presenter :present})


