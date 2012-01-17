(ns home-sweet-home.test.gateway
  (:use [home-sweet-home.gateway])
  (:use [clojure.test])
  (:import [java.io File])
  (:import home_sweet_home.gateway.FileSystemDB))

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

(deftest creating-article
  (create-article db "Title" "content")
  (let [contents (slurp (File. db-path "0"))
        c (read-string contents)]
    (is (re-find #"Title" contents))
    (is (= "Title" (:title c)))
    (is (= "content" (:content c))))
  (create-article db "title2" "content2")
  (let [c (read-string (slurp (File. db-path "1")))]
    (is (= "title2" (:title c)))
    (is (= "content2" (:content c)))))

(deftest retrieving-article
  (is (= nil (retrieve-article db 0)))
  (let [contents "{:title \"Title\" :content \"content\"}"]
    (spit (File. db-path "0") contents)
    (let [got (retrieve-article db 0)]
      (is (= "Title" (:title got)))
      (is (= "content" (:content got))))))

(deftest counting-articles
  (is (= (count-articles db) 0))
  (spit (File. db-path "blub") "")
  (is (= (count-articles db) 1))
  (spit (File. db-path "what") "")
  (is (= (count-articles db) 2)))

(deftest updating-article
  (create-article db "Title" "content")
  (update-article db 0 "newtitle" "newcontent")
  (let [got (retrieve-article db 0)]
    (is (= (:title got) "newtitle"))
    (is (= (:content got) "newcontent"))))

(deftest retrieving-all-articles
  (is (= [] (retrieve-all-articles db)))
  (create-article db "title1" "content1")
  (let [res (retrieve-all-articles db)]
    (is (= (:title (res 0)) "title1"))
    (is (= (:content (res 0)) "content1")))
  (create-article db "title2" "content2")
  (let [res (retrieve-all-articles db)]
    (is (= (:title (res 1)) "title2"))
    (is (= (:content (res 1)) "content2"))))