(ns home-sweet-home.test.web
  (:use [home-sweet-home.web])
  (:use [clojure.test]))

(deftest present-contact-information-test
  (let [text (present-contact-information {:name "name"
                                           :city "city"
                                           :street "street"
                                           :phone "phone"
                                           :email "email"})]
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

(deftest present-list-all-articles
  (is (re-find #"(?i)no articles" (present-all-articles [])))
  (let [listing (present-all-articles [{:title "Title" :content "blank"}])]
    (is (re-find #"<a href=\"blog\?article=0\">Title</a>" listing)))
  (let [listing (present-all-articles [{:title "Title" :content "blank"}
                                       {:title "Title2" :content "blank"}])]
    (is (re-find #"<a href=\"blog\?article=0\">Title</a>" listing))
    (is (re-find #"<a href=\"blog\?article=1\">Title2</a>" listing))))

(deftest present-index-page-test
  (is (re-find #"(?i)index" (present-index-page {})))
  (is (re-find #"<a href=\"/link1\">Text1</a>" (present-index-page {"Text1" "/link1"})))
  (let [index-page (present-index-page
                    {"Text1" "/link1"
                     "Text2" "/link2"})]
    (is (re-find #"<a href=\"/link1\">Text1</a>" index-page))
    (is (re-find #"<a href=\"/link2\">Text2</a>" index-page))))

(deftest present-edit-article-test
  (let [edit (present-edit-article {:title "Title"
                                    :content "content"})]
    (is (re-find #"<form.*?textarea.*?.*?submit" edit))
    (is (re-find #"input.*?textfield.*?value=\"Title\"" edit))))