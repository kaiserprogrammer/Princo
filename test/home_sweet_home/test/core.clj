(ns home-sweet-home.test.core
  (:use [home-sweet-home.core])
  (:use [home-sweet-home.gateway])
  (:import home_sweet_home.gateway.InMemoryDB)
  (:import home_sweet_home.gateway.FileSystemDB)
  (:import [java.io File])
  (:use [clojure.test]))

;; (def db (InMemoryDB. (atom [])))

(def title "Title")
(def content "content")

;; (defn clear-db []
;;   (reset! (:blog db) []))

;; (defn setup-and-teardown [f]
;;   (clear-db)
;;   (f)
;;   (clear-db))

(def db-path (.getCanonicalPath (File. "testdb")))
(def db (FileSystemDB. db-path))
(defn create-dirs []
  (.mkdir (File. db-path)))

(defn delete-dirs []
  (org.apache.commons.io.FileUtils/deleteDirectory (File. db-path)))

(defn setup-and-teardown [f]
  (create-dirs)
  (f)
  (delete-dirs))



(use-fixtures :each setup-and-teardown)

(deftest contact-information
  (let [impressum (get-contact-information db)]
    (are [key value] (= value (key impressum))
         :name "Jürgen Bickert"
         :street "Grafenspitz 11"
         :city "94099 Ruhstorf"
         :phone "08531/249164"
         :email "juergenbickert@gmail.com")))

(deftest blog-article-not-found
  (is (:id (get-article -1 db))))

(deftest blog-articles-different
  (save-article {:title title :content content} db)
  (save-article {:title (str title "2")
                 :content content}
                db)
  (is (not (= (get-article 0 db)
             (get-article 1 db)))))

(deftest save-new-blog-article
  (save-article {:title title :content content} db)
  (let [blog (get-article 0 db)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest listing-all-articles
  (save-article {:title (str title "1") :content content} db)
  (save-article {:title (str title "2") :content content} db)
  (save-article {:title (str title "3") :content content} db)
  (let [listing (list-all-articles db)]
    (is (= (:title (listing 0)) "Title1"))
    (is (= (:title (listing 1)) "Title2"))))

(deftest article-update-test
  (save-article {:title "wrong" :content "wrong"} db)
  (edit-article {:id 0 :title title :content content} db)
  (let [article (get-article 0 db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-title
  (save-article {:title "Title" :content "wrong"} db)
  (edit-article {:id 0 :title "" :content content} db)
  (let [article (get-article 0 db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-content
  (save-article {:title "wrong" :content "content"} db)
  (edit-article {:id 0 :title title :content ""} db)
  (let [article (get-article 0 db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-items
  (save-article {:title title :content content} db)
  (edit-article {:id 0 :title "" :content ""} db)
  (let [article (get-article 0 db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest search-for-one-matching-article-test
  (save-article {:title "wrong" :content content} db)
  (save-article {:title title :content content} db)
  (let [articles (search-for-article title db)]
    (is (= 1 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= title (:title (first articles))))
    (is (= content (:content (first articles))))))

(deftest search-for-two-matching-articles-test
  (save-article {:title "wrong" :content content} db)
  (save-article {:title title :content content} db)
  (save-article {:title (str title 2) :content (str content 2)} db)
  (let [articles (search-for-article title db)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))

(deftest search-for-words-matching-title-and-content
  (save-article {:title "wrong" :content "wrong"} db)
  (save-article {:title "wrong" :content (str content title content)} db)
  (save-article {:title title :content "wrong"} db)
  (let [articles (search-for-article title db)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))