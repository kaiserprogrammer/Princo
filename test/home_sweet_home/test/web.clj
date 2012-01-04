(ns home-sweet-home.test.web
  (:use [home-sweet-home.web])
  (:use [clojure.test]))

(deftest present-contact-information-test
  (let [text (:body (present-contact-information {:name "name"
                                                  :city "city"
                                                  :street "street"
                                                  :phone "phone"
                                                  :email "email"}))]
    (are [to-find] (re-find (re-pattern to-find) text)
         "name"
         "city"
         "street"
         "phone"
         "email")))

(deftest present-article-test
  (let [text (:body (present-blog {:title "title" :content "content"}))]
    (are [to-find] (re-find (re-pattern to-find) text)
         "title"
         "content")))

(deftest present-save-test
  (is (re-find #"error" (:body (present-save {}))))
  (is (re-find #"success" (:body (present-save {:success true})))))

(deftest present-list-all-articles
  (is (re-find #"(?i)no articles" (:body (present-all-articles []))))
  (let [listing (:body
                 (present-all-articles [{:title "Title" :content "blank"}]))]
    (is (re-find #"<a href=\"article\?id=0\">Title</a>" listing)))
  (let [listing (:body
                 (present-all-articles [{:title "Title" :content "blank"}
                                        {:title "Title2" :content "blank"}]))]
    (is (re-find #"<a href=\"article\?id=0\">Title</a>" listing))
    (is (re-find #"<a href=\"article\?id=1\">Title2</a>" listing))))

(deftest present-index-page-test
  (is (re-find #"(?i)index" (:body (present-index-page {}))))
  (is (re-find #"<a href=\"/link1\">Text1</a>" (:body (present-index-page {"Text1" "/link1"}))))
  (let [index-page (:body (present-index-page
                     {"Text1" "/link1"
                      "Text2" "/link2"}))]
    (is (re-find #"<a href=\"/link1\">Text1</a>" index-page))
    (is (re-find #"<a href=\"/link2\">Text2</a>" index-page))))

(deftest present-edit-article-test
  (let [edit (:body (present-edit-article {:id 0
                                     :title "Title"
                                     :content "content"}))]
    (is (re-find #"action=\"/edit\".*?name=\"id\".*?value=\"0\"" edit))
    (is (re-find #"<form.*?textarea.*?.*?submit" edit))
    (is (re-find #"input.*?text.*?value=\"Title\"" edit))))