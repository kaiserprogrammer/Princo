(ns home-sweet-home.test.core
  (:use [home-sweet-home.core])
  (:use [home-sweet-home.gateway])
  (:import home_sweet_home.gateway.InMemoryDB)
  (:use [clojure.test]))

(defn present-return [res] res)
(def db (InMemoryDB. (atom [])))

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
  (let [impressum (get-contact-information present-return db)]
    (are [key value] (= value (key impressum))
         :name "Jürgen Bickert"
         :street "Grafenspitz 11"
         :city "94099 Ruhstorf"
         :phone "08531/249164"
         :email "juergenbickert@gmail.com")))

(deftest blog-article-not-found
  (is (:id (get-article -1 present-return db))))

(deftest blog-articles-different
  (save-article {:title title :content content} present-return db)
  (save-article {:title (str title "2")
                 :content content}
                present-return db)
  (is (not (= (get-article 0 present-return db)
             (get-article 1 present-return db)))))

(deftest save-new-blog-article
  (save-article {:title title :content content} present-return db)
  (let [blog (get-article 0 present-return db)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest listing-all-articles
  (save-article {:title (str title "1") :content content} present-return db)
  (save-article {:title (str title "2") :content content} present-return db)
  (save-article {:title (str title "3") :content content} present-return db)
  (let [listing (list-all-articles present-return db)]
    (is (= (:title (listing 0)) "Title1"))
    (is (= (:title (listing 1)) "Title2"))))

(deftest article-update-test
  (save-article {:title "wrong" :content "wrong"} present-return db)
  (edit-article {:id 0 :title title :content content} present-return db)
  (let [article (get-article 0 present-return db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-title
  (save-article {:title "Title" :content "wrong"} present-return db)
  (edit-article {:id 0 :title "" :content content} present-return db)
  (let [article (get-article 0 present-return db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-content
  (save-article {:title "wrong" :content "content"} present-return db)
  (edit-article {:id 0 :title title :content ""} present-return db)
  (let [article (get-article 0 present-return db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-items
  (save-article {:title title :content content} present-return db)
  (edit-article {:id 0 :title "" :content ""} present-return db)
  (let [article (get-article 0 present-return db)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest search-for-one-matching-article-test
  (save-article {:title "wrong" :content content} present-return db)
  (save-article {:title title :content content} present-return db)
  (let [articles (search-for-article title present-return db)]
    (is (= 1 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= title (:title (first articles))))
    (is (= content (:content (first articles))))))

(deftest search-for-two-matching-articles-test
  (save-article {:title "wrong" :content content} present-return db)
  (save-article {:title title :content content} present-return db)
  (save-article {:title (str title 2) :content (str content 2)} present-return db)
  (let [articles (search-for-article title present-return db)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))

(deftest search-for-words-matching-title-and-content
  (save-article {:title "wrong" :content "wrong"} present-return db)
  (save-article {:title "wrong" :content (str content title content)} present-return db)
  (save-article {:title title :content "wrong"} present-return db)
  (let [articles (search-for-article title present-return db)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))