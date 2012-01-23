(ns home-sweet-home.test.routing
  (:use [home-sweet-home.routing])
  (:use [clojure.test]))

(def test-handler {:interactor :interact
                   :controller :control
                   :presenter :present})

(deftest choosing-handler
  (is (= :test (choose-handler {:uri "uri" :request-method :get} {["uri" :get] :test})))
  (is (= test-handler (choose-handler {:uri "/uri" :request-method :post} {["/uri" :post] test-handler}))))