(ns home-sweet-home.test.core
  (:use [home-sweet-home.core])
  (:use [clojure.test]))

(defn present-return [res] res)

(def title "Title")
(def content "content")

(defn clear-db []
  (reset! (:blog db) []))

(defn setup-and-teardown [f]
  (clear-db)
  (f)
  (clear-db))

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

(deftest save-new-blog-article
  (save-article title content present-return)
  (let [blog (get-article 0 present-return)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest listing-all-articles
  (save-article (str title "1") content present-return)
  (save-article (str title "2") content present-return)
  (save-article (str title "3") content present-return)
  (let [listing (list-all-articles present-return)]
    (is (= (:title (listing 0)) "Title1"))
    (is (= (:title (listing 1)) "Title2"))))

(deftest article-update-test
  (save-article "wrong" "wrong" present-return)
  (edit-article 0 title content present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-title
  (save-article "Title" "wrong" present-return)
  (edit-article 0 "" content present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-content
  (save-article "wrong" "content" present-return)
  (edit-article 0 title "" present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-items
  (save-article title content present-return)
  (edit-article 0 "" "" present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))