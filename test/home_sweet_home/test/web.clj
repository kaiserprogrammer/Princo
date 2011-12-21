(ns home-sweet-home.test.web
  (:use [home-sweet-home.web])
  (:use [clojure.test]))

(deftest present-contact-information-test
  (let [text (present-contact-information {:name "name" :city "city" :street "street" :phone "phone" :email "email"})]
    (are [to-find] (re-find (re-pattern to-find) text)
         "name"
         "city"
         "street"
         "phone"
         "email")))

(deftest present-article-test
  (let [text (present-blog {:title "title" :content "content"})]
    (are [to-find] (re-find (re-pattern to-find) text)
         "title"
         "content")))

(deftest present-save-test
  (is (re-find #"error" (present-save {})))
  (is (re-find #"success" (present-save {:success true}))))