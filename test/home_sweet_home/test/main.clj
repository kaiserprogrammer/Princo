(ns home-sweet-home.test.main
  (:use [clj-webdriver.core])
  (:use [clojure.test]))

(def b (start :firefox "http://localhost:8080"))

(deftest routing
  (are [title relative-link]
       (re-find (re-pattern title)
                (text
                 (find-it (get-url b (str "http://localhost:8080/"
                                          relative-link)) :title)))
       "Blog" "blog?article=0"
       "Save" "save?title=1&content=2"
       "Impressum" "impressum"
       "Articles" "blog"
       "Index" ""
       "Edit Article" "edit"))

(deftest linking
  (is (= "Impressum"
         (do (-> b
                 (get-url "http://localhost:8080/")
                 (find-it {:text "Impressum"})
                 click)
             (-> b
                 title)))))

(close b)