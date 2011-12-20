(ns home-sweet-home.test.core
  (:use [home-sweet-home.core])
  (:use [clojure.test]))

(defn present-return [res] res)

(def title "Title")
(def content "content")

(defn setup-and-teardown [f]
  (reset! backup [])
  (reset! blog [])
  (f)
  (reset! backup [])
  (reset! blog []))

(use-fixtures :each setup-and-teardown)

(deftest contact-information
  (let [impressum (get-contact-information present-return)]
    (are [key value] (= value (key impressum))
         :name "Jürgen Bickert"
         :street "Grafenspitz 11"
         :city "94099 Ruhstorf"
         :phone "08531/249164"
         :email "juergenbickert@gmail.com")))

(deftest blog-article-not-found
  (is (not (get-article -1 present-return))))

(deftest blog-articles-different
  (save-article title content present-return)
  (save-article (str title "2") content present-return)
  (is (not (= (get-article 0 present-return)
             (get-article 1 present-return)))))

(deftest clear-blog-test
  (is (not (do  (save-article title content present-return)
                (clear-blog)
                (get-article 0 present-return)))))

(deftest save-new-blog-article
  (clear-blog)
  (save-article title content present-return)
  (let [blog (get-article 0 present-return)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest clear-blog-saves-backup
  (is (zero? (count @backup)))
  (save-article title content present-return)
  (clear-blog)
  (is (= 1 (count @backup))))