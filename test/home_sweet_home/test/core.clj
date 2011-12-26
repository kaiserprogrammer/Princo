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

(defn save-article-new [title content presenter]
  (save-article {:title title :content content} presenter))

(deftest blog-articles-different
  (save-article-new title content present-return)
  (save-article-new (str title "2") content present-return)
  (is (not (= (get-article 0 present-return)
             (get-article 1 present-return)))))

(deftest save-new-blog-article
  (save-article-new title content present-return)
  (let [blog (get-article 0 present-return)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest listing-all-articles
  (save-article-new (str title "1") content present-return)
  (save-article-new (str title "2") content present-return)
  (save-article-new (str title "3") content present-return)
  (let [listing (list-all-articles present-return)]
    (is (= (:title (listing 0)) "Title1"))
    (is (= (:title (listing 1)) "Title2"))))

(deftest article-update-test
  (save-article-new "wrong" "wrong" present-return)
  (edit-article {:id 0 :title title :content content} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-title
  (save-article-new "Title" "wrong" present-return)
  (edit-article {:id 0 :title "" :content content} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-content
  (save-article-new "wrong" "content" present-return)
  (edit-article {:id 0 :title title :content ""} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-items
  (save-article-new title content present-return)
  (edit-article {:id 0 :title "" :content ""} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest search-for-one-matching-article-test
  (save-article-new "wrong" content present-return)
  (save-article-new title content present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 1 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= title (:title (first articles))))
    (is (= content (:content (first articles))))))

(deftest search-for-two-matching-articles-test
  (save-article-new "wrong" content present-return)
  (save-article-new title content present-return)
  (save-article-new (str title 2) (str content 2) present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))

(deftest search-for-words-matching-title-and-content
  (save-article-new "wrong" "wrong" present-return)
  (save-article-new "wrong" (str content title content) present-return)
  (save-article-new title "wrong" present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))