(ns home-sweet-home.test.core
  (:use [home-sweet-home.core])
  (:use [clojure.test])
  (:import [home_sweet_home.core ContactInteractor BlogInteractor
            ClearBlogInteractor SaveArticleInteractor])
  (:import [home_sweet_home.web Presenter]))

(deftype ConsolePresenter []
  Presenter
  (present [this res] res))

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
  (let [impressum (execute (ContactInteractor. (ConsolePresenter.)))]
    (are [key value] (= value (key impressum))
         :name "Jürgen Bickert"
         :street "Grafenspitz 11"
         :city "94099 Ruhstorf"
         :phone "08531/249164"
         :email "juergenbickert@gmail.com")))

(deftest blog-article-not-found
  (is (not (execute (BlogInteractor. -1 (ConsolePresenter.))))))

(deftest blog-articles-different
  (execute (SaveArticleInteractor. title content (ConsolePresenter.)))
  (execute (SaveArticleInteractor. (str title "2") content (ConsolePresenter.)))
  (is (not (= (execute (BlogInteractor. 0 (ConsolePresenter.)))
              (execute (BlogInteractor. 1 (ConsolePresenter.)))))))

(deftest clear-blog
  (is (not (do (execute (SaveArticleInteractor. title content (ConsolePresenter.)))
               (execute (ClearBlogInteractor.))
               (execute (BlogInteractor. 0 (ConsolePresenter.)))))))

(deftest save-new-blog-article
  (execute (ClearBlogInteractor.))
  (is (execute (SaveArticleInteractor. title content (ConsolePresenter.))))
  (let [blog (execute (BlogInteractor. 0 (ConsolePresenter.)))]
    (is (= title (:title blog)))
    (is (= content (:content blog)))))

(deftest clear-blog-saves-backup
  (is (zero? (count @backup)))
  (execute (SaveArticleInteractor. title content (ConsolePresenter.)))
  (execute (ClearBlogInteractor.))
  (is (= 1 (count @backup))))