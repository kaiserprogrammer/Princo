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
  (is (:id (get-article -1 present-return))))

(deftest blog-articles-different
  (save-article {:title title :content content} present-return)
  (save-article {:title (str title "2")
                 :content content}
                present-return)
  (is (not (= (get-article 0 present-return)
             (get-article 1 present-return)))))

(deftest save-new-blog-article
  (save-article {:title title :content content} present-return)
  (let [blog (get-article 0 present-return)]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest listing-all-articles
  (save-article {:title (str title "1") :content content} present-return)
  (save-article {:title (str title "2") :content content} present-return)
  (save-article {:title (str title "3") :content content} present-return)
  (let [listing (list-all-articles present-return)]
    (is (= (:title (listing 0)) "Title1"))
    (is (= (:title (listing 1)) "Title2"))))

(deftest article-update-test
  (save-article {:title "wrong" :content "wrong"} present-return)
  (edit-article {:id 0 :title title :content content} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-title
  (save-article {:title "Title" :content "wrong"} present-return)
  (edit-article {:id 0 :title "" :content content} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-content
  (save-article {:title "wrong" :content "content"} present-return)
  (edit-article {:id 0 :title title :content ""} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest article-update-test-with-missing-items
  (save-article {:title title :content content} present-return)
  (edit-article {:id 0 :title "" :content ""} present-return)
  (let [article (get-article 0 present-return)]
    (is (= (:title article) title))
    (is (= (:content article) content))))

(deftest search-for-one-matching-article-test
  (save-article {:title "wrong" :content content} present-return)
  (save-article {:title title :content content} present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 1 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= title (:title (first articles))))
    (is (= content (:content (first articles))))))

(deftest search-for-two-matching-articles-test
  (save-article {:title "wrong" :content content} present-return)
  (save-article {:title title :content content} present-return)
  (save-article {:title (str title 2) :content (str content 2)} present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))

(deftest search-for-words-matching-title-and-content
  (save-article {:title "wrong" :content "wrong"} present-return)
  (save-article {:title "wrong" :content (str content title content)} present-return)
  (save-article {:title title :content "wrong"} present-return)
  (let [articles (search-for-article title present-return)]
    (is (= 2 (count articles)))
    (is (= 1 (:article-id (first articles))))
    (is (= 2 (:article-id (second articles))))))